package com.gabrielf.job_matching_platform.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;


    //criado para uso do MatchingService, essencial apenas aqui, porem repassei pra todas entidades por precaucao.
    /* Se não tiver, o .contains() vai comparar por referência de objeto (não por conteúdo), e o matching vai dar
    errado silenciosamente (nunca vai achar match, mesmo quando deveria)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Skill skill)) return false;
        return id != null && id.equals(skill.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
