package study.jpastudy.domain.posts;

import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostsRepositoryTest {

    @Autowired
    PostsRepository postsRepository;

    @AfterEach
    public void cleanup() {
        postsRepository.deleteAll();
    }

    @Test
    public void 게시글저장_불러오기() throws Exception {
        //given
        String title = "테스트 게시글 제목";
        String content = "테스트 본문";

        postsRepository.save(Posts.builder()
                .title(title)
                .content(content)
                .author("didrl741@naver.com")
                .build()
        );
        //when
        List<Posts> postsList = postsRepository.findAll();

        //then
        Posts posts = postsList.get(0);

        assertEquals(posts.getTitle(), title);

        assertEquals(posts.getContent(), content);
    }

    @Test
    public void BaseTimeEntity_등록() throws Exception {
        //given
        LocalDateTime now = LocalDateTime.of(2022, 10, 7, 0, 0, 1);
        Posts posts = new Posts("title1", "content1", "author1");
        postsRepository.save(posts);

        //when
        List<Posts> postsList = postsRepository.findAll();
        Posts findPosts = postsList.get(0);

        //then

        System.out.println(">>>>>>>>. created date = " + findPosts.getCreatedDate());
        System.out.println(">>>>>>>>. modified date = " + findPosts.getModifiedDate());


    }
}