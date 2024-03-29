package com.moti.domain.team;

import com.moti.domain.team.entity.Team;
import com.moti.domain.user.entity.User;
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

    public List<Team> findTeamsByUser(User user) {
        return em.createQuery("select t from Team t" +
                " join t.teamUsers tu" +
                " where tu.user = :user", Team.class)
                .setParameter("user", user)
                .getResultList();
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

    public Long count() {
        return em.createQuery("select count(t) from Team t", Long.class)
                .getSingleResult();
    }

    public void delete(Team team) {
        em.remove(team);
    }

    public void deleteAll() {
        em.createQuery("delete from Team").executeUpdate();
    }

}
