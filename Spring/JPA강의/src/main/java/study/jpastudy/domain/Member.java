package study.jpastudy.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter
public class Member extends BaseEntity {

    @Id @GeneratedValue()
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "locker_id")
    private Locker locker;

    @Embedded
    private Address address;

    // 값 타입 컬렉션
    @ElementCollection
    @CollectionTable(name = "favorite_food", joinColumns = @JoinColumn(name = "member_id"))       // member_id를 FK로.
    @Column(name = "food_name")
    private Set<String> faboriteFoods = new HashSet<>();


    // @ElementCollection
    // @CollectionTable(name = "address", joinColumns = @JoinColumn(name = "member_id"))
    // private List<Address> addressHistory = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "member_id")
    private List<AddressEntity> addressHistory = new ArrayList<AddressEntity>();
}
