package com.gabrielf.job_matching_platform.dto.response;

import com.gabrielf.job_matching_platform.model.Application;
import com.gabrielf.job_matching_platform.model.enums.ApplicationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApplicationResponse(
        UUID id,
        UUID candidateId,
        String candidateName,
        JobResponse job,
        ApplicationStatus status,
        LocalDateTime appliedAt
) {
    public static ApplicationResponse fromEntity(Application application) {
        return new ApplicationResponse(
                application.getId(),
                application.getCandidate().getId(),
                application.getCandidate().getUser().getName(),
                JobResponse.fromEntity(application.getJob()),
                application.getStatus(),
                application.getAppliedAt()
        );

    }
}
