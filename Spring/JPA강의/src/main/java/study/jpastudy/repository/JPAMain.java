package study.jpastudy.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import study.jpastudy.domain.Member;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

@Repository
@RequiredArgsConstructor
public class JPAMain {



    public static void main(String[] args) {

        Member m1 = em.find(Member.class, member1.getId());
        Member m2 = em.getReference(Member.class, member2.getId());

        System.out.println(m1.getClass() == m2.getClass());     // false
        System.out.println(m2 instanceof Member);     // true
    }

}
