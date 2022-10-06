package study.jpastudy.web;

import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import study.jpastudy.domain.posts.Posts;
import study.jpastudy.domain.posts.PostsRepository;
import study.jpastudy.web.dto.PostsSaveRequestDto;
import study.jpastudy.web.dto.PostsUpdateRequestDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PostsApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostsRepository postsRepository;

    @AfterEach
    public void tearDown() throws Exception {
        postsRepository.deleteAll();
    }

    @Test
    public void Posts_등록된다() throws Exception {
        //given
        String title = "title";
        String content = "content";
        PostsSaveRequestDto requestDto = new PostsSaveRequestDto(title, content, "author");

        String url = "http://localhost:" + port + "/api/v1/posts";

        //when
        // 이 때 컨트롤러 코드가 실행되며 save 실행
        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, requestDto, Long.class);

        //then
        List<Posts> all = postsRepository.findAll();

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(all.get(0).getTitle(), title);
        assertEquals(all.get(0).getContent(), content);
    }


    @Test
    public void Posts_수정() throws Exception {
        //given
        String title = "title";
        String content = "content";
        Posts savedPosts = new Posts(title, content, "author");
        postsRepository.save(savedPosts);
        Long updateId = savedPosts.getId();

        PostsUpdateRequestDto requestDto = new PostsUpdateRequestDto("title2", "content2");

        String url = "http://localhost:" + port + "/api/v1/posts/" + updateId;

        HttpEntity<PostsUpdateRequestDto> requestEntity = new HttpEntity<>(requestDto);

        //when
        ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Long.class);

        //then
        List<Posts> all = postsRepository.findAll();

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(all.get(0).getTitle(), "title2");
        assertEquals(all.get(0).getContent(), "content2");
    }
}