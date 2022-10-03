package study.jpastudy.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
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
