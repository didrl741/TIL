package study.jpastudy.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HomeControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void 메인페이지_로딩() throws Exception {
        //given
        //when
        String body = this.restTemplate.getForObject("/", String.class);

        //then
        System.out.println("body ===== " + body);
    }

    @Test
    public void stream테스트() throws Exception {

        // List<Integer> => List<MyClass> 로 바꾸는 stream 문법
        List<Integer> list1 = new ArrayList<>();
        list1.add(1); list1.add(2); list1.add(3);

        // 두 방법 똑같다.
        List<MyClass> list2 = list1.stream().map(integer -> new MyClass(integer)).collect(Collectors.toList());
        List<MyClass> list3 = list1.stream().map(MyClass::new).collect(Collectors.toList());

        for (MyClass myClass:list2) {
            System.out.println(myClass);
        }
    }

    public class MyClass {
        private String hi;

        public MyClass(Integer integer) {
            this.hi = "hello";
        }

        public String toString() {
            return hi;
        }
    }

}