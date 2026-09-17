package com.gabrielf.job_matching_platform.dto.response;

import com.gabrielf.job_matching_platform.model.Job;
import com.gabrielf.job_matching_platform.model.enums.JobStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record JobResponse(
        UUID id,
        String title,
        String description,
        UserResponse recruiter,
        List<SkillResponse> requiredSkills,
        JobStatus status,
        LocalDateTime createdAt
) {
    public static JobResponse fromEntity (Job job) {
        return new JobResponse(
                job.getId(),
                job.getTitle(),
                job.getDescription(),
                UserResponse.fromEntity(job.getRecruiter()),
                job.getRequiredSkills().stream()
                        .map(SkillResponse::fromEntity)
                        .toList(),
                job.getStatus(),
                job.getCreatedAt()

        );
    }
}
