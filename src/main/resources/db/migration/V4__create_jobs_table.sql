CREATE TABLE jobs (
                      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                      title VARCHAR(150) NOT NULL,
                      description TEXT NOT NULL,
                      recruiter_id UUID NOT NULL,
                      status VARCHAR(20) NOT NULL,
                      created_at TIMESTAMP NOT NULL,
                      CONSTRAINT fk_jobs_recruiter FOREIGN KEY (recruiter_id) REFERENCES users (id)
);

CREATE TABLE job_required_skills (
                                     job_id UUID NOT NULL,
                                     skill_id UUID NOT NULL,
                                     PRIMARY KEY (job_id, skill_id),
                                     CONSTRAINT fk_job_required_skills_job FOREIGN KEY (job_id) REFERENCES jobs (id),
                                     CONSTRAINT fk_job_required_skills_skill FOREIGN KEY (skill_id) REFERENCES skills (id)
);