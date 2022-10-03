컨트롤러에서 html파일에 data를 전송하는것은 Model 객체를 이용해서 하면 됐다.   
이번에는 Model에 특정 객체를 전달했을 때만 js에서 alert가 실행되도록 하는 방법을 알아보자.


### 컨트롤러 코드
```java
else {
        model.addAttribute("msg", msg);
        return "users/login";
}
```
위 코드와 같이 특정 상황에서만 msg라는 객체를 전달했다 (msg는 String 객체이다)

### users/login.html 코드
```html
<head>
    <div th:if="${not #strings.isEmpty(msg)}">
        <script>
            top.alert("[[${msg}]]");
        </script>
    </div>
</head>
```
위 코드와 같이 msg 필드가 있을 경우에만 alert를 실행한다.

## 참고

* 타임리프 문법이 많이 미숙하므로 차차 공부해야겠다.
* alert를 처리하는 여러가지 방법이 있다. 
