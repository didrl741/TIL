# 문제 상황

게시판 토이프로젝트를 만드는 도중, 
```java
postService.join(post);
```
를 했는데도 db에 반영이 되지 않았다. 즉, insert문이 실행되지 않았다.   
test에서 join 메서드를 실행해 봤더니 db에 저장이 잘 되길래 내가 모르는 다른 문제가 있을것이라고 생각해서 코드를 이리저리 추가하고 로그기록을 보며 어떤 문제인지 찾아봤다.  

</br>

```java
postService.join(post);
userService.join(user);
```
그러던 중, 위 코드를 실행하면 post도 db에 잘 저장이 되지만

```java
userService.join(user);
postService.join(post);
```
위 코드를 실행했을 때에는 user만 db에 저장이 되는것을 관찰했다.

</br>
</br>

# 해결

UserService에는 있지만 PostService에는 없는것을 다시 한 번 찾아봤더니   

```java
@Transactional
```
이 없다는 것을 찾았다.

</br>

**@Transactional**이 없어도 영속성 컨텍스트에는 반영이 되지만 db에는 반영이 되지 않는다는것을 다시한번 상기할 수 있었다.   
또, **service** 클래스에서 db를 수정하는 코드에는 반드시 **@Transactional**을 붙여야 한다는 것을 복습할 수 있었다.   

위의
```java
postService.join(post);
userService.join(user);
```
코드에서는 **userService**의 **@Transactional**이 영속성 컨텍스트에 있는 모든 엔티티를 flush() 해줬기 때문에 post도 db에 저장이 된 것이였다.

</br>
</br>

# 참고

* 비영속(new/transient): 영속성 컨텍스트와 전혀 관계가 없는 상태
* 영속(managed): 영속성 컨텍스트에 저장된 상태
* 준영속(detached): 영속성 컨텍스트에 저장되었다가 분리된 상태
* 삭제(removed): 삭제된 상태

</br>

```java
EntityManager entityManager = new EntityManager();

TestClass testClass = new TestClass();
// 비영속

entityManager.persist(testClass);
// 영속

entityManager.detach(testClass);
entityManager.clear();
entityManager.close();
// 준영속

entityManager.remove(testClass);
//삭제
```

> 참고 : [https://chanho0912.tistory.com/25](https://chanho0912.tistory.com/25)
