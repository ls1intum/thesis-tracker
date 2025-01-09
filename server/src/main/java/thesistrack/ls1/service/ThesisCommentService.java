package thesismanagement.ls1.service;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import thesismanagement.ls1.constants.ThesisCommentType;
import thesismanagement.ls1.constants.UploadFileType;
import thesismanagement.ls1.entity.Thesis;
import thesismanagement.ls1.entity.ThesisComment;
import thesismanagement.ls1.entity.User;
import thesismanagement.ls1.exception.request.ResourceNotFoundException;
import thesismanagement.ls1.repository.ThesisCommentRepository;

import java.time.Instant;
import java.util.UUID;

@Service
public class ThesisCommentService {
    private final ThesisCommentRepository thesisCommentRepository;
    private final UploadService uploadService;
    private final MailingService mailingService;

    public ThesisCommentService(ThesisCommentRepository thesisCommentRepository, UploadService uploadService, MailingService mailingService) {
        this.thesisCommentRepository = thesisCommentRepository;
        this.uploadService = uploadService;
        this.mailingService = mailingService;
    }

    public Page<ThesisComment> getComments(Thesis thesis, ThesisCommentType commentType, Integer page, Integer limit) {
        return thesisCommentRepository.searchComments(
                thesis.getId(),
                commentType,
                PageRequest.of(page, limit)
        );
    }

    @Transactional
    public ThesisComment postComment(User postingUser, Thesis thesis, ThesisCommentType commentType, String message, MultipartFile file) {
        ThesisComment comment = new ThesisComment();

        comment.setType(commentType);
        comment.setThesis(thesis);
        comment.setMessage(message);
        comment.setCreatedAt(Instant.now());
        comment.setCreatedBy(postingUser);

        if (file != null) {
            comment.setUploadName(file.getOriginalFilename());
            comment.setFilename(uploadService.store(file, 20 * 1024 * 1024, UploadFileType.ANY));
        }

        comment = thesisCommentRepository.save(comment);

        mailingService.sendNewCommentEmail(comment);

        return comment;
    }

    public Resource getCommentFile(ThesisComment comment) {
        return uploadService.load(comment.getFilename());
    }

    @Transactional
    public ThesisComment deleteComment(ThesisComment comment) {
        thesisCommentRepository.deleteById(comment.getId());

        return comment;
    }

    public ThesisComment findById(UUID thesisId, UUID commentId) {
        ThesisComment comment = thesisCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Comment with id %s not found", commentId)));

        if (!comment.getThesis().getId().equals(thesisId)) {
            throw new ResourceNotFoundException(String.format("Comment does not belong to thesis id %s", thesisId));
        }

        return comment;
    }
}
