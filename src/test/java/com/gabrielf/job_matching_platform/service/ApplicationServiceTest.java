package com.gabrielf.job_matching_platform.service;

import com.gabrielf.job_matching_platform.dto.request.ApplicationRequest;
import com.gabrielf.job_matching_platform.dto.response.ApplicationResponse;
import com.gabrielf.job_matching_platform.exception.DuplicateResourceException;
import com.gabrielf.job_matching_platform.exception.ResourceNotFoundException;
import com.gabrielf.job_matching_platform.model.Application;
import com.gabrielf.job_matching_platform.model.Candidate;
import com.gabrielf.job_matching_platform.model.Job;
import com.gabrielf.job_matching_platform.model.User;
import com.gabrielf.job_matching_platform.repository.ApplicationRepository;
import com.gabrielf.job_matching_platform.repository.CandidateRepository;
import com.gabrielf.job_matching_platform.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private ApplicationService applicationService;

    private UUID userId;
    private UUID candidateId;
    private UUID jobId;
    private Candidate candidate;
    private Job job;
    private ApplicationRequest request;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        candidateId = UUID.randomUUID();
        jobId = UUID.randomUUID();

        User user = User.builder().id(userId).name("Gabriel").build();
        candidate = Candidate.builder().id(candidateId).user(user).build();
        User recruiter = User.builder().id(UUID.randomUUID()).name("Ana").build();
        job = Job.builder().id(jobId).title("Vaga Teste").recruiter(recruiter).build();
        request = new ApplicationRequest(jobId);
    }

    @Nested
    @DisplayName("apply")
    class Apply {

        @Test
        @DisplayName("should resolve Candidate from authenticated User id and create application")
        void shouldResolveCandidateFromUserIdAndApply() {
            when(candidateRepository.findByUserId(userId)).thenReturn(Optional.of(candidate));
            when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
            when(applicationRepository.existsByCandidateIdAndJobId(candidateId, jobId)).thenReturn(false);
            when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> {
                Application app = invocation.getArgument(0);
                app.setId(UUID.randomUUID());
                return app;
            });

            ApplicationResponse response = applicationService.apply(userId, request);

            assertThat(response).isNotNull();
            assertThat(response.candidateId()).isEqualTo(candidateId);

            verify(candidateRepository).findByUserId(userId);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user has no candidate profile")
        void shouldThrowWhenNoCandidateProfileExists() {
            when(candidateRepository.findByUserId(userId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> applicationService.apply(userId, request));

            verify(jobRepository, never()).findById(any());
            verify(applicationRepository, never()).save(any(Application.class));
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when job does not exist")
        void shouldThrowWhenJobNotFound() {
            when(candidateRepository.findByUserId(userId)).thenReturn(Optional.of(candidate));
            when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> applicationService.apply(userId, request));

            verify(applicationRepository, never()).save(any(Application.class));
        }

        @Test
        @DisplayName("should throw DuplicateResourceException when candidate already applied to this job")
        void shouldThrowWhenAlreadyApplied() {
            when(candidateRepository.findByUserId(userId)).thenReturn(Optional.of(candidate));
            when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
            when(applicationRepository.existsByCandidateIdAndJobId(candidateId, jobId)).thenReturn(true);

            assertThrows(DuplicateResourceException.class,
                    () -> applicationService.apply(userId, request));

            verify(applicationRepository, never()).save(any(Application.class));

        }
    }

}
