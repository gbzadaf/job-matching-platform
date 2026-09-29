package com.gabrielf.job_matching_platform.dto.response;

import com.gabrielf.job_matching_platform.model.Skill;

import java.util.UUID;

public record SkillResponse(
        UUID id,
        String name
) {
    public static SkillResponse fromEntity(Skill skill) {
        return new SkillResponse(
                skill.getId(),
                skill.getName()
        );
    }
}
