package study.jpastudy.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.jpastudy.domain.Member;
import study.jpastudy.domain.MemberDTO;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
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
