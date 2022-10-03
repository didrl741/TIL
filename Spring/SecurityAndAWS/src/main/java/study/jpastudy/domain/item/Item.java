package study.jpastudy.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name="dtype")
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private  Long id;

    private String name;
    private int price;
    private int stockQuantity;

    //== 비즈니스 로직 ==//
    public void addStock(int quantitiy) {
        this.stockQuantity += quantitiy;
    }

//    public void removeStock(int quantitiy) {
//        int restStock = this.stockQuantity - quantitiy;
//        if (restStock < 0) {
//            throw new NotEnoughStockException("new more stock");
//        }
//        this.stockQuantity = restStock;
//    }
}
