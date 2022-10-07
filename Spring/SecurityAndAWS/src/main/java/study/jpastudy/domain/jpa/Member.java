package study.jpastudy.domain.jpa;

import lombok.Getter;
import lombok.Setter;
import study.jpastudy.domain.BaseTimeEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue()
    @Column(name = "member_id")
    private Long id;

    private String name;

    private int age;

    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "locker_id")
    private Locker locker;



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


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    // 연관관계 편의 메서드
    public void setTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }

    @Override
    public String toString() {

        if (team == null) {
            return "Member{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
        else {
            return "Member{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    ", team=" + team.getName() +
                    '}';
        }

    }
}
