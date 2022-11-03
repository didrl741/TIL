package study.jpastudy.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import study.jpastudy.service.posts.PostsService;
import study.jpastudy.web.dto.PostsResponseDto;
import study.jpastudy.web.dto.PostsSaveRequestDto;
import study.jpastudy.web.dto.PostsUpdateRequestDto;

@RequiredArgsConstructor
@RestController
public class PostsApiController {

    private final PostsService postsService;

    // 삽입
    @PostMapping("/api/v1/posts")
    public Long save(@RequestBody PostsSaveRequestDto requestDto) {
        return postsService.save(requestDto);
    }

    // 변경
    @PutMapping("/api/v1/posts/{id}")
    public Long update(@PathVariable Long id, @RequestBody PostsUpdateRequestDto requestDto) {
        return postsService.update(id, requestDto);
    }

    // 조회. @RequestBody 없다.
    @GetMapping("/api/v1/posts/{id}")
    public PostsResponseDto findById(@PathVariable Long id) {
        return postsService.findById(id);
    }

    // 조회. @RequestBody 없다.
    @GetMapping("/api/v2/posts/all")
    public String findAll() {
        return "hi";
    }
}
