package com.gabrielf.job_matching_platform.service;

import com.gabrielf.job_matching_platform.dto.request.JobRequest;
import com.gabrielf.job_matching_platform.dto.response.JobResponse;
import com.gabrielf.job_matching_platform.exception.ResourceNotFoundException;
import com.gabrielf.job_matching_platform.model.Job;
import com.gabrielf.job_matching_platform.model.Skill;
import com.gabrielf.job_matching_platform.model.User;
import com.gabrielf.job_matching_platform.model.enums.JobStatus;
import com.gabrielf.job_matching_platform.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class JobService {

    private final JobRepository jobRepository;
    private final SkillService skillService;
    private final UserService userService;

    public JobResponse create (UUID recruiterId, JobRequest request) {
        User recruiter = userService.findEntityById(recruiterId);
        Set<Skill> skills = skillService.resolveSkills(request.requiredSkills());

        Job job = Job.builder()
                .title(request.title())
                .description(request.description())
                .recruiter(recruiter)
                .requiredSkills(skills)
                .build();

        Job saved = jobRepository.save(job);
        return JobResponse.fromEntity(saved);

    }

    @Transactional(readOnly = true)
    public JobResponse findById(UUID id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
        return JobResponse.fromEntity(job);

    }

    @Transactional(readOnly = true)
    public Page<JobResponse> findOpenJobs(Pageable pageable) {
        return jobRepository.findByStatus(JobStatus.OPEN, pageable)
                .map(JobResponse::fromEntity);

    }

    @Transactional(readOnly = true)
    public Page<JobResponse> findByRecruiter(UUID recruiterId, Pageable pageable) {
        return jobRepository.findByRecruiterId(recruiterId, pageable)
                .map(JobResponse::fromEntity);

    }

    public JobResponse updateStatus(UUID id, JobStatus status) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));

        job.setStatus(status);
        Job updated = jobRepository.save(job);
        return JobResponse.fromEntity(updated);
    }

}
