참고 : 이 글은 스프링을 이용하여 간단한 게시판을 만드는 과정에서 일어난 일들을 기록한 것입니다.   

# 문제상황

- 특정 게시글을 클라이언트 화면에서 삭제하는 과정에서, 종종 다음과 같은 에러가 났다.

```java
java.sql.SQLIntegrityConstraintViolationException: Cannot delete or update a parent row: a foreign key constraint fails (`syk-board`.`user_like_post`, CONSTRAINT `FK3ep7opmtdheue5q9xidc3e36` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`))
```

- 몇번 삭제와 생성을 반복하다보니 좋아요가 달린 게시글을 삭제했을 때만 에러가 나타나는 것을 확인했다.

# 해결

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

##  Cascade (영속성 전이)

- 부모 엔티티가 영속화될 때 자식 엔티티도 같이 영속화되고, 부모 엔티티가 삭제될 때 자식 엔티티도 삭제되는 등 특정 엔티티를 영속 상태로 만들 때 연관된 엔티티도 함께 영속 상태로 전이되는 것을 의미한다.