package com.gabrielf.job_matching_platform.repository;

import com.gabrielf.job_matching_platform.model.Job;
import com.gabrielf.job_matching_platform.model.enums.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {

    Page<Job> findByStatus(JobStatus status, Pageable pageable);

    Page<Job> findByRecruiterId(UUID recruiterId, Pageable pageable);
}
