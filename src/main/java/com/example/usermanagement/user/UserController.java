package com.example.usermanagement.user;

import com.example.usermanagement.user.dto.UpdateUserProfileRequest;
import com.example.usermanagement.user.dto.UserProfileResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{publicRef}")
    public UserProfileResponse getProfile(@PathVariable String publicRef) {
        return UserProfileResponse.from(userService.getUserByPublicRef(publicRef));
    }

    @PutMapping("/{publicRef}")
    public UserProfileResponse updateProfile(@PathVariable String publicRef,
                                             @Valid @RequestBody UpdateUserProfileRequest request) {
        return UserProfileResponse.from(userService.updateUserProfile(publicRef, request));
    }

    @GetMapping("/{publicRef}/document")
    public ResponseEntity<byte[]> getIdentityDocument(@PathVariable String publicRef) {
        User user = userService.getUserByPublicRef(publicRef);
        if (user.getIdentityDocument() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        String contentType = user.getIdentityDocumentContentType() == null
            ? MediaType.APPLICATION_OCTET_STREAM_VALUE
            : user.getIdentityDocumentContentType();

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, contentType)
            .body(user.getIdentityDocument());
    }

    @PutMapping("/{publicRef}/document")
    public ResponseEntity<?> updateIdentityDocument(@PathVariable String publicRef,
                                                    @RequestParam("file") MultipartFile file) throws Exception {
        User updated = userService.updateIdentityDocument(publicRef, file.getBytes(), file.getContentType());
        return ResponseEntity.ok(new DocumentUpdateResponse(updated.getPublicRef(), "UPDATED"));
    }

    public record DocumentUpdateResponse(String publicRef, String status) {
    }
}
