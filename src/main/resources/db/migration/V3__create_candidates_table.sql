CREATE TABLE candidates (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            user_id UUID NOT NULL UNIQUE,
                            bio TEXT,
                            created_at TIMESTAMP NOT NULL,
                            CONSTRAINT fk_candidates_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE candidate_skills (
                                  candidate_id UUID NOT NULL,
                                  skill_id UUID NOT NULL,
                                  PRIMARY KEY (candidate_id, skill_id),
                                  CONSTRAINT fk_candidate_skills_candidate FOREIGN KEY (candidate_id) REFERENCES candidates (id),
                                  CONSTRAINT fk_candidate_skills_skill FOREIGN KEY (skill_id) REFERENCES skills (id)
);