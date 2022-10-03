package study.jpastudy.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Locker {

    @Id
    @GeneratedValue()
    @Column(name = "locker_id")
    private Long id;

    // 양방향일 경우 추가
    @OneToOne(mappedBy = "locker")
    private Member member;
}
