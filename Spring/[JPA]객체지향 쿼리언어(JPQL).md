참고 : 이 글은 김영한님의 JPA 강의를 들으며 기록한 내용입니다.   

# 소개

## JPA가 제공하는 다양한 쿼리 방법

- **JPQL** : 표준 문법

- JPA Criteria : 자바 코드로 JPQL을 빌드해주는 generator 클래스의 모음. **비추!!**

- **QueryDSL**: 자바 코드로 JPQL을 빌드해주는 generator 클래스의 모음

- 네이티브 SQL: 특정 db에 종속적인 쿼리가 필요할 때.
- JDBC API 직접 사용, MyBatis, SpringJdbcTemplate 함께 사용

## 1. JPQL

- em.find() 말고, 특정 조건의 엔티티만 검색하고싶다면?
- 애플리케이션이 필요한 데이터만 DB에서 불러오려면 결국 검색 조건이 포함된 SQL이 필요
- 검색을 할 때도 테이블이 아닌 엔티티 객체를 대상으로 검색

- SQL과 문법 유사, SELECT, FROM, WHERE, GROUP BY, HAVING, JOIN 지원
- **JPQL은 엔티티 객체를 대상으로 쿼리**
- **(SQL은 데이터베이스 테이블을 대상으로 쿼리)**

- 예시
```java
public List<Member> findMemberOver18() {
    String jpql = "select m From Member m where m.age > 18";
    return em.createQuery(jpql, Member.class).getResultList();
}
```

## 2. queryDSL

- 문자가 아닌 자바코드로 `JPQL`을 작성할 수 있음
- JPQL 빌더 역할

- **컴파일 시점에 문법 오류를 찾을 수 있음**
- 동적쿼리 작성 편리함
- **단순하고 쉬움**
- 실무 사용 권장

- 예시
```java
JPAFactoryQuery query = new JPAQueryFactory(em);
QMember m = QMember.member;
List<Member> list = query.selectFrom(m)
.where(m.age.gt(18))
.orderBy(m.name.desc())
.fetch();
```
- 셋팅이 빡세지만 한번 해두면 편하다 (나중에 제대로 후술)


## 3. Criteria
- 문자가 아닌 자바코드로 JPQL을 작성할 수 있음
- JPQL 빌더 역할,  JPA 공식 기능
- 단점: **너무 복잡하고 실용성이 없다.**, 동적 쿼리를 해결하기 너무 힘들다.
- `Criteria` 대신에 `QueryDSL` 사용 권장

## 4. 네이티브 SQL

- JPA가 제공하는 SQL을 직접 사용하는 기능
- JPQL로 해결할 수 없는 특정 데이터베이스에 의존적인 기능

```java
String sql = “SELECT ID, AGE, TEAM_ID, NAME FROM MEMBER WHERE NAME = ‘kim’ " ;
List<Member> resultList = em.createNativeQuery(sql, Member.class).getResultList();
```

## 5. JDBC 직접 사용, SpringJdbcTemplate 등

- JPA를 사용하면서 JDBC 커넥션을 직접 사용하거나, 스프링 JdbcTemplate, 마이바티스등을 함께 사용 가능
- 단 영속성 컨텍스트를 적절한 시점에 강제로 플러시 필요

<br>
<br>



# JPQL 기본 문법과 쿼리 API

## 기본 문법

- `select m from Member as m where m.age > 18`

- 엔티티와 속성은 대소문자 구분O (`Member`, `age`)
- JPQL 키워드는 대소문자 구분X (`SELECT`, `FROM`, `where`)
- **엔티티 이름 사용**, 테이블 이름이 아님(`Member`)
- 별칭은 필수(m) (as는 생략가능)

<br>
<br>


## TypeQuery, Query

- `TypeQuery`: 반환 타입이 명확할 때 사용

```java
TypedQuery<Member> query = em.createQuery("SELECT m FROM Member m", Member.class);
List<Member> result = query.getResultList();
```

- `Query`: 반환 타입이 명확하지 않을 때 사용

```java
Query query = em.createQuery("SELECT m.username, m.age from Member m");
// String과 int가 서로 다르기때문에 타입을 명기할 수 없다.
```

<br>
<br>


## 결과 조회 API

- `query.getResultList()`: 결과가 하나 이상일 때, 리스트 반환
    - 결과가 없으면 빈 리스트 반환 -> `nullPointerException` 걱정 안해도 된다!

- `query.getSingleResult()`: **단일 객체 반환**, **결과가 정확히 하나일때만 써야함**
    - 결과가 없으면: javax.persistence.NoResultException
    - 둘 이상이면: javax.persistence.NonUniqueResultException
    - 나중에 spring data jpa에서는 예외처리 해주는것 배운다.


<br>
<br>


## 파라미터 바인딩

- jpql 내부의 `:변수` 에 바인딩 해준다. 

- 이름기준 : 

```java
// Repository
public List<Member> findByName(String name1) {
    return em.createQuery("select m from Member m where m.name = :givenName", Member.class)
            .setParameter("givenName", name1)
            .getResultList();
}
```

- 위치 기준: (왠만하면 쓰지 말자!!)
```java
// Repository
public List<Member> findByName(String name1) {
    return em.createQuery("select m from Member m where m.name = ?1", Member.class)
            .setParameter(1, name1)
            .getResultList();
}
```
## 중요사항

- **위와 같이 JPQL로 select 쿼리의 결과로 받은 List<Member>의 Member들은 모두 영속성 컨텍스트에서 관리된다!!**

<br>
<br>


# 프로젝션(SELECT)

## 프로젝션이란?

- : `SELECT` 절에 조회할 대상을 지정하는 것
- RDB는 숫자, 문자 등 기본 데이터 타입만 지정할 수 있는데 반해서 JPQL은 다양하게 가능.

<br>
<br>


## 프로젝션 대상: **엔티티, 임베디드 타입, 스칼라 타입**(숫자, 문자등 기본 데이터 타입)

- `SELECT m FROM Member m` -> 엔티티 프로젝션

- `SELECT m.team FROM Member m` -> 엔티티 프로젝션
    - `SELECT t FROM Member m join m.team t ` 로 쓰는게 더 낫다.
- `SELECT m.address FROM Member m` -> 임베디드 타입 프로젝션
 -> 스칼라 타입 프로젝션
- `DISTINCT`로 중복 제거 가능.

<br>
<br>


## 여러 값 조회

- `SELECT m.username, m.age FROM Member m`

1. `Query` 타입으로 조회 (타입이 명확하지 않기 때문)

```java
public Object[] findNameAge() {
    List resultList = em.createQuery("select m.name, m.age from Member m")
            .getResultList();
    Object o = resultList.get(0);
    Object[] result = (Object[]) o;
    return result;
}
```

2. `Object[]` 타입으로 조회

```java
public Object[] findNameAge() {
    List<Object[]> resultList = em.createQuery("select m.name, m.age from Member m")
            .getResultList();
    Object[] o = resultList.get(0);
    return o;
}
```

3. new 명령어로 조회 (**추천!**)

- 우선 `List`에 담을 `DTO` 만들자 (생성자 필요)

```java
@Getter @Setter
@AllArgsConstructor
public class MemberDTO {

    private String name;
    private int age;
}
```
- 그 후 조회할 때 `new`로 `DTO`를 생성하며 받아오자

```java
public List<MemberDTO> findNameAgeByNew() {
    List<MemberDTO> resultList = em.createQuery("select new study.jpastudy.domain.MemberDTO(m.name, m.age) from Member m", MemberDTO.class)
            .getResultList();

    return resultList;
    }
```

<br>
<br>


# 페이징

## API

- `setFirstResult(int startPosition)` : **조회 시작 위치** (0부터 시작)
- `setMaxResults(int maxResult)` : **조회할 데이터 수**


<br>
<br>


## 예시

```java
public List<Member> findMemberByPaging() {
    return em.createQuery("select m from Member m order by m.age desc ", Member.class)
            .setFirstResult(1)      // default = 0
            .setMaxResults(2)
            .getResultList();
}
```

- age가 1, 2, 3, 4, 5, 6인 `Member`들이 db에 있었다면 나이가 5, 4인 엔티티가 리스트에 담긴다.
- 각 db에 맞는 방언에 맞게 알아서 쿼리가 나간다.

<br>
<br>


# 조인

## 조인이란?

- 두개 이상의 테이블에 대해서 결합하여 나타낼 때 사용
- ON : 조인 대상 필터링, 연관관계 없는 엔티티 외부 조인

## 1. Inner JOIN

- `SELECT m FROM Member m JOIN m.team t`
- 두 테이블의 교집합 ( Member & Team )

- 쉬운 예시

```java
public List<Member> findByTeamJoin(String teamName) {
        return em.createQuery("select m from Member m join m.team t on t.name = :givenName", Member.class)
                .setParameter("givenName", teamName)
                .getResultList();
    }
```

<br>
<br>


## 2. Outer JOIN

- `SELECT m FROM Member m LEFT JOIN m.team t`
- 왼쪽 테이블 ( Member )

- `A LEFT JOIN B` 와 `B RIGHT JOIN A`는 완전히 같은 식이다. 

<br>
<br>


## 3. 세타 JOIN ( Cross JOIN )

- 연관관계가 없을 때 사용.
` select m from Member m, Team t where m.username = t.name`
` select m from Member m join Team t on m.username = t.name`


<br>
<br>


# 서브 쿼리

- 아직 이해가 잘 안 간다. 나중에 다시 정리하자.

## 예시

- 나이가 평균보다 많은 회원
- `select m from Member m where m.age > (select avg(m2.age) from Member m2)`

- 한 건이라도 주문한 고객
- `select m from Member m where (select count(o) from Order o where m = o.member) > 0`

## EXISTS : 서브쿼리에 결과가 존재하면 참

- 팀A 소속인 회원

- `select m from Member m where exists (select t from m.team t where t.name = ‘팀A')`

## ALL : 모두 만족하면 참

- 전체 상품 각각의 재고보다 주문량이 많은 주문들

- `select o from Order o where o.orderAmount > ALL (select p.stockAmount from Product p)`

## ANY, SOME : 하나라도 만족하면 참

- 어떤 팀이든 팀에 소속된 회원

- `select m from Member m where m.team = ANY (select t from Team t)`

## IN : 서브쿼리의 결과 중 하나라도 같은 것이 있으면 참

## JPA 서브쿼리의 한계
- `FROM` 절의 서브 쿼리는 현재 `JPQL`에서 불가능
    - 조인으로 풀 수 있으면 풀어서 해결


<br>
<br>


# JPQL 타입 표현과 기타식

- 아직 써보지 않아서 와닿지 않는다. 나중에 필요할 때 다시 보자.

## 타입 표현
- 문자: `‘HELLO’`, `'She’’s'`
- 숫자: `10L`(Long), `10D`(Double), `10F`(Float)
- Boolean: `TRUE`, `FALSE`
- ENUM: `jpabook.MemberType.Admin` (패키지명 포함)
    - `select m from Member m where m.type = jpql.MemberType.ADMIN`
- 엔티티 타입: `TYPE(m) = Member` (상속 관계에서 사용)
    - `select i from Item i where type(i) = Book`

## SQL과 문법이 같은 식 (표준 SQL 문법은 거의 다 지원)

- EXISTS, IN
- AND, OR, NOT
- =, >, >=, <, <=, <>
- BETWEEN, LIKE, IS NULL

<br>
<br>


# 조건식

- 여기부터는 나중에 필요할 때 강의듣고 정리..

<br>
<br>


# JPQL 함수