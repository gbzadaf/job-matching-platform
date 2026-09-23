package com.gabrielf.job_matching_platform.service;

import com.gabrielf.job_matching_platform.dto.request.ApplicationRequest;
import com.gabrielf.job_matching_platform.dto.response.ApplicationResponse;
import com.gabrielf.job_matching_platform.exception.DuplicateResourceException;
import com.gabrielf.job_matching_platform.exception.ResourceNotFoundException;
import com.gabrielf.job_matching_platform.model.Application;
import com.gabrielf.job_matching_platform.model.Candidate;
import com.gabrielf.job_matching_platform.model.Job;
import com.gabrielf.job_matching_platform.model.enums.ApplicationStatus;
import com.gabrielf.job_matching_platform.repository.ApplicationRepository;
import com.gabrielf.job_matching_platform.repository.CandidateRepository;
import com.gabrielf.job_matching_platform.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final CandidateRepository  candidateRepository;
    private final JobRepository jobRepository;

    public ApplicationResponse apply(UUID userId, ApplicationRequest request) {
        Candidate candidate = candidateRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + userId));

        Job job = jobRepository.findById(request.jobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + request.jobId()));

        if (applicationRepository.existsByCandidateIdAndJobId(candidate.getId(), job.getId())) {
            throw new DuplicateResourceException("Candidate has already applied to this job");

        }

        Application application = Application.builder()
                .candidate(candidate)
                .job(job)
                .build();

        Application saved =  applicationRepository.save(application);
        return ApplicationResponse.fromEntity(saved);

    }

    @Transactional(readOnly = true)
    public Page<ApplicationResponse> findByCandidate(UUID candidateId, Pageable pageable) {
        return applicationRepository.findByCandidateId(candidateId, pageable)
                .map(ApplicationResponse::fromEntity);

    }

    @Transactional(readOnly = true)
    public Page<ApplicationResponse> findByJob(UUID jobId, Pageable pageable) {
        return applicationRepository.findByJobId(jobId, pageable)
                .map(ApplicationResponse::fromEntity);

    }

    public ApplicationResponse updateStatus(UUID id, ApplicationStatus status) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));

        application.setStatus(status);
        Application updated = applicationRepository.save(application);
        return ApplicationResponse.fromEntity(updated);

    }


}
