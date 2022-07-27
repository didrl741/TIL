이 글은 스프링과 스프링부트를 사용해서 간단한 게시판 만들기 토이프로젝트를 만드는 과정에서의 트러블슈팅을 기록한 글입니다.

# 문제 상황

'좋아요' 기능을 만들기 위해 'Like' 자바 클래스를 만들어 @Entity 애노테이션을 붙여서 서버를 돌려봤다.   
하지만 MySQL에서 계속 새로고침을 해보아도 이 'Like'엔티티가 나타나지 않았다.   

```java
@Entity
public class Like {
}
```

```console
You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near 'like (
       like_id bigint not null,
        primary key (like_id)
    ) en' at line 1
```

# 해결

- 클래스 명을 바꾸니 MySQL에 제대로 뜨는것을 발견했다.
- 'Like' 는 db의 SQL에서 쓰이는 문법에 사용할 수 있는 '예약어'라서 안되는 것을 알게 되었다.
- 엔티티 이름을 UserLikePost 으로 바꿔서 해결했다.
```java
@Entity
public class UserLikePost {
}
```
## 참고

### 1. Like

- SQL에서 LIKE는 문자열의 패턴을 검색하는데 주로 사용된다.
```sql
SELECT * FROM 테이블 WHERE 칼럼 LIKE 'pattern' 
```

### 2. SQL 이름 규칙

- 유일한 이름, 예약어가 아닐 것.
- 30바이트 이내로.
- 문자로 시작하고, _으로 끝나지 않게
- 문자, 숫자, _만 사용
- _ 두번 이상 쓰지 않기
- 빈칸 대신 _ 사용 예) first name -> first_name
- 축약형 피하기. 일반적으로 이해할 수 있는 단어가 아니라면.
- 출처: https://taptorestart.tistory.com/entry/MySQL-데이터베이스명-테이블명-컬럼명은-어떻게-지어야-할까 [Tap to restart:티스토리]