참고: 이 글은 김영한님의 JPA강의를 듣고 개인 공부용으로 정리한 글입니다.

# **1. 엔티티 매핑**

# 1-1. 객체와 테이블 매핑

## @Entity
 - @Entity가 붙은 클래스는 JPA가 관리하며, 엔티티 라고 한다.
 - 주의
    - 기본 생성자 필수(파라미터 없는 `public` or `protected`)
    - `final, enum, interface, inner` 클래스는 사용 불가.
    - DB에 저장할 필드에 `final` 사용 X


</br>

## @Table
- 엔티티와 매핑할 테이블 지정
- name 속성 : 메핑할 테이블 이름 지정 (기본값 : 엔티티 이름)

</br>

## db 스키마 자동 생성

- 참고: DDL ; `CREATE, ALTER, DROP`과 같이 테이블과 같은 데이터 구조를 정의하는데 사용되는 명령어들로 (생성, 변경, 삭제, 이름변경) 데이터 구조와 관련된 명령어들을 말함. 

- DDL을 앱 실행시점에 자동 생성.
- db 방언을 활용해서 db에 맞는 적절한 DDL 생성
- 이렇게 생성된 DDL은 앱 개발서버에서만 사용하자. 
```yml
spring:
  jpa:
    database-platform: org.hibernate.dialect.MySQL5InnoDBDialect   (방언)

    hibernate:
      ddl-auto: create            (스키마 자동생성 속성)
```

- 속성들

| Option | Description | 사용 시기 |
|:--:|:--:|:--:|
| create | 기존 테이블 삭제 후 다시 생성 (DROP + CREATE) | 개발 초기 |
| create-drop | create와 같으나 종료시점에 테이블 DROP |
| update | 변경사항만 반영 | 개발 초기 또는 테스트 서버 |
| validate | 엔티티와 테이블이 정상 매핑되었는지만 확인 | 테스트 서버 또는 운영서버 |
| none | 사용X | 운영서버 |

</br>

# 1-2. 필드와 컬럼 매핑

</br>

## 사용 예시

```java
@Entity
public class Member {

    @Id
    private Long id;

    @Column(name = "name")
    private String username;

    private LocalDateTime enrollTime;     // java8부터는 @Temporal 안쓰고 이렇게 쓰면 된다.

    @Enumerated(EnumType.STRING)      // enum 타입 매핑. ORDINAL 사용 XXX!!!
    private RoleType roleType;

    @Lob
    private String description;

    @Transient
    private String myDescription;         // db 컬럼에 매핑하지 않음.

}
```

</br>

## @Column 주요 속성

| Option | Description | Default |
|:--:|:--:|:--:|
| name | 테이블의 컬럼 이름 | 객체 필드 이름 |
| insertable, updatable | 등록, 변경 가능 여부 | True |
| nullable | null값 허용 여부 |  |
| length | 문자 길이 제약조건. String에만 사용 | 255 |

# 1-3. 기본 키 매핑

## 방법
- 직접 할당 : `@Id`만 사용
- 자동 생성 : `@GeneratedValue`
- 1. IDENTITY : db에 위임, MYSQL (auto - increament)
    - `em.persist()` 하는 시점에 바로 `INSERT` SQL을 날린다!
    - db에 넣어야 id값이 생성되기 때문.
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

- 2. SEQUENCE : db 시퀀스 오브젝트 사용, ORACLE. `@SequenceGenerator` 필요
    - db 시퀀스 오브젝트는 유일한 값을 순서대로 생성하는 특별한 db 오브젝트.
    - `allocationSize` : 성능 최적화에 도움(기본값 50: 50번째마다만 db 들어가서 갱신)

```java
@Entity
@SequenceGenerator(
name = “MEMBER_SEQ_GENERATOR",
sequenceName = “MEMBER_SEQ", //매핑할 데이터베이스 시퀀스 이름
initialValue = 1, allocationSize = 50)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
    generator = "MEMBER_SEQ_GENERATOR")
    private Long id;
}
```

- 3. TALBE : 키 생성용 테이블 사용, 모든 DB에서 사용 가능 but 성능이슈. `@TableGenerator` 필요
- 4. AUTO : 방언에 따라 자동 지정, 기본값.

## 권장하는 전략
- 조건: null 아님, 유일, 변하면 안된다.
- 이 조건을 만족하는 자연키(예: 주민번호)는 찾기 힘들다. 대체키를 사용하자. (비즈니스로직을 키로 끌고오면 안됨)
- 권장 : Long + 대체키 + `@GeneratedValue` 사용(`IDENTITY` 또는 `SEQUENCE` 사용하자)

</br>

</br>

# **2. 연관관계 매핑**

- 객체를 테이블이 맞추어 모델링하지 말자
    - 예를 들어, Member 엔티티에 필드로 TeamId를 놓으면 안 된다.
    - 협력관계를 만들 수 없다. Member가 속한 Team을 구하고싶을 때 em.find()를 두 번 써야한다.

</br>

# 2-1. 단방향 연관관계

- Member
```java
@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue()
    @Column(name = "member_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

}
```
- Team
```java
@Entity
@Getter @Setter
public class Team {

    @Id @GeneratedValue()
    @Column(name = "team_id")
    private Long id;
}
```

- 가장 많이 사용하는 연관관계다.

</br>

# 2-2. 양방향 연관관계

- Member : 위와 같다.

- Team
```java
@Entity
@Getter @Setter
public class Team {

    @Id @GeneratedValue()
    private Long id;

    @OneToMany(mappedBy = "team")
    List<Member> members = new ArrayList<Member>();
}
```
- Team에서 속한 Member들 조회할 일이 필요할 때에 업그레이드 해주면 된다.

</br>

## 연관관계의 주인

- `mappedBy` = JPA에서 가장 어려운 부분!

- 테이블의 양방향 연관관계 : MEMBER.TEAM_ID 외래 키 하나로 **양방향** 연관관계 가짐 (양쪽으로 조인할 수 있다.)

```SQL
SELECT * FROM MEMBER M
JOIN TEAM T ON M.TEAM_ID = T.TEAM_ID

SELECT *
FROM TEAM T
JOIN MEMBER M ON T.TEAM_ID = M.TEAM_ID
```

- 객체의 양방향 관계 : 사실 서로 다른 단방향 관계 2개. (Member클래스에는 Team 필드, Team클래스에는 members 필드)

- Member클래스의 Team과 Team클래스의 members중 **누구를 수정했을 때 db의 외래키가 바뀌게** 해야할까?
    - db의 외래 키가 있는쪽을 주인으로 정하자. (다 쪽)
    - 두 관계중 하나를 연관관계의 주인으로 지정하여 외래 키로 관리해야 한다.   
    - 연관관계의 **주인만 외래 키를 관리**(등록, 수정)
    - 주인이 아닌쪽은 `mappedBy` 속성으로 주인을 지정하며 **읽기만 가능**
    - `members`를 아무리 만져봐야 db가 변경되지 않는다.

</br>

## 연관관계 편의 메서드
- 순수 객체 상태를 고려해서 항상 양쪽에 값을 설정하자
- 연관관계 편의 메서드 생성
- 무한루프 조심.
- 다 쪽에 만들지 일 쪽에 만들지는 상황마다 다르다. 다 쪽에 만들 경우 코드 :

```java
    public void setTeam(Team team) {
        this.team = team;
        team.getMembers.add(this);
    }
```

</br>

## 정리

- 단방향 매핑만으로도 이미 연관관계 매핑은 완료된다. (테이블 완료)
- 일단 단방향 매핑을 하고, 나중에 반대방향 조회가 필요할 때 양방향으로 업데이트 하자.
- `@ManyToOne` 주요 속성

| Option | Description | Default |
|:--:|:--:|:--:|
| fetch | 글로벌 페치 전략을 설정 | @ManyToOne=FetchType.EAGER  </br> @OneToMany=FetchType.LAZY |
| cascade | 영속성 전이 기능을 사용 |  |


</br>

- `@OneToMany` 주요 속성

| Option | Description | Default |
|:--:|:--:|:--:|
| mappedBy | 연관관계 주인 필드를 선택 |  |
| fetch | 글로벌 페치 전략을 설정 | @ManyToOne=FetchType.EAGER  </br> @OneToMany=FetchType.LAZY |
| cascade | 영속성 전이 기능을 사용 |  |


</br>

# **3. 다양한 연관관계 매핑**

# 3-1. 일대다

- 다대일이 앞서 설명했던, `Member`클래스에 `Team` 필드가 있는 관계.
- 일대다는 '일'이 연관관계의 주인이다 (`Team`)
- `Team` 객체의 `members` 리스트를 수정하면 db의 `MEMBER`의 외래 키를 `UPDATE` 해야된다 (어색해진다. 큰 단점)
- 단점이 많다. 다대일을 사용하자.

```java
@OneToMany
@JoinColumn(name = "TEAM_ID")
private List<Member> members = new ArrayList<>();
```
- 위와 같이 `@JoinColumn`을 꼭 써줘야 한다.

</br>

</br>

# 3-2. 일대일

- 똑같이, 외래키가 있는 곳이 연관관계의 주인.
- 외래 키에 유니크 제약조건 필요

## 주 테이블에 외래 키 (MEMBER 테이블에 LOCKER_ID)

- 객체지향 개발자 선호
- JPA 매핑 편리
- 장점: 주 테이블만 조회해도 대상 테이블에 데이터가 있는지 확인 가능
- 단점: 값이 없으면 외래 키에 null 허용
- 영한님이 선호하는 방식.

```java
@Entity
public class Member {

    @Id @GeneratedValue()
    @Column(name = "member_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "locker_id")
    private Locker locker;
}
```

```java
@Entity
public class Locker {

    @Id @GeneratedValue()
    @Column(name = "locker_id")
    private Long id;

    // 양방향일 경우 추가
    @OneToOne(mappedBy = "locker")
    private Member member;
}
```

## 대상 테이블에 외래 키

- 대상 테이블에 외래 키가 존재
- 전통적인 데이터베이스 개발자 선호
- 장점: 주 테이블과 대상 테이블을 일대일에서 일대다 관계로 변경할 때 테이블 구조 유지
- 단점: 프록시 기능의 한계로 지연 로딩으로 설정해도 항상 즉시 로딩됨

</br>

</br>

# 3-3. 다대다

- 실무에서는 다대다 관계를 쓰지 않는다. (`@ManyToMany`)
- RDB는 테이블 2개로 다애다 관계 표현 불가.
- 연결 테이블을 추가해서 일대다, 다대일 관계로 풀어야한다.
- 그러기 위해 연결 테이블용 엔티티를 추가해야한다.

</br>

</br>


# **4. 고급 매핑**

# 상속관계 매핑

- 객체는 상속관계가 있지만, RDB는 상속 관계가 없다!
- 슈퍼타입 서브타입 관계가 객체의 상속과 유사

- @Inheritance(strategy=InheritanceType.XXX)
- SINGLE_TABLE: 단일 테이블 전략
- JOINED: 조인 전략
- TABLE_PER_CLASS: 구현 클래스마다 테이블 전략

</br>

</br>

## 1. 통합 테이블로 변환 -> 단일 테이블 전략

- 객체관계와 RDB 그림



- Item (부모)
```java
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="dtype")          # 안 써줘도 디폴트로 생성
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private  Long id;

    private String name;
    private int price;
    private int stockQuantity;
}
```

- Book (자식중 하나)
```java
@Entity
@DiscriminatorValue("B")
public class Book extends Item {

    private String author;
    private String isbn;
}
```
- 장점:
    - 조인이 필요 없으므로 조회 성능 빠름
    - 조회 쿼리 단순
- 단점:
    - 자식 엔티티가 매핑한 컬럼은 모두 null 허용
    - 테이블이 커질 수 있다. -> 상황에 따라 조회 성능이 오히려 느려질 수 있다.


</br>

</br>

## 2. 각각 테이블로 변환 -> 조인 전략

- 객체관계와 RDB 그림


- 코드 (나머지는 위와 같다)

```java
@Inheritance(strategy = InheritanceType.JOINED)
```

- 장점:
    - 테이블 정규화
    - 외래 키 참조 무결성 제약조건 활용가능(아직 이해 X)
    - 저장공간 효율화
- 단점:
    - 조회시 JOIN 많이 사용 -> 성능 저하
    - 조회 쿼리 복잡
    - 데이터 저장시 INSERT 쿼리 2번 호출


</br>

</br>



## 3. 서브타입 테이블로 변환 -> 구현 클래스마다 테이블 전략

- 객체관계와 RDB 그림


- 조인 전략에서 ITEM 테이블을 없애고 밑으로 내렸다고 생각하면 쉽다.
- DB 설계자와 ORM 전문가 둘 다 싫어하는 전략.
- 여러 자식 테이블을 함께 조회할 때 성능이 느림(UNION SQL 필요)

## 4. 정리
- 단순한 프로젝트면 단일 테이블 
- 보통의 프로젝트라면 조인 테이블 사용

# @MappedSuperclass

- 그림

- 코드

```java
@MappedSuperclass
@Getter @Setter
public class BaseEntity {
    
    private String createdBy;
    private LocalDateTime createdDate;
}

...


@Entity
@Getter @Setter
public class Member extends BaseEntity {
    ...
}
```

- 공통 매핑 정보가 필요할 때 사용(id, name)
- 상속관계 매핑X, 엔티티X, 테이블과 매핑X
- 부모 클래스를 상속 받는 자식 클래스에 매핑 정보만 제공
- 조회, 검색 불가(em.find(BaseEntity) 불가)
- 직접 생성해서 사용할 일이 없으므로 추상 클래스 권장
- 테이블과 관계 없고, 단순히 엔티티가 공통으로 사용하는 매핑정보를 모으는 역할
- 주로 등록일, 수정일, 등록자, 수정자 같은 전체 엔티티에서 공통으로 적용하는 정보를 모을 때 사용
- 참고: @Entity 클래스는 엔티티나 @MappedSuperclass로 지정한 클래스만 상속 가능