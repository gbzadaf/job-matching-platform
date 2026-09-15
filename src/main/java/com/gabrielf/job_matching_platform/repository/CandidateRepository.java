package com.gabrielf.job_matching_platform.repository;

import com.gabrielf.job_matching_platform.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CandidateRepository extends JpaRepository<Candidate, UUID> {

    Optional<Candidate> findByUserId(UUID userId);

    /*como está com fetch LAZY, uso JOIN FETCH para buscar o user junto (ex: pra mostrar nome/email do candidato numa
    tela de perfil), busca tudo numa query só. Evita N+1 sem abrir mao do LAZY.
     */
    @Query("SELECT c FROM Candidate c JOIN FETCH c.user WHERE c.id = :id")
    Optional<Candidate> findByIdWithUser(@Param("id") UUID id);

}
