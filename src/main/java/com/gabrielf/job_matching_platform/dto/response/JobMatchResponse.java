package com.gabrielf.job_matching_platform.dto.response;

import java.util.List;
import java.util.UUID;

public record JobMatchResponse(
        UUID jobId,
        String jobTitle,
        int totalRequiredSkills,
        int matchedSkillsCount,
        double matchPercentage,
        List<String> matchedSkills,
        List<String> missingSkills
) {
}
