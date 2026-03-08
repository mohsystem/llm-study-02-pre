package com.example.usermanagement.admin;

import com.example.usermanagement.admin.dto.ImportXmlResponse;
import com.example.usermanagement.admin.dto.MergeUsersRequest;
import com.example.usermanagement.admin.dto.MergeUsersResponse;
import com.example.usermanagement.admin.dto.UpdateUserRoleRequest;
import com.example.usermanagement.admin.dto.UpdateUserRoleResponse;
import com.example.usermanagement.directory.DirectoryUserResponse;
import com.example.usermanagement.directory.ValidateDirectoryRequest;
import com.example.usermanagement.user.AccountStatus;
import com.example.usermanagement.user.User;
import com.example.usermanagement.user.UserRole;
import com.example.usermanagement.user.UserService;
import com.example.usermanagement.user.dto.UserProfileResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public Page<UserProfileResponse> listUsers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(required = false) UserRole role,
        @RequestParam(required = false) AccountStatus status
    ) {
        return userService.getUsersForAdmin(page, size, role, status).map(UserProfileResponse::from);
    }

    @PostMapping("/users/merge")
    public MergeUsersResponse mergeUsers(@Valid @RequestBody MergeUsersRequest request) {
        return userService.mergeUsers(request.sourcePublicRef(), request.targetPublicRef());
    }

    @PutMapping("/users/{id}/role")
    public UpdateUserRoleResponse updateUserRole(@PathVariable Long id, @Valid @RequestBody UpdateUserRoleRequest request) {
        User updated = userService.updateRole(id, request.role());
        return new UpdateUserRoleResponse(updated.getId(), updated.getPublicRef(), updated.getRole());
    }

    @GetMapping("/directory/user-search")
    public DirectoryUserResponse userSearch(@RequestParam String dc, @RequestParam String username) {
        return userService.lookupDirectoryUser(dc, username);
    }

    @PostMapping(value = "/users/import-xml", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImportXmlResponse importXml(@RequestPart("file") MultipartFile file) throws Exception {
        return userService.importUsersFromXml(new String(file.getBytes()));
    }

    @PostMapping("/users/validate-directory")
    public DirectoryUserResponse validateDirectory(@Valid @RequestBody ValidateDirectoryRequest request) {
        return userService.validateAgainstDirectory(request.dc(), request.username());
    }
}
