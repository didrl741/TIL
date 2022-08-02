스프링으로 간단히 비동기 화면 렌더링을 해보자.
좋아요 버튼을 누르면 현재 페이지에서 바로 이미지를 바꾸고 자바 함수를 실행시켜보자.

# AJAX

- 제이쿼리를 쓰지 않고 vanilla JS로 비동기를 구현해보자.

# Get 
- URI에 대한 정보를 서버에 Request (HTTP 헤더만)
- 서버는 그에 맞는 Response를 제공. (body는 있을수도, 없을수도)
- 주로 조회시 사용

# Post
- URI와 (HTTP 헤더)과 data (HTTP 바디)를 서버에 Request
- data는 노출되지 않기때문에 비교적 안전.
- 서버는 Request에 따라 DB를 조작.
- 그 후, 그에 맞는 Response를 제공. (body는 있을수도, 없을수도)
    - body가 있다면 비동기, @RestController
    - body가 없다면 그냥 @Controller 로 뷰 반환. 그렇다면 model.addAttribute로 추가한것은? (서버사이드 렌더링?)

# @Controller
- 주로 뷰(html)를 반환하기 위해 사용

# @RestController
- @Controller + @ResponseBody
- 주 용도: Json 형태로 객체 데이터를 반환.
- 객체를 ResponseEntity로 감싸서 반환.

- 분리하여 사용한 코드

```java
@Controller
public class UserController {

    @GetMapping(value = "/users")
    public @ResponseBody ResponseEntity<User> findUser(@RequestParam("userName") String userName){
        return ResponseEntity.ok(userService.findUser(user));
    }
}
// 출처: https://mangkyu.tistory.com/49 [MangKyu's Diary:티스토리]
```
- User를 json으로 반환하기 위해 @ResponseBody 사용.

- 코드

```java
@RestController
public class UserController {

    @GetMapping(value = "/users")
    public User findUser(@RequestParam("userName") String userName){
        return userService.findUser(user);
    }

    @GetMapping(value = "/users")
    public ResponseEntity<User> findUserWithResponseEntity(@RequestParam("userName") String userName){
        return ResponseEntity.ok(userService.findUser(user));
    }
}
// 출처: https://mangkyu.tistory.com/49 [MangKyu's Diary:티스토리]
```
- findUser 함수의 경우, 객체를 그대로 반환하기 때문에 클라이언트가 예상하는 HttpStatus를 설정해줄 수 없다.

<br/>

# 결과 캡쳐
