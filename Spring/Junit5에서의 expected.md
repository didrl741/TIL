 junit5를 이용해서 테스트메서드를 작성하던 중, 익숙한 expected가 안되어 알아보니 다음과같이 써야한다고 한다.   
코드는 User객체의 중복이메일을 체크하는 테스트이다.
## junit4 에서의 코드
```java
    @Test(expected = IllegalStateException.class )
    public void 중복이메일() throws Exception {
        //given
        User user1 = new User();
        user1.setUserEmail("didrl741@naver.com");
        userService.join(user1);

        //when
        User user2 = new User();
        user2.setUserEmail("didrl741@naver.com");
        userService.join(user2);
    }
```

## junit5 에서의 코드
```java
    @Test
    public void 중복이메일() throws Exception {
        //given
        User user1 = new User();
        user1.setUserEmail("didrl741@naver.com");
        userService.join(user1);

        //when
        User user2 = new User();
        user2.setUserEmail("didrl741@naver.com");

        //then
        assertThrows(IllegalStateException.class, () -> {
            userService.join(user2);
        });
    }
```


## 그 외 주된 차이점

### Junit4

```java
@RunWith(SpringRunner.class)
```

### Junit5

```java
@ExtendWith(SpringExtension.class)  // but, 써줄 필요 없다.
```

기능은 동일하지만 junit5는   
@SpringBootTest에 @RunWith(SpringRunner.class)가 **포함**되어있기 때문에(메타 에노태이션),   
@SpringBootTest 만 써주면 된다.

