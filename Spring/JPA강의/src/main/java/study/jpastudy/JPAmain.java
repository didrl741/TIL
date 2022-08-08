package study.jpastudy;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAmain {

    public static void main(String[] args) {

        Integer a = new Integer(10);
        Integer b = a;          // ref을 넘김

        a = 20;

        System.out.println("a = " + a);
        System.out.println("b = " + b);
    }
}
