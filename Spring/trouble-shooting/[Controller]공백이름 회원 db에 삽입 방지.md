# 문제 상황

회원가입 기능을 만들었다. 그런데 회원 이름 또는 비밀번호가 사용자로부터 공백으로 입력받으면   
아래와 같이 경고메시지가 뜨고 db에는 저장이 안돼야 하는데 저장이 되고있다.
그리고 회원가입에 성공했을 때 다시 이전 페이지로 돌아가는 기능도 추가하고싶었다.


# 해결
### 이전의 코드
```java
    public String create(@Valid UserForm userForm, BindingResult result) {

        User user = new User();

        user.setUserName(userForm.getName());
        user.setUserPassword(userForm.getPassword());
        user.setUserEmail(userForm.getEmail());

        userService.join(user);
        return "users/createUserForm";
    }
```

이전의 코드를 보면 userForm이 뭐가오든 join을 실행하여 db에 저장했다.

### 이후의 코드
```java
    public String create(@Valid UserForm userForm, BindingResult result) {

        if (userForm.getName().length() == 0 || userForm.getPassword().length() == 0) {
            return "users/createUserForm";
        }

        User user = new User();

        user.setUserName(userForm.getName());
        user.setUserPassword(userForm.getPassword());
        user.setUserEmail(userForm.getEmail());

        userService.join(user);
        return "redirect:/";
    }
```
따라서 userForm에 부적절한 데이터가 오면 검증하는 코드를 삽입해서 해결했다.   
또, userForm이 적절할 시 db에 저장함과 동시에 이전의 페이지로 돌아가게 했다.


## 참고

* Form을 검증하는 과정이 복잡해지면 따로 함수로 뺄 생각이다.   
