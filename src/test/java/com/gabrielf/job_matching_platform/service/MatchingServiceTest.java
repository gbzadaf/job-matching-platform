package com.gabrielf.job_matching_platform.service;

import com.gabrielf.job_matching_platform.dto.response.JobMatchResponse;
import com.gabrielf.job_matching_platform.exception.ResourceNotFoundException;
import com.gabrielf.job_matching_platform.model.Candidate;
import com.gabrielf.job_matching_platform.model.Job;
import com.gabrielf.job_matching_platform.model.Skill;
import com.gabrielf.job_matching_platform.model.enums.JobStatus;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MatchingServiceTest {

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private MatchingService matchingService;

    private UUID candidateId;
    private UUID jobId;
    private Skill java;
    private Skill spring;
    private Skill postgres;
    private Skill docker;

    @BeforeEach
    void setUp() {
        candidateId = UUID.randomUUID();
        jobId = UUID.randomUUID();

        java = Skill.builder().id(UUID.randomUUID()).name("Java").build();
        spring = Skill.builder().id(UUID.randomUUID()).name("Spring Boot").build();
        postgres = Skill.builder().id(UUID.randomUUID()).name("PostgreSQL").build();
        docker = Skill.builder().id(UUID.randomUUID()).name("Docker").build();
    }


    @Nested
    @DisplayName("calculateMatchForJob")
    class CalculateMatchForJob {

        @Test
        @DisplayName("should calculate partial match correctly when candidate has some required skills")
        void shouldCalculatePartialMatch() {
            Candidate candidate = Candidate.builder()
                    .id(candidateId)
                    .skills(Set.of(java, spring, postgres))
                    .build();

            Job job = Job.builder()
                    .id(jobId)
                    .title("Desenvolvedor Java Pleno")
                    .requiredSkills(Set.of(java, spring, postgres, docker))
                    .build();

            when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
            when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

            JobMatchResponse result = matchingService.calculateMatchForJob(candidateId, jobId);

            assertThat(result.totalRequiredSkills()).isEqualTo(4);
            assertThat(result.matchedSkillsCount()).isEqualTo(3);
            assertThat(result.matchPercentage()).isEqualTo(75.0);
            assertThat(result.matchedSkills()).containsExactlyInAnyOrder("Java", "Spring Boot", "PostgreSQL");
            assertThat(result.missingSkills()).containsExactly("Docker");

        }

        @Test
        @DisplayName("should return 100% match when candidate has all required skills")
        void shouldCalculateFullMatch() {
            Candidate candidate = Candidate.builder()
                    .id(candidateId)
                    .skills(Set.of(java, spring))
                    .build();

            Job job = Job.builder()
                    .id(jobId)
                    .title("Vaga Simples")
                    .requiredSkills(Set.of(java, spring))
                    .build();

            when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
            when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

            JobMatchResponse result = matchingService.calculateMatchForJob(candidateId, jobId);

            assertThat(result.matchPercentage()).isEqualTo(100.0);
            assertThat(result.missingSkills()).isEmpty();
        }

        @Test
        @DisplayName("should return 0% match when candidate has none of the required skills")
        void shouldCalculateZeroMatch() {
            Candidate candidate = Candidate.builder()
                    .id(candidateId)
                    .skills(Set.of(docker))
                    .build();

            Job job = Job.builder()
                    .id(jobId)
                    .title("Vaga Java")
                    .requiredSkills(Set.of(java, spring))
                    .build();

            when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
            when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

            JobMatchResponse result = matchingService.calculateMatchForJob(candidateId, jobId);

            assertThat(result.matchPercentage()).isEqualTo(0.0);
            assertThat(result.matchedSkills()).isEmpty();
            assertThat(result.missingSkills()).containsExactlyInAnyOrder("Java", "Spring Boot");

        }

        @Test // cobre a logica "double percentage = totalRequired == 0 ? 0.0 : (matchedCount * 100.0) / totalRequired;"
        @DisplayName("should not divide by zero when job has no required skills")
        void shouldHandleJobWithNoRequiredSkills() {
            Candidate candidate = Candidate.builder()
                    .id(candidateId)
                    .skills(Set.of(java))
                    .build();

            Job job = Job.builder()
                    .id(jobId)
                    .title("Vaga sem requisitos")
                    .requiredSkills(Set.of())
                    .build();

            when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
            when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

            JobMatchResponse result = matchingService.calculateMatchForJob(candidateId, jobId);

            assertThat(result.totalRequiredSkills()).isZero();
            assertThat(result.matchPercentage()).isEqualTo(0.0);

        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when candidate does not exist")
        void shouldThrowWhenCandidateNotFound() {
            when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> matchingService.calculateMatchForJob(candidateId, jobId));
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when job does not exist")
        void shouldThrowWhenJobNotFound() {
            Candidate candidate = Candidate.builder().id(candidateId).skills(Set.of()).build();

            when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
            when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> matchingService.calculateMatchForJob(candidateId, jobId));

        }

    }

    @Nested
    @DisplayName("findMatchesForCandidate")
    class FindMatchesForCandidate {

        @Test // valida que o .sorted(...).reversed() do MatchingService realmente funciona
        @DisplayName("should return matches sorted by match percentage descending")
        void shouldReturnMatchesSortedByPercentageDescending() {
            Candidate candidate = Candidate.builder()
                    .id(candidateId)
                    .skills(Set.of(java, spring, postgres))
                    .build();

            Job lowMatchJob = Job.builder()
                    .id(UUID.randomUUID())
                    .title("Vaga com baixo match")
                    .requiredSkills(Set.of(java, docker))
                    .build();

            Job highMatchJob = Job.builder()
                    .id(UUID.randomUUID())
                    .title("Vaga com alto match")
                    .requiredSkills(Set.of(java, spring, postgres))
                    .build();

            Page<Job> openJobsPage = new PageImpl<>(List.of(lowMatchJob, highMatchJob));

            when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
            when(jobRepository.findByStatus(eq(JobStatus.OPEN), any(Pageable.class)))
                    .thenReturn(openJobsPage);

            List<JobMatchResponse> results = matchingService.findMatchesForCandidate(candidateId);

            assertThat(results).hasSize(2);
            assertThat(results.get(0).jobTitle()).isEqualTo("Vaga com alto match");
            assertThat(results.get(0).matchPercentage()).isEqualTo(100.0);
            assertThat(results.get(1).jobTitle()).isEqualTo("Vaga com baixo match");
            assertThat(results.get(1).matchPercentage()).isEqualTo(50.0);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when candidate does not exist")
        void shouldThrowWhenCandidateNotFound() {
            when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> matchingService.findMatchesForCandidate(candidateId));

        }

    }
}
