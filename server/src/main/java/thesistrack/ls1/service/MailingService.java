package thesistrack.ls1.service;

import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import thesistrack.ls1.entity.Application;
import thesistrack.ls1.entity.User;
import thesistrack.ls1.exception.MailingException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class MailingService {
    private final JavaMailSender javaMailSender;
    private final UploadService uploadService;

    private final boolean enabled;
    private final String mailFooter;
    private final String workspaceUrl;
    private final String sender;
    private final List<String> chairMemberRecipientsList;
    private final Path rootLocation;

    @Autowired
    public MailingService(
            JavaMailSender javaMailSender,
            UploadService uploadService,
            @Value("${thesis-track.mail.enabled}") boolean enabled,
            @Value("${thesis-track.mail.sender}") String sender,
            @Value("${thesis-track.mail.chair-member-recipients}") String chairMemberRecipientsList,
            @Value("${thesis-track.mail.footer}") String mailFooter,
            @Value("${thesis-track.mail.workspace-url}") String workspaceUrl
    ) {
        this.javaMailSender = javaMailSender;
        this.uploadService = uploadService;

        this.enabled = enabled;
        this.sender = sender;
        this.workspaceUrl = workspaceUrl;
        this.mailFooter = mailFooter;
        this.rootLocation = Paths.get("mails");

        if (chairMemberRecipientsList != null && !chairMemberRecipientsList.isEmpty()) {
            this.chairMemberRecipientsList = Arrays.asList(chairMemberRecipientsList.split(";"));
            this.chairMemberRecipientsList.removeIf(String::isEmpty);
        } else {
            this.chairMemberRecipientsList = new ArrayList<>();
        }
    }

    public void sendApplicationCreatedMailToChair(Application application) throws MailingException {
        if (!enabled) {
            return;
        }

        if (chairMemberRecipientsList.isEmpty()) {
            return;
        }

        try {
            MimeMessage message = javaMailSender.createMimeMessage();

            message.setFrom(sender);

            for (String recipient : chairMemberRecipientsList) {
                message.addRecipients(MimeMessage.RecipientType.TO, recipient);
            }

            message.setSubject("New Thesis Application");

            String template = getMailTemplate("application-created-chair");
            template = fillUserPlaceholders(template, "student", application.getUser());
            template = fillApplicationPlaceholders(template, application);

            message.setContent(createApplicationFilesMailContent(template, application));

            javaMailSender.send(message);
        } catch (MessagingException | IOException e) {
            throw new MailingException("Failed to send email", e);
        }
    }

    public void sendApplicationCreatedMailToStudent(Application application) throws MailingException {
        if (!enabled) {
            return;
        }

        try {
            MimeMessage message = javaMailSender.createMimeMessage();

            message.setSubject("Thesis Application Confirmation");

            message.setFrom(sender);
            message.setRecipients(MimeMessage.RecipientType.TO, application.getUser().getEmail());

            String template = getMailTemplate("application-created-student");
            template = fillUserPlaceholders(template, "student", application.getUser());
            template = fillApplicationPlaceholders(template, application);

            message.setContent(createApplicationFilesMailContent(template, application));

            javaMailSender.send(message);
        } catch (MessagingException | IOException e) {
            throw new MailingException("Failed to send email", e);
        }
    }

    public void sendApplicationAcceptanceEmail(Application application, User advisor) throws MailingException {
        if (!enabled) {
            return;
        }

        try {
            MimeMessage message = javaMailSender.createMimeMessage();

            message.setSubject("Thesis Application Acceptance");

            message.setFrom(sender);
            message.setRecipients(MimeMessage.RecipientType.TO, application.getUser().getEmail());
            message.addRecipients(MimeMessage.RecipientType.CC, advisor.getEmail());

            String template = getMailTemplate("application-acceptance");
            template = fillUserPlaceholders(template, "advisor", advisor);
            template = fillUserPlaceholders(template, "student", application.getUser());
            template = fillApplicationPlaceholders(template, application);
            message.setContent(template, "text/html; charset=utf-8");

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new MailingException("Failed to send email", e);
        }
    }

    public void sendApplicationRejectionEmail(Application application) throws MailingException {
        if (!enabled) {
            return;
        }

        try {
            MimeMessage message = javaMailSender.createMimeMessage();

            message.setSubject("Thesis Application Rejection");

            message.setFrom(sender);
            message.setRecipients(MimeMessage.RecipientType.TO, application.getUser().getEmail());

            String content = getMailTemplate("application-rejection");
            content = fillUserPlaceholders(content, "student", application.getUser());
            content = fillApplicationPlaceholders(content, application);

            message.setContent(content, "text/html; charset=utf-8");

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new MailingException("Failed to send email", e);
        }
    }

    private String getMailTemplate(String name) throws MailingException {
        Path filePath = rootLocation.resolve(name + ".html");

        try {
            byte[] fileBytes = Files.readAllBytes(filePath);

            String template = new String(fileBytes, StandardCharsets.UTF_8);

            return template
                    .replace("{{config.footer}}", mailFooter)
                    .replace("{{config.workspaceUrl}}", workspaceUrl);
        } catch (IOException e) {
            throw new MailingException("Mail template not found", e);
        }
    }

    private String fillUserPlaceholders(String template, String placeholder, User user) {
        return template
                .replace("{{" + placeholder + ".firstName}}", user.getFirstName())
                .replace("{{" + placeholder + ".lastName}}", user.getLastName())
                .replace("{{" + placeholder + ".email}}", user.getEmail())
                .replace("{{" + placeholder + ".tumId}}", user.getUniversityId())
                .replace("{{" + placeholder + ".matriculationNumber}}", user.getMatriculationNumber())
                .replace("{{" + placeholder + ".gender}}", user.getGender())
                .replace("{{" + placeholder + ".nationality}}", user.getNationality())
                .replace("{{" + placeholder + ".isExchangeStudent}}", user.getIsExchangeStudent().toString());
    }

    private String fillApplicationPlaceholders(String template, Application application) {
        String pattern = "dd. MMM yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        User student = application.getUser();

        return template.replace("{{application.studyProgram}}", student.getStudyProgram())
                .replace("{{application.studyDegree}}", student.getStudyDegree())
                .replace("{{application.enrolledAt}}", simpleDateFormat.format(student.getEnrolledAt()))
                .replace("{{application.desiredThesisStart}}", simpleDateFormat.format(application.getDesiredStartDate()))
                .replace("{{application.specialSkills}}", student.getSpecialSkills())
                .replace("{{application.motivation}}", application.getMotivation())
                .replace("{{application.interests}}", student.getInterests())
                .replace("{{application.projects}}", student.getProjects())
                .replace("{{application.specialSkills}}", student.getSpecialSkills())
                .replace("{{application.thesisTitle}}", application.getThesisTitle())
                .replace("{{application.researchAreas}}", String.join(", ", student.getResearchAreas()))
                .replace("{{application.focusTopics}}", String.join(", ", student.getFocusTopics()));
    }

    private Multipart createApplicationFilesMailContent(String text, Application application) throws MessagingException, IOException {
        Multipart multipart = new MimeMultipart();

        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(text, "text/html; charset=utf-8");
        multipart.addBodyPart(messageBodyPart);

        MimeBodyPart examinationReportAttachment = new MimeBodyPart();
        examinationReportAttachment.attachFile(uploadService.load(application.getUser().getExaminationFilename()).getFile());
        multipart.addBodyPart(examinationReportAttachment);

        MimeBodyPart cvAttachment = new MimeBodyPart();
        cvAttachment.attachFile(uploadService.load(application.getUser().getCvFilename()).getFile());
        multipart.addBodyPart(cvAttachment);

        String bachelorReportFilename = application.getUser().getDegreeFilename();

        if (bachelorReportFilename != null && !bachelorReportFilename.isBlank()) {
            MimeBodyPart bachelorReportAttachment = new MimeBodyPart();
            bachelorReportAttachment.attachFile(uploadService.load(bachelorReportFilename).getFile());
            multipart.addBodyPart(bachelorReportAttachment);
        }

        return multipart;
    }
}
