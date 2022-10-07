package study.jpastudy.domain.jpa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class MemberDTO {

    private String name;
    private int age;
}
