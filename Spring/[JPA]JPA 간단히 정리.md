참고: 이 글은 김영한님의 JPA강의를 듣고 개인 공부용으로 정리한 글입니다.

# 등장 배경

- 객체를 어떻게 객체의 특징을 살려서 '잘' 저장할까? -> 대안이 바로 관계형 DB (RDB)

- 하지만 SQL때문에 무한 반복, 지루한 코드

- 또, 객체와 RDB는 차이가 있다.

- 객체를 자바 컬렉션이 저장하듯이 DB에 저장하게 해주고 SQL도 알아서 다 처리해주는 기술이 바로 **JPA**

# 특징

## 1. ORM
- JPA는 자바 진영의 ORM 기술 표준이다. (object-relational mapping)
- ORM:
  - 객체는 객체대로 설계하고, RDB는 RDB대로 설계.
  - ORM 프레임워크가 중간에서 매핑해준다.

- JPA는 java app과 jdbc 사이에서 동작하여 jdbc api와 db간의 통신을 처리해준다.

## 2. 생산성 (CRUD)

- 저장: jpa.persist(member)
- 조회: Member member = jpa.find(memberId)
- 수정★: member.setName(“변경할 이름”) ->          **알아서 DB에서 수정된다!!!**
- 삭제: jpa.remove(member)

## 3. 성능최적화 기능

1. 1차 캐시와 동일성(identity) 보장

```java
String memberId = "100";
Member m1 = jpa.find(Member.class, memberId); //SQL
Member m2 = jpa.find(Member.class, memberId); //캐시
println(m1 == m2) //true. 총 SQL 1번만 실행
```


2. 트랜잭션을 지원하는 쓰기 지연(transactional write-behind)

```java
transaction.begin(); 		// 트랜잭션 시작
em.persist(memberA);
em.persist(memberB);
em.persist(memberC);
//여기까지 INSERT SQL을 데이터베이스에 보내지 않는다.
//커밋하는 순간 데이터베이스에 INSERT SQL을 모아서 보낸다.
transaction.commit(); // 트랜잭션 커밋
```


3. 지연 로딩(Lazy Loading)

- 지연 로딩: 객체가 실제 사용될 때 로딩 (불필요한 SQL 줄여줌)
- 즉시 로딩: JOIN SQL로 한번에 연관된 객체까지 미리 조회