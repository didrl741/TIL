package study.jpastudy.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue()
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();
}
