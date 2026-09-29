package com.gabrielf.job_matching_platform.service;

import com.gabrielf.job_matching_platform.dto.request.RegisterRequest;
import com.gabrielf.job_matching_platform.dto.response.UserResponse;
import com.gabrielf.job_matching_platform.exception.DuplicateResourceException;
import com.gabrielf.job_matching_platform.exception.ResourceNotFoundException;
import com.gabrielf.job_matching_platform.model.User;
import com.gabrielf.job_matching_platform.model.enums.Role;
import com.gabrielf.job_matching_platform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest(
                "Gabriel Candidato",
                "gabriel@email.com",
                "senha12345",
                Role.CANDIDATE
        );
    }


    @Nested
    @DisplayName("register")
    class Register {

        @Test
        @DisplayName("should register a new user successfully with encoded password")
        void shouldRegisterNewUser() {
            when(userRepository.existsByEmail(registerRequest.email())).thenReturn(false);
            when(passwordEncoder.encode(registerRequest.password())).thenReturn("hashed-password");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(UUID.randomUUID());
                return user;
            });

            UserResponse response = userService.register(registerRequest);

            assertThat(response.name()).isEqualTo("Gabriel Candidato");
            assertThat(response.email()).isEqualTo("gabriel@email.com");
            assertThat(response.role()).isEqualTo(Role.CANDIDATE);

        }

        @Test
        @DisplayName("should never persist the raw password, only the encoded one")
        void shouldPersistEncodedPasswordOnly() {
            when(userRepository.existsByEmail(registerRequest.email())).thenReturn(false);
            when(passwordEncoder.encode(registerRequest.password())).thenReturn("hashed-password");
            when(userRepository.save(any(User.class))).thenAnswer(invocation ->
                    invocation.getArgument(0));

            userService.register(registerRequest);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());

            User savedUser = userCaptor.getValue();
            assertThat(savedUser.getPassword()).isEqualTo("hashed-password");
            assertThat(savedUser.getPassword()).isNotEqualTo(registerRequest.password());

        }

        @Test
        @DisplayName("should throw DuplicateResourceException when email is already registered")
        void shouldThrowWhenEmailAlreadyExists() {
            when(userRepository.existsByEmail(registerRequest.email())).thenReturn(true);

            assertThrows(DuplicateResourceException.class,
                    () -> userService.register(registerRequest));

            verify(userRepository, never()).save(any(User.class));

        }

    }

    @Nested
    @DisplayName("findEntityById")
    class FindEntityById {

        @Test
        @DisplayName("should return user entity when found")
        void shouldReturnUserWhenFound() {
            UUID userId = UUID.randomUUID();
            User user = User.builder().id(userId).name("Ana").email("ana@empresa.com").role(Role.RECRUITER).build();

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            User result = userService.findEntityById(userId);

            assertThat(result.getId()).isEqualTo(userId);
            assertThat(result.getName()).isEqualTo("Ana");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user does not exist")
        void shouldThrowWhenUserNotFound() {
            UUID userId = UUID.randomUUID();
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> userService.findEntityById(userId));

        }
    }

}
