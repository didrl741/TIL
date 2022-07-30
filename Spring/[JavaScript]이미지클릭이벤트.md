스프링으로 간단한 게시판을 만들고있다.   
이번에는 좋아요 버튼을 누르면 이벤트를 발생시키게 해보자.

# img 클릭시 기본 이벤트 실행

## html 코드
```html
<img  src="/img/like_empty.png" onclick="myFunc()" >

...

<script >
    function myFunc() {
        alert("안녕하세요");
    }
</script>
```



</br>
</br>

# 타임리프 사용해서 컨트롤러에서 넘긴 객체 받기 1

## PostController 코드

`model.addAttribute("message", "전달된 메시지입니다");`

## html 코드
```html
<img  src="/img/like_empty.png" onclick="myFunc()" >

...

<script th:inline="javascript">
    /*<![CDATA[*/
    function myFunc() {
        const message = /*[[${message}]]*/;

        alert(message)
    }
    /*]]>*/
</script>
```
- 컨트롤러에서 model로 넘긴 객체를 JS에서 바로 받을 수 있다.
- 타임 리프 변수 `${}`를 `<script>` 태그 내에서 사용하기 위해서는 `th:inline="javascript"`를 명시해주어야 하고, 스크립트 내에` /*<![CDATA[*/ /*]]>*/`를 명시해주어야 한다. 또한 타임 리프 변수도` /*[[]]*/`로 감싸주어야 한다.


</br>
</br>

# 타임리프 사용해서 컨트롤러에서 넘긴 객체 받기 2

## PostController 코드

`model.addAttribute("post", postService.findOne(postId));`


## html 코드
```html
   <img src="/img/like_empty.png"
         th:myTitle="${post.title}"
         onclick="myFunc2(this.getAttribute('myTitle'))"
    >

...

<script>

    function myFunc2(message) {

        alert(message)
    }

</script>
```
- html 부분에서 model에 실려온 post객체를 파싱하여 JS에 넘겨주는 방식이다.


</br>
</br>

# 안되는 경우

## PostController 코드

`model.addAttribute("message", "전달된 메시지입니다");`


## html 코드
```html
   <img src="/img/like_empty.png" 
         th:onclick="myFunc2(${message})"
    >

...

<script>

    function myFunc2(message) {

        alert(message)
    }

</script>
```

</br>
</br>

# 이미지 클릭시 자바 함수 실행하기
- html의 객체 클릭시 JS 함수 호출
- JS에서 form생성해서 post방식으로 컨트롤러에게 form 전송
- 컨트롤러에서 자바 함수 실행

## html 코드
```html
<img src="/img/like_empty.png" alt="" width="30px" height="30px"
            onclick="unLogined()"
        >

...


<script>

    function addLike(id) {
        var form = document.createElement("form");
        form.setAttribute("method", "post");
        form.setAttribute("action", "/items/" + id + "/like");
        document.body.appendChild(form);
        form.submit();
    }

</script>
```

## 컨트롤러

```java
    @PostMapping("/items/{postId}/like")
    public String likePost(@PathVariable("postId") Long postId) {

        // 원하는 로직 실행

        return "redirect:/";
    }
```


</br>
</br>

# 정리

- `th:` 가 붙은 부분은 `model`에서 넘긴 객체를 `${}`를 이용해서 받을 수 있다.
- script부분도 마찬가지로, `th:inline="javascript"` 가 있으면 `model`에서 넘긴 객체를 사용할 수 있다! (양식이 좀 까다롭긴 하다)