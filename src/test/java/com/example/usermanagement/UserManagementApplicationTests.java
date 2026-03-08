package com.example.usermanagement;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.usermanagement.user.User;
import com.example.usermanagement.user.UserRepository;
import com.example.usermanagement.user.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserManagementApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoadsAndSeedsAdmin() {
        User admin = userRepository.findByEmail("admin@example.com").orElseThrow();
        assertThat(admin.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(admin.getPublicRef()).isNotBlank();
    }
}
