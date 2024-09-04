package thesistrack.ls1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import thesistrack.ls1.constants.ApplicationRejectReason;
import thesistrack.ls1.controller.payload.LegacyCreateApplicationPayload;
import thesistrack.ls1.entity.*;
import thesistrack.ls1.constants.ApplicationState;
import thesistrack.ls1.exception.request.ResourceInvalidParametersException;
import thesistrack.ls1.exception.request.ResourceNotFoundException;
import thesistrack.ls1.repository.ApplicationRepository;
import thesistrack.ls1.repository.TopicRepository;
import thesistrack.ls1.repository.UserRepository;
import thesistrack.ls1.utility.RequestValidator;

import java.time.Instant;
import java.util.*;

@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final UploadService uploadService;
    private final MailingService mailingService;
    private final TopicRepository topicRepository;
    private final ThesisService thesisService;
    private final TopicService topicService;

    @Autowired
    public ApplicationService(
            ApplicationRepository applicationRepository,
            UserRepository userRepository,
            UploadService storageService,
            MailingService mailingService,
            TopicRepository topicRepository,
            ThesisService thesisService,
            TopicService topicService
    ) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;

        this.uploadService = storageService;
        this.mailingService = mailingService;
        this.topicRepository = topicRepository;
        this.thesisService = thesisService;
        this.topicService = topicService;
    }

    public Page<Application> getAll(
            UUID userId,
            String searchQuery,
            ApplicationState[] states,
            String[] previous,
            String[] topics,
            String[] types,
            boolean includeSuggestedTopics,
            int page,
            int limit,
            String sortBy,
            String sortOrder
    ) {
        Sort.Order order = new Sort.Order(sortOrder.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);

        String searchQueryFilter = searchQuery == null || searchQuery.isEmpty() ? null : searchQuery.toLowerCase();
        Set<ApplicationState> statesFilter = states == null || states.length == 0 ? null : new HashSet<>(Arrays.asList(states));
        Set<String> topicsFilter = topics == null || topics.length == 0 ? null : new HashSet<>(Arrays.asList(topics));
        Set<String> typesFilter = types == null || types.length == 0 ? null : new HashSet<>(Arrays.asList(types));
        Set<String> previousFilter = previous == null || previous.length == 0 ? null : new HashSet<>(Arrays.asList(previous));

        return applicationRepository.searchApplications(
                userId,
                searchQueryFilter,
                statesFilter,
                previousFilter,
                topicsFilter,
                typesFilter,
                includeSuggestedTopics,
                PageRequest.of(page, limit, Sort.by(order))
        );
    }

    @Transactional
    public Application createLegacyApplication(
            LegacyCreateApplicationPayload payload,
            MultipartFile examinationReport,
            MultipartFile cv,
            MultipartFile degreeReport
    ) {
        Instant currentTime = Instant.now();

        User student = userRepository.findByUniversityId(payload.universityId()).orElseGet(() -> {
            User user = new User();

            user.setJoinedAt(currentTime);

            return user;
        });

        student.setUniversityId(RequestValidator.validateStringMaxLength(payload.universityId(), 30));
        student.setMatriculationNumber(RequestValidator.validateStringMaxLength(payload.matriculationNumber(), 30));
        student.setFirstName(RequestValidator.validateStringMaxLength(payload.firstName(), 100));
        student.setLastName(RequestValidator.validateStringMaxLength(payload.lastName(), 100));
        student.setGender(RequestValidator.validateStringMaxLength(payload.gender(), 100));
        student.setNationality(RequestValidator.validateStringMaxLength(payload.nationality(), 100));
        student.setEmail(RequestValidator.validateEmail(payload.email()));
        student.setStudyDegree(RequestValidator.validateStringMaxLength(payload.studyDegree(), 1000));
        student.setStudyProgram(RequestValidator.validateStringMaxLength(payload.studyProgram(), 1000));
        student.setSpecialSkills(RequestValidator.validateStringMaxLength(payload.specialSkills(), 1000));
        student.setInterests(RequestValidator.validateStringMaxLength(payload.interests(), 1000));
        student.setProjects(RequestValidator.validateStringMaxLength(payload.projects(), 1000));

        student.setEnrolledAt(payload.enrolledAt());
        student.setUpdatedAt(currentTime);

        student.setExaminationFilename(uploadService.store(examinationReport, 3 * 1024 * 1024));
        student.setCvFilename(uploadService.store(cv, 3 * 1024 * 1024));

        if (degreeReport != null && !degreeReport.isEmpty()) {
            student.setDegreeFilename(uploadService.store(degreeReport, 3 * 1024 * 1024));
        }

        Application application = new Application();
        application.setUser(userRepository.save(student));

        application.setThesisTitle(RequestValidator.validateStringMaxLength(payload.thesisTitle(), 500));
        application.setThesisType(RequestValidator.validateStringMaxLength(payload.thesisType(), 500));
        application.setMotivation(RequestValidator.validateStringMaxLength(payload.motivation(), 1000));
        application.setComment("");
        application.setState(ApplicationState.NOT_ASSESSED);
        application.setDesiredStartDate(payload.desiredStartDate());
        application.setCreatedAt(currentTime);

        application = applicationRepository.save(application);

        mailingService.sendApplicationCreatedEmail(application);

        return application;
    }

    @Transactional
    public Application createApplication(User user, UUID topicId, String thesisTitle, String thesisType, Instant desiredStartDate, String motivation) {
        Topic topic = topicId == null ? null : topicService.findById(topicId);

        if (topic != null && topic.getClosedAt() != null) {
            throw new ResourceInvalidParametersException("This topic is already closed. You cannot submit new applications for it.");
        }

        Application application = new Application();
        application.setUser(user);

        application.setTopic(topic);
        application.setThesisTitle(thesisTitle);
        application.setThesisType(thesisType);
        application.setMotivation(motivation);
        application.setComment("");
        application.setState(ApplicationState.NOT_ASSESSED);
        application.setDesiredStartDate(desiredStartDate);
        application.setCreatedAt(Instant.now());

        application = applicationRepository.save(application);

        mailingService.sendApplicationCreatedEmail(application);

        return application;
    }

    @Transactional
    public Application updateApplication(Application application, UUID topicId, String thesisTitle, String thesisType, Instant desiredStartDate, String motivation) {
        application.setTopic(topicId == null ? null : topicService.findById(topicId));
        application.setThesisTitle(thesisTitle);
        application.setThesisType(thesisType);
        application.setMotivation(motivation);
        application.setDesiredStartDate(desiredStartDate);

        application = applicationRepository.save(application);

        return application;
    }

    @Transactional
    public List<Application> accept(
            User reviewingUser,
            Application application,
            String thesisTitle,
            String thesisType,
            List<UUID> advisorIds,
            List<UUID> supervisorIds,
            boolean notifyUser,
            boolean closeTopic
    ) {
        List<Application> result = new ArrayList<>();

        application.setState(ApplicationState.ACCEPTED);
        application.setReviewedAt(Instant.now());
        application.setReviewedBy(reviewingUser);

        Thesis thesis = thesisService.createThesis(
                reviewingUser,
                thesisTitle,
                thesisType,
                supervisorIds,
                advisorIds,
                List.of(application.getUser().getId()),
                application,
                false
        );

        application = applicationRepository.save(application);

        Topic topic = application.getTopic();

        if (topic != null && closeTopic) {
            topic.setClosedAt(Instant.now());

            result.addAll(rejectApplicationsForTopic(reviewingUser, topic, ApplicationRejectReason.TOPIC_FILLED, true));

            application.setTopic(topicRepository.save(topic));
        }

        if (notifyUser) {
            mailingService.sendApplicationAcceptanceEmail(application, thesis);
        }

        result.add(applicationRepository.save(application));

        return result;
    }

    @Transactional
    public List<Application> reject(User reviewingUser, Application application, ApplicationRejectReason reason, boolean notifyUser) {
        application.setState(ApplicationState.REJECTED);
        application.setRejectReason(reason);
        application.setReviewedAt(Instant.now());
        application.setReviewedBy(reviewingUser);

        List<Application> result = new ArrayList<>();

        if (reason == ApplicationRejectReason.FAILED_STUDENT_REQUIREMENTS) {
            List<Application> applications = applicationRepository.findAllByUser(application.getUser());

            for (Application item : applications) {
                if (item.getState() == ApplicationState.NOT_ASSESSED) {
                    item.setState(ApplicationState.REJECTED);
                    item.setRejectReason(reason);
                    item.setReviewedAt(Instant.now());
                    item.setReviewedBy(reviewingUser);

                    result.add(applicationRepository.save(item));
                }
            }
        }

        if (notifyUser) {
            mailingService.sendApplicationRejectionEmail(application, reason);
        }

        result.add(applicationRepository.save(application));

        return result;
    }

    @Transactional
    public Topic closeTopic(User closer, Topic topic, ApplicationRejectReason reason, boolean notifyUser) {
        topic.setClosedAt(Instant.now());

        rejectApplicationsForTopic(closer, topic, reason, notifyUser);

        return topicRepository.save(topic);
    }

    @Transactional
    public List<Application> rejectApplicationsForTopic(User closer, Topic topic, ApplicationRejectReason reason, boolean notifyUser) {
        List<Application> applications = applicationRepository.findAllByTopic(topic);
        List<Application> result = new ArrayList<>();

        for (Application application : applications) {
            if (application.getState() != ApplicationState.NOT_ASSESSED) {
                continue;
            }

            result.addAll(reject(closer, application, reason, notifyUser));
        }

        return result;
    }

    @Transactional
    public Application updateComment(Application application, String comment) {
        application.setComment(comment);

        return applicationRepository.save(application);
    }

    public boolean applicationExists(User user, UUID topicId) {
        return applicationRepository.existsPendingApplication(user.getId(), topicId);
    }

    public Application findById(UUID applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Application with id %s not found.", applicationId)));
    }
}
