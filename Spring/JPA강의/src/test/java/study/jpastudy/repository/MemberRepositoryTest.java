package study.jpastudy.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import study.jpastudy.domain.*;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;

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

//    @Test
//    @Rollback(value = false)
//    public void 값타입컬렉션() throws Exception {
//        Member member = new Member();
//
//        member.getAddressHistory().add(new AddressEntity("hi", "hello", "bye"));
//        member.getAddressHistory().add(new AddressEntity("hihihi", "hello", "bye"));
//
//        memberRepository.save(member);
//        member.getFaboriteFoods().add("족발");
//        member.getFaboriteFoods().add("피자");
//
//        System.out.println("==============");
//
//
//        member.getFaboriteFoods().remove("족발");
//        member.getFaboriteFoods().add("한식");
//
//        member.getAddressHistory().remove(new AddressEntity("hi", "hello", "bye"));
//        member.getAddressHistory().add(new AddressEntity("newCity", "hello", "bye"));
//
//        memberRepository.save(member);
//    }

//    @Test
//    public void 엔티티컬렉션() throws Exception {
//        Member member = new Member();
//
//        member.getAddressHistory().add(new AddressEntity("hi", "hello", "bye"));
//        member.getAddressHistory().add(new AddressEntity("hihihi", "hello", "bye"));
//
//        memberRepository.save(member);
//
//    }

    @Test
    public void JPQL테스트() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("song"); member1.setAge(20);

        Member member2 = new Member();
        member2.setName("kim"); member2.setAge(10);

        Member member3 = new Member();
        member3.setName("Lee"); member3.setAge(30);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        //when
        List<Member> memberOver18s = memberRepository.findMemberOver18();

        //then
        for(Member member: memberOver18s) {
            System.out.println("member === > "+ member.getName());
        }
    }


    @Test
    public void JPQL테스트2() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("song"); member1.setAge(20);

        Member member2 = new Member();
        member2.setName("kim"); member2.setAge(10);

        Member member3 = new Member();
        member3.setName("Lee"); member3.setAge(30);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        //when
        List<String> memberNames = memberRepository.findMemberName();

        //then
        for(String memberName: memberNames) {
            System.out.println("member === > "+ memberName);
        }
    }

    @Test
    public void JPQL_findByName() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("song"); member1.setAge(20);

        Member member2 = new Member();
        member2.setName("kim"); member2.setAge(10);

        Member member3 = new Member();
        member3.setName("Lee"); member3.setAge(30);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        //when
        List<Member> members = memberRepository.findByName("song");

        //then
        for(Member member: members) {
            System.out.println("member === > "+ member.getName());
        }
    }

    @Test
    public void JPQL_findNameAge() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("song"); member1.setAge(20);

        Member member2 = new Member();
        member2.setName("kim"); member2.setAge(10);

        Member member3 = new Member();
        member3.setName("Lee"); member3.setAge(30);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        //when
        Object[] nameAndAge = memberRepository.findNameAge();

        //then
        System.out.println("name = " + nameAndAge[0]);
        System.out.println("age = " + nameAndAge[1]);
    }

    @Test
    public void JPQL_findNameAgeByNew() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("song"); member1.setAge(20);

        Member member2 = new Member();
        member2.setName("kim"); member2.setAge(10);

        Member member3 = new Member();
        member3.setName("Lee"); member3.setAge(30);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        //when
        List<MemberDTO> nameAndAgeList = memberRepository.findNameAgeByNew();

        //then
        for(MemberDTO memberDTO: nameAndAgeList) {
            System.out.println("member === > "+ memberDTO.getName() + memberDTO.getAge());
        }
    }


    @Test
    public void JPQL_findByPaging() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("song"); member1.setAge(10);

        Member member2 = new Member();
        member2.setName("kim"); member2.setAge(20);

        Member member3 = new Member();
        member3.setName("Lee"); member3.setAge(30);

        Member member4 = new Member();
        member4.setName("ki"); member4.setAge(50);

        Member member5 = new Member();
        member5.setName("Leeef"); member5.setAge(60);

        Member member6 = new Member();
        member6.setName("Leem"); member6.setAge(70);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
        memberRepository.save(member5);
        memberRepository.save(member6);

        //when
        List<Member> members = memberRepository.findMemberByPaging();

        //then
        for(Member member: members) {
            System.out.println(member);
        }
    }

    @Test
    public void JPQL_Join() throws Exception {
        //given
        Team dream = new Team(); dream.setName("dream");
        Team happy = new Team(); happy.setName("happy");
        Team devil = new Team(); devil.setName("devil");

        Member member1 = new Member();
        member1.setName("song"); member1.setAge(20); member1.setTeam(dream);

        Member member2 = new Member();
        member2.setName("kim"); member2.setAge(10); member2.setTeam(dream);

        Member member3 = new Member();
        member3.setName("Lee"); member3.setAge(30); member3.setTeam(happy);

        Member member4 = new Member();
        member4.setName("jo"); member4.setAge(50);

        teamRepository.save(dream);
        teamRepository.save(happy);
        teamRepository.save(devil);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);

        //when
        // List<Member> members = memberRepository.findAll(); 이것과의 차이가 뭐지??
        List<Member> members = memberRepository.findByInnerJoin();

        //then
        for(Member member: members) {
            System.out.println(member);
        }

        List<Member> members2 = memberRepository.findByOuterJoin();

        //then
        for(Member member: members2) {
            System.out.println(member);
        }

        List<Member> members3 = memberRepository.findByTeamJoin("dream");

        //then
        for(Member member: members3) {
            System.out.println(member);
        }
    }
}