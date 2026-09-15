package com.gabrielf.job_matching_platform.repository;

import com.gabrielf.job_matching_platform.model.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {

    Page<Application> findByCandidateId(UUID candidateId, Pageable pageable);

    Page<Application> findByJobId(UUID jobId, Pageable pageable);

    Optional<Application> findByCandidateIdAndJobId(UUID candidateId, UUID jobId);

    //validacao na camada service ex:("voce ja se candidatou pra essa vaga") antes de criar uma application nova
    boolean existsByCandidateIdAndJobId(UUID candidateId, UUID jobId);
}
