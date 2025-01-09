package thesismanagement.ls1.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import thesismanagement.ls1.constants.ThesisPresentationState;
import thesismanagement.ls1.constants.ThesisPresentationVisibility;
import thesismanagement.ls1.constants.ThesisRoleName;
import thesismanagement.ls1.constants.ThesisState;
import thesismanagement.ls1.dto.TaskDto;
import thesismanagement.ls1.entity.Thesis;
import thesismanagement.ls1.entity.ThesisPresentation;
import thesismanagement.ls1.entity.User;
import thesismanagement.ls1.repository.ApplicationRepository;
import thesismanagement.ls1.repository.ThesisPresentationRepository;
import thesismanagement.ls1.repository.ThesisRepository;
import thesismanagement.ls1.repository.TopicRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
public class DashboardService {
    private final ThesisRepository thesisRepository;
    private final ApplicationRepository applicationRepository;
    private final TopicRepository topicRepository;
    private final String scientificWritingGuide;

    public DashboardService(
            ThesisRepository thesisRepository,
            ApplicationRepository applicationRepository,
            TopicRepository topicRepository,
            @Value("${thesis-management.scientific-writing-guide}") String scientificWritingGuide
    ) {
        this.thesisRepository = thesisRepository;
        this.applicationRepository = applicationRepository;
        this.topicRepository = topicRepository;
        this.scientificWritingGuide = scientificWritingGuide;
    }

    public List<TaskDto> getTasks(User user) {
        List<TaskDto> tasks = new ArrayList<>();

        if (user.hasAnyGroup("student") && !scientificWritingGuide.isBlank()) {
            tasks.add(new TaskDto(
                    "Please make yourself familiar with scientific writing",
                    scientificWritingGuide,
                    50
            ));
        }

        // general student tasks
        for (Thesis thesis : thesisRepository.findActiveThesesForRole(user.getId(), Set.of(ThesisRoleName.STUDENT), null)) {
            if (thesis.getAbstractField().isBlank() || thesis.getInfo().isBlank()) {
                tasks.add(new TaskDto(
                        "Add the abstract and additional information to thesis \"" + thesis.getTitle() + "\"",
                        getThesisLink(thesis),
                        50
                ));
            }
        }

        // general advisor, supervisor tasks
        for (Thesis thesis : thesisRepository.findActiveThesesForRole(user.getId(), Set.of(ThesisRoleName.ADVISOR), null)) {
            if (thesis.getState().equals(ThesisState.PROPOSAL)) {
                continue;
            }

            if (thesis.getStartDate() == null || thesis.getEndDate() == null) {
                tasks.add(new TaskDto(
                        "Add start and end date to thesis \"" + thesis.getTitle() + "\"",
                        getThesisLink(thesis),
                        50
                ));
            }

            for (ThesisPresentation presentation : thesis.getPresentations()) {
                if (presentation.getState() == ThesisPresentationState.DRAFTED) {
                    tasks.add(new TaskDto(
                            "There is a presentation draft for thesis \"" + thesis.getTitle() + "\". Please review and confirm it.",
                            getThesisLink(thesis),
                            80
                    ));
                }
            }
        }

        // proposal task
        for (Thesis thesis : thesisRepository.findActiveThesesForRole(user.getId(), Set.of(ThesisRoleName.STUDENT), Set.of(ThesisState.PROPOSAL))) {
            if (!thesis.getProposals().isEmpty()) {
                continue;
            }

            tasks.add(new TaskDto(
                    "Add a proposal to thesis \"" + thesis.getTitle() + "\"",
                    getThesisLink(thesis),
                    100
            ));
        }

        for (Thesis thesis : thesisRepository.findActiveThesesForRole(user.getId(), Set.of(ThesisRoleName.ADVISOR), Set.of(ThesisState.PROPOSAL))) {
            if (thesis.getProposals().isEmpty()) {
                continue;
            }

            tasks.add(new TaskDto(
                    "A proposal was submitted to thesis \"" + thesis.getTitle() + "\". Please review and accept it or send feedback to the student.",
                    getThesisLink(thesis),
                    100
            ));
        }

        // thesis submission task
        for (Thesis thesis : thesisRepository.findActiveThesesForRole(user.getId(), Set.of(ThesisRoleName.STUDENT), Set.of(ThesisState.WRITING))) {
            tasks.add(new TaskDto(
                    "Submit your final thesis and presentation. You can check your submission deadline on the thesis page.",
                    getThesisLink(thesis),
                    80
            ));
        }

        // presentation tasks
        for (Thesis thesis : thesisRepository.findActiveThesesForRole(user.getId(), Set.of(ThesisRoleName.STUDENT, ThesisRoleName.ADVISOR), Set.of(ThesisState.WRITING, ThesisState.SUBMITTED))) {
            if (!thesis.getPresentations().isEmpty() || thesis.getEndDate() == null) {
                continue;
            }

            if (thesis.getEndDate().minus(30, ChronoUnit.DAYS).isAfter(Instant.now())) {
                continue;
            }

            tasks.add(new TaskDto(
                    "Schedule a presentation date for thesis \"" + thesis.getTitle() + "\" with the advisor.",
                    getThesisLink(thesis),
                    40
            ));
        }

        // thesis assessment task
        for (Thesis thesis : thesisRepository.findActiveThesesForRole(user.getId(), Set.of(ThesisRoleName.ADVISOR), Set.of(ThesisState.SUBMITTED))) {
            tasks.add(new TaskDto(
                    "Thesis \"" + thesis.getTitle() + "\" was submitted. Please review the thesis and add an assessment.",
                    getThesisLink(thesis),
                    100
            ));
        }

        // grade thesis task
        for (Thesis thesis : thesisRepository.findActiveThesesForRole(user.getId(), Set.of(ThesisRoleName.SUPERVISOR), Set.of(ThesisState.ASSESSED))) {
            tasks.add(new TaskDto(
                    "Review assessment of thesis \"" + thesis.getTitle() + "\" and add a final grade.",
                    getThesisLink(thesis),
                    100
            ));
        }

        // close thesis task
        for (Thesis thesis : thesisRepository.findActiveThesesForRole(user.getId(), Set.of(ThesisRoleName.SUPERVISOR), Set.of(ThesisState.GRADED))) {
            tasks.add(new TaskDto(
                    "Thesis \"" + thesis.getTitle() + "\" is graded but not completed yet.",
                    getThesisLink(thesis),
                    20
            ));
        }

        if (user.hasAnyGroup("admin", "supervisor", "advisor")) {
            // review application task
            long unreviewedApplications = applicationRepository.countUnreviewedApplications(user.getId());

            if (unreviewedApplications > 0) {
                tasks.add(new TaskDto(
                        "You have " + unreviewedApplications + " unreviewed applications.",
                        "/applications",
                        10
                ));
            }

            // no open topic task
            long openTopics = topicRepository.countOpenTopics();

            if (openTopics == 0) {
                tasks.add(new TaskDto(
                        "There are currently no open Topics. Please create a topic.",
                        "/topics",
                        10
                ));
            }
        }

        tasks.sort(Comparator.comparingInt(a -> a.priority().intValue()));

        return tasks.reversed();
    }

    private String getThesisLink(Thesis thesis) {
        return "/theses/" + thesis.getId();
    }
}