package com.moti.domain.team;

import com.moti.domain.team.entity.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TeamRepository {

    private final EntityManager em;

    public Long save(Team team) {
        em.persist(team);
        return team.getId();
    }

    public Team findOne(Long teamId) {
        return em.find(Team.class, teamId);
    }

    public List<Team> findAll(int offset, int limit) {
        return em.createQuery("select t from Team t", Team.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public Team findTeamByTeam(Long teamId) {
        try {
            return em.createQuery("select t from Team t" +
                            " join fetch t.teamUsers tu" +
                            " join fetch tu.user u" +
                            " where t.id = :teamId", Team.class)
                    .setParameter("teamId", teamId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Team findTeamByUser(Long userId) {
        try {
            return em.createQuery("select t from Team t" +
                            " join fetch t.teamUsers tu" +
                            " join fetch tu.user u" +
                            " where u.id = :userId", Team.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Long count() {
        return em.createQuery("select count(t) from Team t", Long.class)
                .getSingleResult();
    }

}
