package com.gabrielf.job_matching_platform.service;

import com.gabrielf.job_matching_platform.dto.response.JobMatchResponse;
import com.gabrielf.job_matching_platform.exception.ResourceNotFoundException;
import com.gabrielf.job_matching_platform.model.Candidate;
import com.gabrielf.job_matching_platform.model.Job;
import com.gabrielf.job_matching_platform.model.Skill;
import com.gabrielf.job_matching_platform.model.enums.JobStatus;
import com.gabrielf.job_matching_platform.repository.CandidateRepository;
import com.gabrielf.job_matching_platform.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchingService {

    private final CandidateRepository candidateRepository;
    private final JobRepository jobRepository;

    public List<JobMatchResponse> findMatchesForCandidate(UUID candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + candidateId));

        List<Job> openJobs = jobRepository.findByStatus(JobStatus.OPEN, Pageable.unpaged())
                .getContent();

        return openJobs.stream()
                .map(job -> calculateMatch(candidate, job))
                .sorted(Comparator.comparingDouble(JobMatchResponse::matchPercentage).reversed())
                .toList();

    }

    public JobMatchResponse calculateMatchForJob(UUID candidateId, UUID jobId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + candidateId));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        return calculateMatch(candidate, job);

    }

    private JobMatchResponse calculateMatch(Candidate candidate, Job job) {
        Set<Skill> candidateSkills = candidate.getSkills();
        Set<Skill> requiredSkills = job.getRequiredSkills();

        List<String> matchedSkills = requiredSkills.stream()
                .filter(candidateSkills::contains)
                .map(Skill::getName)
                .toList();

        List<String> missingSkills = requiredSkills.stream()
                .filter(skill -> !candidateSkills.contains(skill))
                .map(Skill::getName)
                .toList();

        int totalRequired = requiredSkills.size();
        int matchedCount = matchedSkills.size();
        double percentage = totalRequired == 0 ? 0.0 : (matchedCount * 100.0) / totalRequired;

        return new JobMatchResponse(
                job.getId(),
                job.getTitle(),
                totalRequired,
                matchedCount,
                Math.round(percentage * 100.0) / 100.0,
                matchedSkills,
                missingSkills
        );

    }

}
