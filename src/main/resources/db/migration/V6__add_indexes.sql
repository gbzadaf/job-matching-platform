CREATE INDEX idx_candidates_user_id ON candidates (user_id);
CREATE INDEX idx_jobs_recruiter_id ON jobs (recruiter_id);
CREATE INDEX idx_applications_candidate_id ON applications (candidate_id);
CREATE INDEX idx_applications_job_id ON applications (job_id);
CREATE INDEX idx_candidate_skills_skill_id ON candidate_skills (skill_id);
CREATE INDEX idx_job_required_skills_skill_id ON job_required_skills (skill_id);