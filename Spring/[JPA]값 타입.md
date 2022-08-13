참고 : 이 글은 김영한님의 JPA 강의를 들으며 기록한 내용입니다.   

# JPA의 데이터 타입 분류

## 엔티티 타입
- `@Entity`로 정의하는 객체
- 데이터가 변해도 **식별자로 지속해서 추적 가능**

## 값 타입
- 분류:
    - 기본값 타입 (int, double, Integer, Long, String ...)
    - 임베디드 타입
    - 컬렉션 값 타입

- int, Integer, String처럼 단순히 값으로 사용하는 자바 기본 타입이나 객체
- 식별자가 없고 값만 있어서 변경시 추적 불가
- 예) 숫자 100을 200으로 변경하면 완전히 다른 값으로 대체

<br>
<br>


# 기본값 타입

- 예): String name, int age

- 생명주기를 엔티티에 의존한다.
- 예) 회원을 삭제하면 이름, 나이 필드도 함께 삭제
- 값 타입은 절대 공유하면 X
- 예) 회원 이름 변경시 다른 회원의 이름도 함께 변경되면 안됨
- `Integer`같은 래퍼 클래스나 `String` 같은 특수한 클래스는 공유가능한 객체이지만 변경 불가능.

<br>
<br>


# 임베디드 타입

- 새로운 값 타입을 직접 정의할 수 있다.

- 주로 기본 값 타입을 모아서 만들어서 복합 값 타입이라고도 한다 (c의 struct와 비슷?)
- 기본 생성자 필수
- 사용 전과 후에 매핑하는 테이블은 같다.
- 장점:
    - 관련된 필드들과 메서드들을 한 곳에 묶기 때문에, 객체지향적 설계가 가능하다.
    - 재사용성

    - 임베디드 타입을 포함한 모든 값 타입은, 값 타입을 소유한 엔티티에 생명주기를 의존.
    - 객체와 테이블을 아주 세밀하게 매핑하는것이 가능.
- 한 엔티티에서 같은 임베디드 타입을 사용하려면? -> `@AttributeOverrides`, `@AttributeOverride`를 사용해서 컬러 명 속성을 재정의하자. (잘 안쓴다)


## 코드

```java
@Entity
public class Member{

    @Embedded
    private Address address;
}

...


@Embeddable
// @setter 만들면 안됨!
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    private  String city;
    private  String street;
    private  String zipcode;

    // equals() 재정의 해야함!
}
```

<br>
<br>


# 값 타입과 불변 객체

> 값 타입은 복잡한 객체 세상을 조금이라도 단순화하려고 만든 개념이다. 따라서 값 타입은 단순하고 안전하게 다룰 수 있어야 한다.

## 값 타입, 복사vs공유참조 

- 항상 값을 복사해서 사용하면 공유 참조로 인해 발생하는 부작용을 피할 수 있다.
- 문제는 임베디드 타입처럼 직접 정의한 값 타입은 자바의 기본타입이 아니라 객체 타입이다.
- 객체 타입은 참조 값을 직접 대입하는 것을 막을 방법이 없고, 객체의 공유 참조는 피할 수 없다.

- 임베디드 타입 같은 값 타입을 여러 엔티티에서 공유하면 위험하다!
    - 부작용: 
    ```java
    Address a = new Address(“Old”);
    Address b = a; //객체 타입은 참조를 전달
    b. setCity(“New”)       // a도 바뀌어버린다!
    ```

<br>
<br>


## 불변 객체

- 객체 타입을 수정할 수 없게 만들면 부작용을 원천 차단

- 값 타입은 불변 객체(immutable object)로 설계해야함
- 불변 객체: 생성 시점 이후 절대 값을 변경할 수 없는 객체
- 생성자로만 값을 설정하고 수정자(Setter)를 만들지 않으면 된다!
- 참고: Integer, String은 자바가 제공하는 대표적인 불변 객체

<br>
<br>


## 정리

- 따라서, **임베디드 타입은 `setter`를 만들지 말자** (또는 `private`으로 만들자)

- 불변이라는 작은 제약으로 부작용이라는 큰 재앙을 막을 수 있다.

<br>
<br>


# 값 타입의 비교

## 동일성 비교 (identity)
- 인스턴스의 참조 값을 비교(레퍼런스), == 사용
- Primitive 값 타입은 값 비교

<br>
<br>


## 동등성 비교(equivalence)
- 인스턴스의 값을 비교, `equals()` 사용
- 값 타입은 `a.equals(b)`를 사용해서 동등성 비교를 해야 함
- **값 타입의 `equals()` 메소드를 적절하게 재정의(주로 모든 필드사용)**

<br>
<br>


## 코드

```java
int a = 1;
int b = 1;

Address address1 = new Address("h", "h", "h");
Address address2 = new Address("h", "h", "h");
Address address3 = new Address("k", "k", "k");

System.out.println(a == b);          // true (내부에 레퍼런스가 아니라 값이 들어있기 때문)

System.out.println(address1 ==address2);        // false (레퍼런스가 다르기 때문)
System.out.println(address1.equals(address2));  // false. 재정의 해야함

String str1 = "hi";
String str2 = "hi";
String newStr = new String("hi");

System.out.println(str1 ==str2);            // true (주소 일치)
System.out.println(str1 ==newStr);            // false (주소 불일치)
System.out.println(str1.equals((str2)));    // true ( 값 일치)
System.out.println(str1.equals((newStr)));    // true ( 값 일치)

String str3 = "hi";
String str4 = "hello";

System.out.println(str3 ==str4);            // false (주소 다름)
System.out.println(str3.equals((str4)));    // false (값 다름)


...

// 재정의 : Alt + Ins 로 equals()와 hashCode()를 재정의할 수 있다 (프록시 문제가 안생기도록 get으로 하자!)
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Address address = (Address) o;
    return Objects.equals(getCity(), address.getCity()) && Objects.equals(getStreet(), address.getStreet()) && Objects.equals(getZipcode(), address.getZipcode());
}

@Override
public int hashCode() {
    return Objects.hash(getCity(), getStreet(), getZipcode());
}

// 이제, 아래를 다시 돌려보면

System.out.println(address1.equals(address2));  // true
```

- 참고
    - `String`을 `new` 방식으로 만들면 완전 새롭게 생성
    - `String`을 그냥 리터럴로 만들면, 같은 문자열 `String` 객체가 있을 경우 그 레퍼런스 반환

<br>
<br>


# 값 타입 컬렉션

## 코드

```java
@Entity
public class Member{

    @ElementCollection
    @CollectionTable(name = "favorite_food", joinColumns = @JoinColumn(name = "member_id"))                                  // member_id를 FK로.
    @Column(name = "food_name")
    private Set<String> faboriteFoods = new HashSet<>();


    @ElementCollection
    @CollectionTable(name = "address", joinColumns = @JoinColumn(name = "member_id"))                                    // member_id를 FK로.
    private List<Address> addressHistory = new ArrayList<>();
}
```

<br>
<br>


## 그림 및 테이블 구성

![값타입컬렉션](https://user-images.githubusercontent.com/97036481/183468578-df5bb9fb-7446-4988-a4c9-5301eccdb6b4.png)
![member](https://user-images.githubusercontent.com/97036481/183468689-f0ff6891-5227-4cf0-bf7c-85c2df6afde4.png)
![food](https://user-images.githubusercontent.com/97036481/183468703-5318653b-2c1c-43f7-8ede-74becdc8c90a.png)
![address](https://user-images.githubusercontent.com/97036481/183468713-2bc43aab-6844-42a4-82c2-7ea1efd71e60.png)


<br>
<br>


## 개념

- 일대 다 관계와 비슷하지만 다른 개념이다. (컬렉션의 요소가 엔티티가 아니다)

- DB는 컬렉션을 같은 테이블에 저장할 수 없다 -> 별도의 테이블이 필요
- 값 타입 컬렉션은 영속성 전이(`Cascade`) + 고아 객체 제거 기능을 필수로 가진다고 볼 수 있다.
    - 위의 코드에서, `Member`만 `persist` 해도 값 타입 컬렉션들은 자동으로 `persist `된다.

    - = `Member`의 생명주기에 의존적이다!
- `em.find(member)` 하면 `member`만 `SELECT` 된다 -> 지연로딩이 디폴트!


<br>
<br>


## 수정

1. `private Set<String> faboriteFoods = new HashSet<>();` 의 경우, `String`은 수정할 수 없고 통째로 갈아 끼워야되기 때문에 아래와 같이 수정해야한다.

```java
member.getFaboriteFoods().remove("족발");
member.getFaboriteFoods().add("한식");
```

<br>
<br>


2. `private List<Address> addressHistory = new ArrayList<>();` 의 경우, Address는 수정할 수 없고 통째로 갈아 끼워야되기 때문에 아래와 같이 수정해야**할까...?**

```java
// equals()를 제대로 재정의해놓지 않았다면 밑의 remove는 동작하지 않는다!!!
member.getAddressHistory().remove(new Address("hi", "hello", "bye"));
member.getAddressHistory().add(new Address("newCity", "hello", "bye"));
```

<br>
<br>


## 값 타입 컬렉션의 제약사항

- 위 코드를 실행해보면, `String` 때와 달리 쿼리가 예상과 다르게 나간다.

- 값 타입 컬렉션에 변경 사항이 발생하면, 주인 엔티티와 연관된 모든 데이터를 삭제하고, 값 타입 컬렉션에 있는 현재 값을 모두 다시 저장한다.
- 따라서, 다음과 같이 엔티티로 승급시키자!

```java
@Entity
public class Member {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "member_id")
    private List<AddressEntity> addressHistory = new ArrayList<AddressEntity>();
}

...


@Entity
@Table(name = "address")
@NoArgsConstructor
public class AddressEntity {

    @Id
    @GeneratedValue
    @Column(name = "address_id")
    private Long id;

    private Address address;

    public AddressEntity(String city, String street, String zipcode) {
        this.address = new Address(city, street, zipcode);
    }
}
```

- 위와 같이 관계를 짜면, `member.getAddressHistory().add(new AddressEntity("hi", "hello", "bye"));` 했을 때 `AddressEntity` 엔티티가 db에 `INSERT` 된다. 즉, 연관관계의 주인이 '일' 쪽이다.

- 이제 `AddressEntity` 엔티티는 db에서 본인의 pk를 갖기 때문에 추적과 관리가 용이하다.

![마지막](https://user-images.githubusercontent.com/97036481/183468771-9ecb4776-b238-48af-a713-2e1014b898b3.png)


<br>
<br>


## 정리

- 실무에서는 값 타입 컬렉션보다는 일대다 관계를 고려하자!
- 아주 단순한 경우 (ex. 선택박스에서 치킨or 피자 선택)가 아니라면 엔티티로 승격시켜서 사용하자!
- 식별자가 필요하고, 지속해서 값을 추적, 변경해야 한다면 그것은 값 타입이 아닌 엔티티로 써야한다.

- 엔티티 타입의 특징
    - 식별자O
    - 생명 주기 관리
    - 공유
- 값 타입의 특징
    - 식별자X
    - 생명 주기를 엔티티에 의존
    - 공유하지 않는 것이 안전(복사해서 사용)
    - 혹시라도 공유될 때를 대비해서 불변 객체로 만드는 것이 안전
