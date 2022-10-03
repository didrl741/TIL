스프링으로 간단히 비동기 화면 렌더링을 해보자.
좋아요 버튼을 누르면 현재 페이지에서 바로 이미지를 바꾸고 자바 함수를 실행시켜보자.

# AJAX

- 제이쿼리를 쓰지 않고 vanilla JS로 비동기를 구현해보자.
- POST 방식을 사용했다. (사실 Request 바디 데이터가 쓸모가 없으므로 GET으로 해도 된다.)
    - 하지만 DB를 조작하는 기능이 포함되므로 POST를 썼다.


## html

- 뷰에서 특정 이벤트 발생시(클릭) 서버에 Request 전달
- 서버로부터 넘어온 Response를 가지고 다시 뷰 수정

```html
        <div th:if="${checkLiked == true }">
            <img th:id="likeImg" src="/img/like_full.png" alt="" width="50px" height="50px"
                 th:postId="${post.id}"
                 onclick="determineLike(this.getAttribute('postId'))"
            >
        </div>

        <div th:if="${checkLiked != true }">
            <img th:id="likeImg" src="/img/like_empty.png" alt="" width="50px" height="50px"
                 th:postId="${post.id}"
                 onclick="determineLike(this.getAttribute('postId'))"
            >
        </div>


          ...

<script type="text/javascript">

    function determineLike(id) {

        /* 데이터를 가져옴 */
        var input = document.getElementById("likeCount").value;
        /* 입력된 데이터 Json 형식으로 변경 */
        var reqJson = new Object();
        reqJson.name = input;
        /* 통신에 사용 될 XMLHttpRequest 객체 정의 */
        httpRequest = new XMLHttpRequest();
        /* httpRequest의 readyState가 변화했을때 함수 실행 */
        httpRequest.onreadystatechange = () => {
            /* readyState가 Done이고 응답 값이 200일 때, 받아온 response로 name과 age를 그려줌 */
            if (httpRequest.readyState === XMLHttpRequest.DONE) {
                if (httpRequest.status === 200) {
                    var result = httpRequest.response;

                    if (result.check === "liked") {
                        document.getElementById("likeImg").src = "/img/like_full.png";
                    }
                    else {
                        document.getElementById("likeImg").src = "/img/like_empty.png";
                    }


                    document.getElementById("likeCount").innerText = result.count;
                } else {
                    alert('request에 뭔가 문제가 있어요.');
                }
            }
        };
        /* Post 방식으로 요청 */
        httpRequest.open('POST', "/items/" + id + "/likeAndHateByAjax", true);
        /* Response Type을 Json으로 사전 정의 */
        httpRequest.responseType = "json";
        /* 요청 Header에 컨텐츠 타입은 Json으로 사전 정의 */
        httpRequest.setRequestHeader('Content-Type', 'application/json');
        /* 정의된 서버에 Json 형식의 요청 Data를 포함하여 요청을 전송 */
        httpRequest.send(JSON.stringify(reqJson));

    }

</script>
```

## Controller

```java
@RestController
public class AjaxController {

    // 좋아요와 취소 둘다 구현
    @PostMapping("/items/{postId}/likeAndHateByAjax")
    public Map<String,Object> likeOrHate(@PathVariable("postId") Long postId, HttpSession session) {

        String logInedUserName = (String)session.getAttribute("loginedUserName");
        Post post = postService.findOne(postId);

        // 리팩토링 필요..
        Long logInedUserId = userService.findByName(logInedUserName).get(0).getId();

        boolean checkLiked = userService.checkLiked(logInedUserId, postId);

        Map<String,Object> returnMap = new HashMap<>();

        // 이미 좋아요 했었으면
        if (checkLiked == true) {
            // 취소기능
            UserLikePost userLikePost = userService.findLiked(logInedUserId, postId);

            userLikePostService.deleteLIke(userLikePost.getId());

            returnMap.put("check", "canceled");

        // 좋아요 안한 상태이면
        } else {
            UserLikePost userLikePost = new UserLikePost();
            userLikePost.setPost( post );

            userLikePost.setUser(userService.findByName(logInedUserName).get(0));

            userLikePostService.join(userLikePost);
            returnMap.put("check", "liked");
        }


        returnMap.put("count", post.getUserLikePosts().size());

        return returnMap;
    }
}

```

# 참고사항

## Get 
- URI에 대한 정보를 서버에 Request (HTTP 헤더만)
- 서버는 그에 맞는 Response를 제공. (body는 있을수도, 없을수도)
- 주로 조회시 사용

## Post
- URI와 (HTTP 헤더)과 data (HTTP 바디)를 서버에 Request
- data는 노출되지 않기때문에 비교적 안전.
- 서버는 Request에 따라 DB를 조작.
- 그 후, 그에 맞는 Response를 제공. (body는 있을수도, 없을수도)
    - body가 있다면 비동기에 적절하다. `@RestController`
    - body가 없다면 그냥 `@Controller` 로 뷰 반환. 
        - 그렇다면 `model.addAttribute`로 추가한것은? -> **서버사이드 렌더링**

## @Controller
- 주로 뷰(html)를 반환하기 위해 사용

## @RestController
- `@Controller` + `@ResponseBody`
- 주 용도: `Json` 형태로 객체 데이터를 반환.
- 객체를 `ResponseEntity`로 감싸서 반환.

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
- `User`를 json으로 반환하기 위해 `@ResponseBody` 사용.

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
- `findUser` 함수의 경우, 객체를 그대로 반환하기 때문에 클라이언트가 예상하는 HttpStatus를 설정해줄 수 없다.

