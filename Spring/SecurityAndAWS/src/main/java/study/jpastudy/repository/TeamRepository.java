package study.jpastudy.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.jpastudy.domain.jpa.Team;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class TeamRepository {
    private final EntityManager em;

    public void save(Team team) {
        em.persist(team);
    }
}
