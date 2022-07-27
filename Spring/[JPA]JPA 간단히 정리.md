참고: 이 글은 김영한님의 JPA강의를 듣고 개인 공부용으로 정리한 글입니다.

# 등장 배경

- 객체를 어떻게 객체의 특징을 살려서 '잘' 저장할까? -> 대안이 바로 관계형 DB (RDB)

- 하지만 SQL때문에 무한 반복, 지루한 코드

- 또, 객체와 RDB는 차이가 있다.

- 객체를 자바 컬렉션이 저장하듯이 DB에 저장하게 해주고 SQL도 알아서 다 처리해주는 기술이 바로 **JPA**
</br>
</br>

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

## 3. 영속성 컨텍스트 (Persistence Context)
- JPA에서 가장 중요한 개념.
- EntityManager를 생성하면 영속성 컨텍스트가 생성됨
- em.persist(entity) 하면 영속성 컨텍스트에 저장.
- **엔티티 생명주기**
  * 비영속(new/transient): 영속성 컨텍스트와 전혀 관계가 없는 상태
  * 영속(managed): 영속성 컨텍스트에 저장된 상태
  * 준영속(detached): 영속성 컨텍스트에 저장되었다가 분리된 상태
  * 삭제(removed): 삭제된 상태

</br>

```java
EntityManager entityManager = new EntityManager();

TestClass testClass = new TestClass();
// 비영속

entityManager.persist(testClass);
// 영속

entityManager.detach(testClass);    // 특정 엔티티만.
entityManager.clear();                    // 완전히 초기화
entityManager.close();                    // 종료
// 준영속

entityManager.remove(testClass);
//삭제
```

</br>
</br>

# 성능최적화 기능 (영속성 컨텍스트 이점)

## 1. 1차 캐시와 동일성(identity) 보장

- 영속성 컨텍스트 내부에는 1차캐시가 있다(사실 이것을 영속컨텍스트로 봐도 무방)
- em.find() 하면 db에 조회하기 전에 1차캐시를 조회.
  - 있으면 반환
  - 없으면 db 조회해서 1차캐시에 저장한 후 반환
- 1차캐시는 하나의 트랜잭션 안에서만 효과가 있기때문에 사실 효과가 미비하다.(트랜잭션 끝나면 영속컨텍스트 초기화)

- 동일성 보장
```java
String memberId = "100";
Member m1 = jpa.find(Member.class, memberId); //SQL
Member m2 = jpa.find(Member.class, memberId); //캐시
println(m1 == m2) //true. 총 SQL 1번만 실행
```
</br>
</br>

## 2. 트랜잭션을 지원하는 쓰기 지연(transactional write-behind)

- 영속 컨텍스트 내부에 **'쓰기지연 SQL 저장소'**에 SQL을 저장해놓는다. 

```java
transaction.begin(); 		// 트랜잭션 시작
em.persist(memberA);
em.persist(memberB);
em.persist(memberC);
//여기까지 INSERT SQL을 데이터베이스에 보내지 않는다.
//커밋하는 순간 데이터베이스에 INSERT SQL을 모아서 보낸다.
transaction.commit(); // 트랜잭션 커밋
```
- 참고: em.flush()는 영속 컨텍스트를 비우지 않는다.

</br>
</br>

## 3. 변경 감지
- member.setName()하면 영속컨텍스트에 자동으로 반영됨.
- 원리: db에서 가져온 직후의 엔티티의 정보를 기록해둔 '스냅샷'과 현재 엔티티를 비교해서 달라진것이 있으면 쓰기지연 SQL저장소에 UPDATE SQL 넣어둠.

</br>
</br>

## 4. 지연 로딩(Lazy Loading)

- 지연 로딩: 객체가 실제 사용될 때 로딩 (쿼리를 나중에 필요할 때 날림, 불필요한 SQL 줄여줌)
- 즉시 로딩: JOIN SQL로 한번에 연관된 객체까지 미리 조회