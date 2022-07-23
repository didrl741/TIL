스프링으로 간단한 게시판을 만들고있다.   
이번에는 작성한 글을 취소하는 기능을 만들어보자.

# 코드

## postList.html

> 배치 부분

```html
  <td>
      <a th:if="${post.getUser().getUserName() == session.loginedUserName}" href="#"
          th:href="'javascript:cancel('+${post.id}+')'"
          class="btn btn-danger">삭제하기</a>
  </td>
```
- 게시글이 현재 **세션의 아이디와 일치하는 경우에만** 삭제하기 버튼이 보인다.
- 삭제하기 버튼을 누를 시, JS의 cancel이라는 함수가 실행된다.

<br/>

> JS 부분

```html
<script>
    function cancel(id) {
        var form = document.createElement("form");
        form.setAttribute("method", "post");
        form.setAttribute("action", "/items/" + id + "/cancel");
        document.body.appendChild(form);
        form.submit();
    }
</script>
```
- form 객체를 만들어서, http method와 주소를 지정해준 후 post방식으로 전송한다.
- 그러면 PostController에서 저 주소가 post방식으로 들어올 때 처리하는 코드가 필요하겠지? 작성해보자.

<br/>

## PostController

```java
   @PostMapping("/items/{postId}/cancel")
    public String cancelPost(@PathVariable("postId") Long postId) {
        postService.cancelPost(postId);
        return "redirect:/items";
    }
```
- PostService에 취소기능을 위임한다.

<br/>


## PostService

```java
    @Transactional
    public void cancelPost(Long postId) {
        postRepository.cancelOne(postId);
    }
```
- PostRepository에 취소기능을 위임한다.
- 엔티티를 삭제하는 함수이므로 **@Transactional** 필수!!

<br/>

## PostRepository

```java
  public void cancelOne(Long postId) {
      Post post = findOne(postId);
      em.remove(post);
  }
```
- EntityManager에서 직접 db의 엔티티를 삭제한다.

<br/>

# 결과 캡쳐
![스프링부트](https://user-images.githubusercontent.com/97036481/180595197-593ec1ac-8852-4cf8-b238-9fa2ec10c9ab.png)
- 해당 글 작성자만 그 글을 삭제할 수 있다.
