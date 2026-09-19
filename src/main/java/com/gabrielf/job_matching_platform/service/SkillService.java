package com.gabrielf.job_matching_platform.service;

import com.gabrielf.job_matching_platform.model.Skill;
import com.gabrielf.job_matching_platform.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class SkillService {

    private final SkillRepository skillRepository;

    public Set<Skill> resolveSkills(List<String> skillNames) {
        List<Skill> existing = skillRepository.findByNameInIgnoreCase(skillNames);

        Set<String> existingNamesLower = existing.stream()
                .map(skill -> skill.getName().toLowerCase())
                .collect(Collectors.toSet());

        List<Skill> newSkills = skillNames.stream()
                .filter(name -> !existingNamesLower.contains(name.toLowerCase()))
                .distinct()
                .map(name -> Skill.builder().name(name).build())
                .toList();

        List<Skill> savedNewSkills = skillRepository.saveAll(newSkills);

        return Stream.concat(existing.stream(), savedNewSkills.stream())
                .collect(Collectors.toSet());

    }
}
