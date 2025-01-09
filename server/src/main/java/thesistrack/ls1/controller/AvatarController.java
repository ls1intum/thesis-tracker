package thesismanagement.ls1.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import thesismanagement.ls1.constants.ThesisState;
import thesismanagement.ls1.constants.ThesisVisibility;
import thesismanagement.ls1.dto.PaginationDto;
import thesismanagement.ls1.dto.PublishedThesisDto;
import thesismanagement.ls1.entity.Thesis;
import thesismanagement.ls1.entity.User;
import thesismanagement.ls1.service.AuthenticationService;
import thesismanagement.ls1.service.ThesisService;
import thesismanagement.ls1.service.UploadService;
import thesismanagement.ls1.service.UserService;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/v2/avatars")
public class AvatarController {
    private final UserService userService;
    private final UploadService uploadService;

    @Autowired
    public AvatarController(UserService userService, UploadService uploadService) {
        this.userService = userService;
        this.uploadService = uploadService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Resource> getTheses(@PathVariable UUID userId) {
        User user = userService.findById(userId);
        String avatar = user.getAvatar();

        if (avatar == null || avatar.isBlank()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS).cachePublic())
                .body(uploadService.load(avatar));
    }
}
