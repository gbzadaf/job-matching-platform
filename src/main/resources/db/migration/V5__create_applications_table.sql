CREATE TABLE applications (
                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              candidate_id UUID NOT NULL,
                              job_id UUID NOT NULL,
                              status VARCHAR(20) NOT NULL,
                              applied_at TIMESTAMP NOT NULL,
                              CONSTRAINT fk_applications_candidate FOREIGN KEY (candidate_id) REFERENCES candidates (id),
                              CONSTRAINT fk_applications_job FOREIGN KEY (job_id) REFERENCES jobs (id),
                              CONSTRAINT uk_applications_candidate_job UNIQUE (candidate_id, job_id)
);