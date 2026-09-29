package com.gabrielf.job_matching_platform.repository;

import com.gabrielf.job_matching_platform.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SkillRepository extends JpaRepository<Skill, UUID> {

    Optional<Skill> findByNameIgnoreCase(String name);

    List<Skill> findByNameInIgnoreCase(List<String> names);


}
