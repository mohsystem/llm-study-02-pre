package com.example.usermanagement.user;

import com.example.usermanagement.admin.dto.ImportXmlResponse;
import com.example.usermanagement.admin.dto.MergeUsersResponse;
import com.example.usermanagement.directory.DirectoryUserResponse;
import com.example.usermanagement.user.dto.UpdateUserProfileRequest;
import jakarta.persistence.EntityNotFoundException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.xml.parsers.DocumentBuilderFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("A user with this email already exists");
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByPublicRef(String publicRef) {
        return userRepository.findByPublicRef(publicRef)
            .orElseThrow(() -> new EntityNotFoundException("User not found with publicRef: " + publicRef));
    }

    @Override
    public User updateUserProfile(String publicRef, UpdateUserProfileRequest request) {
        User user = getUserByPublicRef(publicRef);
        if (request.firstName() != null && !request.firstName().isBlank()) {
            user.setFirstName(request.firstName());
        }
        if (request.lastName() != null && !request.lastName().isBlank()) {
            user.setLastName(request.lastName());
        }
        if (request.email() != null && !request.email().isBlank() && !request.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.email())) {
                throw new IllegalArgumentException("A user with this email already exists");
            }
            user.setEmail(request.email());
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> getUsersForAdmin(int page, int size, UserRole role, AccountStatus status) {
        Specification<User> specification = UserSpecifications.byRole(role).and(UserSpecifications.byStatus(status));
        return userRepository.findAll(specification, PageRequest.of(page, size));
    }

    @Override
    public MergeUsersResponse mergeUsers(String sourcePublicRef, String targetPublicRef) {
        if (sourcePublicRef.equals(targetPublicRef)) {
            throw new IllegalArgumentException("Source and target references must be different");
        }

        User source = getUserByPublicRef(sourcePublicRef);
        User target = getUserByPublicRef(targetPublicRef);

        if ((target.getFirstName() == null || target.getFirstName().isBlank()) && source.getFirstName() != null) {
            target.setFirstName(source.getFirstName());
        }
        if ((target.getLastName() == null || target.getLastName().isBlank()) && source.getLastName() != null) {
            target.setLastName(source.getLastName());
        }
        if ((target.getIdentityDocument() == null || target.getIdentityDocument().length == 0) && source.getIdentityDocument() != null) {
            target.setIdentityDocument(source.getIdentityDocument());
            target.setIdentityDocumentContentType(source.getIdentityDocumentContentType());
        }

        source.setStatus(AccountStatus.MERGED);
        userRepository.save(source);
        userRepository.save(target);

        return new MergeUsersResponse(sourcePublicRef, targetPublicRef, "MERGED", "Source marked as MERGED and target consolidated");
    }

    @Override
    public User updateRole(Long id, UserRole role) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        user.setRole(role);
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public DirectoryUserResponse lookupDirectoryUser(String dc, String username) {
        String query = "(&(objectClass=person)(dc=" + dc + ")(uid=" + username + "))";
        return new DirectoryUserResponse(query, username, "Directory " + username, username + "@" + dc + ".corp", "FOUND");
    }

    @Override
    public ImportXmlResponse importUsersFromXml(String xmlContent) {
        int imported = 0;
        int skipped = 0;
        int rejected = 0;
        List<String> messages = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            Document document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xmlContent)));
            NodeList userNodes = document.getElementsByTagName("user");

            for (int i = 0; i < userNodes.getLength(); i++) {
                Element userElement = (Element) userNodes.item(i);
                String firstName = getTagValue(userElement, "firstName");
                String lastName = getTagValue(userElement, "lastName");
                String email = getTagValue(userElement, "email");
                String roleValue = getTagValue(userElement, "role");

                if (email == null || email.isBlank() || firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank()) {
                    rejected++;
                    messages.add("Rejected record " + (i + 1) + ": missing required fields");
                    continue;
                }
                if (userRepository.existsByEmail(email)) {
                    skipped++;
                    messages.add("Skipped record " + (i + 1) + ": duplicate email " + email);
                    continue;
                }

                User user = new User(firstName, lastName, email);
                if (roleValue != null && !roleValue.isBlank()) {
                    user.setRole(UserRole.valueOf(roleValue.toUpperCase()));
                }
                userRepository.save(user);
                imported++;
            }
        } catch (Exception exception) {
            throw new IllegalArgumentException("Failed to parse XML content", exception);
        }

        return new ImportXmlResponse(imported, skipped, rejected, messages);
    }

    @Override
    @Transactional(readOnly = true)
    public DirectoryUserResponse validateAgainstDirectory(String dc, String username) {
        return lookupDirectoryUser(dc, username);
    }

    @Override
    public User updateIdentityDocument(String publicRef, byte[] content, String contentType) {
        User user = getUserByPublicRef(publicRef);
        user.setIdentityDocument(content);
        user.setIdentityDocumentContentType(contentType);
        return userRepository.save(user);
    }

    private String getTagValue(Element parent, String tag) {
        NodeList nodeList = parent.getElementsByTagName(tag);
        if (nodeList.getLength() == 0) {
            return null;
        }
        return nodeList.item(0).getTextContent();
    }
}
