package com.gabrielf.job_matching_platform.service;

import com.gabrielf.job_matching_platform.dto.request.CandidateRequest;
import com.gabrielf.job_matching_platform.dto.response.CandidateResponse;
import com.gabrielf.job_matching_platform.exception.DuplicateResourceException;
import com.gabrielf.job_matching_platform.exception.ForbiddenOperationException;
import com.gabrielf.job_matching_platform.model.Candidate;
import com.gabrielf.job_matching_platform.model.Skill;
import com.gabrielf.job_matching_platform.model.User;
import com.gabrielf.job_matching_platform.model.enums.Role;
import com.gabrielf.job_matching_platform.repository.CandidateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CandidateServiceTest {

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private SkillService skillService;

    @Mock
    private UserService userService;

    @InjectMocks
    private CandidateService candidateService;

    private UUID userId;
    private CandidateRequest request;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        request = new CandidateRequest("Bio de teste", List.of("Java", "Spring Boot"));
    }


    @Nested
    @DisplayName("createProfile")
    class CreateProfile {

        @Test
        @DisplayName("should create profile successfully when user has CANDIDATE role")
        void shouldCreateProfileForCandidateRole() {
            User candidateUser = User.builder().id(userId).name("Gabriel").role(Role.CANDIDATE).build();
            Set<Skill> skills = Set.of(Skill.builder().id(UUID.randomUUID()).name("Java").build());

            when(candidateRepository.findByUserId(userId)).thenReturn(Optional.empty());
            when(userService.findEntityById(userId)).thenReturn(candidateUser);
            when(skillService.resolveSkills(request.skills())).thenReturn(skills);
            when(candidateRepository.save(any(Candidate.class))).thenAnswer(invocation -> {
                Candidate candidate = invocation.getArgument(0);
                candidate.setId(UUID.randomUUID());
                return candidate;
            });

            CandidateResponse response = candidateService.createProfile(userId, request);

            assertThat(response).isNotNull();
            assertThat(response.bio()).isEqualTo("Bio de teste");
            verify(candidateRepository).save(any(Candidate.class));
        }

        @Test
        @DisplayName("should throw ForbiddenOperationException when user has RECRUITER role")
        void shouldThrowWhenUserIsRecruiter() {
            User recruiterUser = User.builder().id(userId).name("Ana").role(Role.RECRUITER).build();

            when(candidateRepository.findByUserId(userId)).thenReturn(Optional.empty());
            when(userService.findEntityById(userId)).thenReturn(recruiterUser);

            assertThrows(ForbiddenOperationException.class,
                    () -> candidateService.createProfile(userId, request));

            verify(candidateRepository, never()).save(any(Candidate.class));
        }

        @Test
        @DisplayName("should throw DuplicateResourceException when candidate profile already exists")
        void shouldThrowWhenProfileAlreadyExists() {
            Candidate existingCandidate = Candidate.builder().id(UUID.randomUUID()).build();
            when(candidateRepository.findByUserId(userId)).thenReturn(Optional.of(existingCandidate));

            assertThrows(DuplicateResourceException.class,
                    () -> candidateService.createProfile(userId, request));

            verify(userService, never()).findEntityById(any());
            verify(candidateRepository, never()).save(any(Candidate.class));

        }
    }

}
