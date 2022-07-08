# 문제 상황

User, UserRepository, UserService 클래스를 각각 만들고   
UserRepository 클래스의 **findByEmail()** 메서드를 테스트 하는 도중에   
다음과 같은 에러가 나타났다.

```console
org.springframework.dao.InvalidDataAccessResourceUsageException: Named parameter not bound : email;
```
email이라는 파라미터가 바인딩되지 않았다는 것인데, 우선 먼저 작성했던 코드를 보자.

```java
    public List<User> findByEmail(String email) {
        List<User> result = em.createQuery("select u from User u where u.userEmail = :email", User.class)
                .getResultList();
        return result;
    }
```

# 해결
아래와 같이 setparameter 메서드를 호출해서 파라미터를 바인딩해줬다.
```java
    public List<User> findByEmail(String email) {
        List<User> result = em.createQuery("select u from User u where u.userEmail = :email", User.class)
                .setParameter("email", email)
                .getResultList();
        return result;
    }
```
## 참고
### **EntityManager.createQuery("JPQL", 엔티티)**    

EntityManager 객체에서 createQuery() 메서드를 호출하면 쿼리가 생성된다.

TypedQuery는 반환되는 엔티티가 정해져 있을 때 사용하는 타입이며,

em.createQuery 메서드를 호출할 때 두 번째 인자로 엔티티 클래스를 넘겨준다.

TypedQuery 객체의 getResultList() 메서드를 호출하면 작성한 JPQL에 의해 데이터를 검색하며, List 타입으로 반환한다.

**getSingleResult()** : 결과가 1개가 아니면 오류 발생.   
**getResultSet()**: 결과가 0개이면 null 반환.
