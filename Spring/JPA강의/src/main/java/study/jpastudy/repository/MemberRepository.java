package study.jpastudy.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.jpastudy.domain.Member;
import study.jpastudy.domain.MemberDTO;
import study.jpastudy.domain.Team;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    public List<Member> findAll() {
        String jpql = "select m From Member m";
        return em.createQuery(jpql, Member.class).getResultList();
    }

    public List<Member> findMemberOver18() {
        String jpql = "select m From Member m where m.age > 18";
        return em.createQuery(jpql, Member.class).getResultList();
    }

    public List<String> findMemberName() {
        String jpql = "select m.name From Member m";
        return em.createQuery(jpql, String.class).getResultList();
    }

    public List<Member> findByName(String name1) {
        return em.createQuery("select m from Member m where m.name = :givenName", Member.class)
                .setParameter("givenName", name1)
                .getResultList();
    }

    public Object[] findNameAge() {
        List<Object[]> resultList = em.createQuery("select m.name, m.age from Member m")
                .getResultList();
        Object[] o = resultList.get(0);
        return o;
    }

    public List<MemberDTO> findNameAgeByNew() {
        List<MemberDTO> resultList = em.createQuery("select new study.jpastudy.domain.MemberDTO(m.name, m.age) from Member m", MemberDTO.class)
                .getResultList();

        return resultList;
    }

    public List<Member> findMemberByPaging() {
        return em.createQuery("select m from Member m order by m.age desc ", Member.class)
                .setFirstResult(1)
                .setMaxResults(2)
                .getResultList();
    }

    public List<Member> findByInnerJoin() {
        return em.createQuery("select m from Member m join m.team t", Member.class)
                .getResultList();
    }

    public List<Member> findByOuterJoin() {
        return em.createQuery("select m from Member m left join m.team t", Member.class)
                .getResultList();
    }

    public List<Member> findByTeamJoin(String teamName) {
        return em.createQuery("select m from Member m join m.team t on t.name = :givenName", Member.class)
                .setParameter("givenName", teamName)
                .getResultList();
    }

    public List<Member> findOlderMember() {
        return em.createQuery("select m from Member m where m.age > ( select avg(m2.age) from Member m2)"
                        , Member.class)
                .getResultList();
    }

    public List<Member> findTeamMember() {
        return em.createQuery("select m from Member m where exists (select t from m.team t where t.name = 'dream')"
                        , Member.class)
                .getResultList();
    }

    // QueryDSL 사용
//    public List<Member> findMemberOver18queryDSL() {
//        JPAFactoryQuery query = new JPAQueryFactory(em);
//        QMember m = QMember.member;
//        List<Member> list = query.selectFrom(m)
//                .where(m.age.gt(18))
//                .orderBy(m.name.desc())
//                .fetch();
//        return list;
//    }
}
