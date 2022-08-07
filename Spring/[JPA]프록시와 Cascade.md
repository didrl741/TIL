참고 : 이 글은 스프링을 이용하여 간단한 게시판을 만드는 과정에서 일어난 일들을 기록한 것입니다.   

# 트러블슈팅

## 문제상황

- 특정 게시글을 클라이언트 화면에서 삭제하는 과정에서, 종종 다음과 같은 에러가 났다.

```java
java.sql.SQLIntegrityConstraintViolationException: Cannot delete or update a parent row: a foreign key constraint fails (`syk-board`.`user_like_post`, CONSTRAINT `FK3ep7opmtdheue5q9xidc3e36` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`))
```

- 몇번 삭제와 생성을 반복하다보니 좋아요가 달린 게시글을 삭제했을 때만 에러가 나타나는 것을 확인했다.

<br>
<br>


## 해결

- 위 에러는 게시글을 삭제할 경우 좋아요 엔티티의 외래키인 `post_id` 가` null`값이 발생하는 에러인 것 같다.
- 생각해보니 좋아요 객체는 자기가 속한 게시글이 삭제되면 자동으로 같이 삭제되어야한다.
- 따라서 다음가 같이 `Cascade` 속성을 추가해서 해결했다.

```java
@Entity
public class Post {

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<UserLikePost> userLikePosts = new ArrayList<UserLikePost>();

}
```

<br>
<br>


# **정리**

# 프록시

## `em.find()` VS `em.getReference()`

- `em.find()` : db를 통해 실제 엔티티 객체 조회 (SELECT 쿼리 나감)
- `em.getReference()` : db 조회를 미루는 가짜(프록시) 엔티티 객체 조회 (쿼리 안나감)

<br>
<br>


- 프록시 특징
    - 실제 클래스를 상속받아서 만들어짐
    - 실제 클래스와 겉 모양이 같다. (껍데기)
    - 사용하는 입장에서는 진짜인지 프록시인지 몰라도 그냥 사용하면 된다.
    - 프록시 객체는 실제 객체의 참조를 보관한다.
    - 프록시 객체를 호출하면 프록시 객체는 실제 객체의 메소드를 호출한다.

<br>
<br>


- 프록시 객체의 초기화
    - 그림과 같이, 메서드를 호출 시 강제 초기화가 된다. (SELECT 쿼리가 그때서야 나간다)

    ![초기화](https://user-images.githubusercontent.com/97036481/183278597-c6f797d8-115b-4489-9e59-edf691383b9c.png)


    - 프록시는 처음 사용할 때 처음 한 번만 초기화.
    - 프록시 객체가 **실제 엔티티로 바뀌는게 아니라, 프록시 객체를 통해 실제 엔티티에 접근 가능!**
    - 타입 체크시 주의 : (== 말고 instance of 사용하자)

    ```java
            Member m1 = em.find(Member.class, member1.getId());
            Member m2 = em.getReference(Member.class, member2.getId());

            System.out.println(m1.getClass() == m2.getClass());     // false
            System.out.println(m2 instanceof Member);     // true
    ```

    - 영속컨텍스트에 이미 그 엔티티가 있으면 `getReference()` 해도 실제 엔티티 반환. (JPA에서는 같은 트랜잭션 안에서 == 하면 true 보장)
    - **준영속 상태일 때 프록시를 초기화**하면 `LazyInitializationException` 예외를 터트린다! (실무에서 자주 맞딱뜨림)


<br>
<br>


# 즉시로딩과 지연로딩

## FetchType.LAZY

```java
@Entity
@Table(name = "orders")
public class Member {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;
}
```

- `Member member = em.find(Member.class, 1L);` 에서 Team은 프록시로 조회 (쓸모없는 쿼리 안날려도 됨)
- `Team team = member.getTeam();` 에서 team 객체 초기화(DB 조회)
- `@OneToMany`, `@ManyToMany`는 기본이 지연 로딩

<br>
<br>


## FetchType.EAGER

- Member와 Team을 자주 함께 사용할 때에 유용하다. (하지만 실무에서는 지연로딩만 쓰자!!)
- 조인을 사용해서 SQL 한번에 함께 조회한다.
- `@ManyToOne` 과 `@OneToOne` 은 디폴트가 `EAGER` 이다. -> `LAZY`로 바꿔주자.
- 특히 JPQL을 사용할 때에 n+1 문제를 일으킨다 (이중 쿼리가 나간다 -> 성능에 안좋다)

<br>
<br>


## 정리

- 가급적 지연로딩만 쓰자 (특히 실무에서)
- 즉시로딩을 쓰면 예상치 못한 SQL이 발생한다.
- 즉시로딩이 필요한 상황이면, JPQL 패치 조인 기능을 쓰자.


<br>
<br>


# 영속성 전이(Cascade)와 고아 객체

##  Cascade (영속성 전이)
- 부모 엔티티가 영속화될 때 자식 엔티티도 같이 영속화되고, 부모 엔티티가 삭제될 때 자식 엔티티도 삭제되는 등 특정 엔티티를 영속 상태로 만들 때 연관된 엔티티도 함께 영속 상태로 전이되는 것을 의미한다.

- 연관된 엔티티도 함께 영속화하는 편리함을 제공할 뿐, 연관관계 매핑과는 아무 관련이 없다.

- 종류:
    - ALL: 모두 적용
    - PERSIST: 영속
    - REMOVE: 삭제

- 예시 코드 

```java
@OneToMany(mappedBy="parent", cascade=CascadeType.PERSIST)

...

Parent parent = new Parent();
parent.addChild(child1);
parent.addChild(child2);

em.persist(parent);
em.persist(child1);     // Cascade 속성 있다면 생략 가능. 
em.persist(child2);     // Cascade 속성 있다면 생략 가능. 
```

<br>
<br>


## 고아 객체

- 제거: 부모 엔티티와 연관관계가 끊어진 자식 엔티티를 자동으로 삭제.
- `orphanRemoval = true`
- `@OneToOne` 과 `@OneToMany` 만 가능

```java
@OneToMany(mappedBy="parent", cascade=CascadeType.PERSIST, orphanRemoval = true)

...

findParent.getChildList().reomove(0);       // 이 때 동작
```

<br>
<br>


## 정리

- 두 연관 객체의 생명주기가 똑같고, **특정 엔티티가 개인소유 할 때만 사용하자!** (예: 게시글과 첨부파일)
- `CascadeType.REMOVE` 와 고아 객체 제거의 차이점 : 전자는 부모 엔티티 제거시 자식 엔티티 제거, 후자는 연관관계가 끊어질 시 그 자식 엔티티가 제거
- 참고: 개념적으로 부모를 제거하면 자식은 고아가 된다. 따라서 고아 객체 제거 기능을 활성화 하면, 부모를 제거할 때 자식도 함께 제거된다. 이것은 `CascadeType.REMOVE`처럼 동작한다.
- 두 옵션을 모두 활성화 하면 부모 엔티티를 통해서 자식의 생명주기를 관리할 수 있다.
