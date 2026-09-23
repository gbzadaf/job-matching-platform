package com.gabrielf.job_matching_platform.service;

import com.gabrielf.job_matching_platform.dto.request.CandidateRequest;
import com.gabrielf.job_matching_platform.dto.response.CandidateResponse;
import com.gabrielf.job_matching_platform.exception.DuplicateResourceException;
import com.gabrielf.job_matching_platform.exception.ForbiddenOperationException;
import com.gabrielf.job_matching_platform.exception.ResourceNotFoundException;
import com.gabrielf.job_matching_platform.model.Candidate;
import com.gabrielf.job_matching_platform.model.Skill;
import com.gabrielf.job_matching_platform.model.User;
import com.gabrielf.job_matching_platform.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final SkillService skillService;
    private final UserService  userService;

    public CandidateResponse createProfile(UUID userId, CandidateRequest request) {
        if (candidateRepository.findByUserId(userId).isPresent()) {
            throw new DuplicateResourceException("Candidate profile already exists for this user");
        }

        User user = userService.findEntityById(userId);
        Set<Skill> skills = skillService.resolveSkills(request.skills());

        Candidate candidate = Candidate.builder()
                .user(user)
                .bio(request.bio())
                .skills(skills)
                .build();

        Candidate saved = candidateRepository.save(candidate);
        return CandidateResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public CandidateResponse findById(UUID id) {
        Candidate candidate = candidateRepository.findByIdWithUser(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + id));
        return CandidateResponse.fromEntity(candidate);

    }

    public CandidateResponse updateProfile(UUID id, UUID authenticatedUserId, CandidateRequest request) {
        Candidate candidate = candidateRepository.findByIdWithUser(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + id));

        if (!candidate.getUser().getId().equals(authenticatedUserId)) {
            throw new ForbiddenOperationException("You do not have permission to update this profile");
        }

        candidate.setBio(request.bio());
        candidate.setSkills(skillService.resolveSkills(request.skills()));

        Candidate updated = candidateRepository.save(candidate);
        return CandidateResponse.fromEntity(updated);

    }

}
