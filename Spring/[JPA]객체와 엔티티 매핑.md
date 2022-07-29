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

# **2. 연관관계 매핑**


# **3. 다양한 연관관계 매핑**


# **4. 고급 매핑**