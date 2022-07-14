스프링으로 간단히 로그인과 로그아웃을 구현해보자.   
spring security 등 다른 방법도 존재하지만 이번에는 HttpSession을 이용하여 간단하게 구현해봤다.   

# 필요한 배경지식

## 1. HttpSession 및 HttpServletRequest

요청 매핑 애노테이션을 적용한 메서드에 파라미터로 HttpSession타입 객체를 추가하는 방법

```java
// 1번
@PostMapping
public String submit(HttpSession session){
    ... 생략
}

// 2번
@PostMapping
public String submit(HttpServletRequest req){
	HttpSession session = req.getSession();
    ... 생략
}
```

첫 번째 예제에서 매핑 애노테이션을 적용한 메서드에 HttpSession 파라미터를 추가하면 메서드를 호출할 때 MVC가 HttpSession 객체를 생성하고 전달합니다. 이미 존재한다면 전달만 합니다.

두 번째 예제에서는 getSession() 메서드를 이용해서 HttpSession을 생성하거나 호출합니다. 첫 번째 예제와 달리 HttpSession을 필요할 때만 생성하는 것이 가능합니다.

[ 출처 : https://jaehoney.tistory.com/67 ]( https://jaehoney.tistory.com/67 )   

<br/>

## 2.  타임리프 문법 (문자열 합치기)

> th:text="| 합칠 문자열들 나열 |"

```html
<p th:text="|${session.loginedUserName} 님, 반가워요|"></p>
```

<br/>

# 코드

## 유저컨트롤러 코드
> 로그인 구현
```java
    @PostMapping("/users/login")
    public String login(@Valid UserForm userForm, BindingResult result, HttpSession session, HttpServletRequest request, Model model) {

        if (userForm.getName().length() == 0 || userForm.getPassword().length() == 0) {
            return "users/login";
        }

        Map<String, Object> rs = userService.checkLoginAvailable(userForm);

        String resultCode = (String) rs.get("resultCode");
        String msg = (String) rs.get("msg");

        if (resultCode.startsWith("S")) {
            session.setAttribute("loginedUserName", userForm.getName());
            return "redirect:/";
        } else {
            model.addAttribute("msg", msg);
            return "users/login";
        }

    }
```
로그인에 성공하면 HttpSession에 인증정보객체 loginedUserName 를 추가합니다.   
로그인에 실패하면 Model 객체에 실패 메시지를 추가하여 alert를 호출합니다.

<br/>

> 로그아웃 구현
```java
@GetMapping("/users/logout")
    public String logout(HttpSession session) {

        session.invalidate();
        return "redirect:/";
    }
```
로그아웃시, 세션을 제거합니다.

<br/>

## 유저서비스 코드
```html
    public Map<String, Object> checkLoginAvailable(UserForm userForm) {
        Map<String, Object> rs = new HashMap<String, Object>();

        List<User> users = userRepository.findByName(userForm.getName());

        if (users.isEmpty()) {
            rs.put("resultCode", "F-1");
            rs.put("msg", "해당 회원이 존재하지 않습니다.");
            log.info("해당 회원이 존재하지 않습니다");
        } else if (users.get(0).getUserPassword().equals(userForm.getPassword()) == false ) {
            rs.put("resultCode", "F-2");
            rs.put("msg", "비밀번호가 일치하지 않습니다.");
            log.info("비밀번호가 일치하지 않습니다.");
        } else {
            rs.put("resultCode", "S-1");
            rs.put("msg", "로그인에 성공했습니다.");
            log.info("로그인 성공");
        }

        return rs;
    }
```
로그인 정보를 담고있는 UserForm 객체를 전달받아서, 현재 로그인이 가능한지 판단해주는 함수입니다.   
결과코드 및 메시지를 **HashMap**에 저장하여 유저 컨트롤러에 전달합니다.   

<br/>

## home.html 코드
```html
  <div th:if="${session.loginedUserName==null}"href="users/login">로그인하러가기</div>

  <div th:if="${session.loginedUserName!=null}">
    <a href="users/logout">로그아웃 하기</a>
    <p th:text="|${session.loginedUserName} 님, 반가워요|"></p>
  </div>
```
현재 로그인되어있는 경우 로그아웃 버튼과 회원명을 출력하여 보여줍니다.   
현재 로그아웃 상황일 경우에는 로그인 버튼을 보여줍니다.

<br/>

## login.html 코드
```html
    <div th:if="${not #strings.isEmpty(msg)}">
        <script>
            top.alert("[[${msg}]]");
        </script>
    </div>
```
로그인 실패시 실패 이유를 alert를 이용해여 팝업창으로 출력합니다.

<br/>

# 결과 캡쳐
* 홈 화면   
![20220714_163910](https://user-images.githubusercontent.com/97036481/178928753-fcec58f0-bad7-4a77-93ae-23c86d096ee4.png)
![20220714_163943](https://user-images.githubusercontent.com/97036481/178928767-c341f0cf-3afa-484b-8eca-2601571d33a9.png)

* 팝업 창   
![20220714_164007](https://user-images.githubusercontent.com/97036481/178928773-d220ba66-f911-4fed-a533-c56638135d8a.png)
