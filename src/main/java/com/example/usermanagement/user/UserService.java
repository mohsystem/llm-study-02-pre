package com.example.usermanagement.user;

import com.example.usermanagement.admin.dto.ImportXmlResponse;
import com.example.usermanagement.admin.dto.MergeUsersResponse;
import com.example.usermanagement.directory.DirectoryUserResponse;
import com.example.usermanagement.user.dto.UpdateUserProfileRequest;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface UserService {

    User createUser(User user);

    Optional<User> getUserById(Long id);

    User getUserByPublicRef(String publicRef);

    User updateUserProfile(String publicRef, UpdateUserProfileRequest request);

    Page<User> getUsersForAdmin(int page, int size, UserRole role, AccountStatus status);

    MergeUsersResponse mergeUsers(String sourcePublicRef, String targetPublicRef);

    User updateRole(Long id, UserRole role);

    DirectoryUserResponse lookupDirectoryUser(String dc, String username);

    ImportXmlResponse importUsersFromXml(String xmlContent);

    DirectoryUserResponse validateAgainstDirectory(String dc, String username);

    User updateIdentityDocument(String publicRef, byte[] content, String contentType);
}
