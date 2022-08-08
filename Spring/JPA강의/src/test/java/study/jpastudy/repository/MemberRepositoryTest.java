package study.jpastudy.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import study.jpastudy.domain.Address;
import study.jpastudy.domain.AddressEntity;
import study.jpastudy.domain.Member;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;

    @Test
    @Rollback(value = false)
    public void 임베디드테스트() throws Exception {

        Member member = new Member();
        member.setAddress(new Address("hi", "hello", "bye"));

        memberRepository.save(member);
    }

    @Test
    public void 값타입비교() throws Exception {

        int a = 1;
        int b = 1;

        Address address1 = new Address("h", "h", "h");
        Address address2 = new Address("h", "h", "h");
        Address address3 = new Address("k", "k", "k");

        System.out.println(a == b);          // true (내부에 레퍼런스가 아니라 값이 들어있기 때문)

        System.out.println(address1 ==address2);        // false (레퍼런스가 다르기 때문)
        System.out.println(address1.equals(address2));  // false. 재정의 해야함

        String str1 = "hi";
        String str2 = "hi";
        String newStr = new String("hi");

        System.out.println(str1 ==str2);            // true (주소 일치)
        System.out.println(str1 ==newStr);            // false (주소 불일치)
        System.out.println(str1.equals((str2)));    // true ( 값 일치)
        System.out.println(str1.equals((newStr)));    // true ( 값 일치)

        String str3 = "hi";
        String str4 = "hello";

        System.out.println(str3 ==str4);            // false (주소 다름)
        System.out.println(str3.equals((str4)));    // false (값 다름)

    }

    @Test
    @Rollback(value = false)
    public void 값타입컬렉션() throws Exception {
        Member member = new Member();

        member.getAddressHistory().add(new AddressEntity("hi", "hello", "bye"));
        member.getAddressHistory().add(new AddressEntity("hihihi", "hello", "bye"));

        memberRepository.save(member);
        member.getFaboriteFoods().add("족발");
        member.getFaboriteFoods().add("피자");

        System.out.println("==============");


        member.getFaboriteFoods().remove("족발");
        member.getFaboriteFoods().add("한식");

        member.getAddressHistory().remove(new AddressEntity("hi", "hello", "bye"));
        member.getAddressHistory().add(new AddressEntity("newCity", "hello", "bye"));

        memberRepository.save(member);


    }

    @Test
    @Rollback(value = false)
    public void 엔티티컬렉션() throws Exception {
        Member member = new Member();

        member.getAddressHistory().add(new AddressEntity("hi", "hello", "bye"));
        member.getAddressHistory().add(new AddressEntity("hihihi", "hello", "bye"));

        memberRepository.save(member);



    }
}