package study.jpastudy.web;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import study.jpastudy.config.auth.LoginUser;
import study.jpastudy.config.auth.dto.SessionUser;
import study.jpastudy.service.posts.PostsService;
import study.jpastudy.web.dto.PostsSaveRequestDto;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Controller
public class IndexController {

    private final PostsService postsService;
    private final HttpSession httpSession;

    @RequestMapping("/")
    public String home(@LoginUser SessionUser user, Model model) {

        // 아직 작동안함 -> @LoginUser 만들었더니 작동, 편리.
        if (user != null) {
            model.addAttribute("userName", user.getName()+"user1");
        }

        // 또다른 방법 (비추)
        SessionUser user2 = (SessionUser) httpSession.getAttribute("user");
        if (user2 != null) {
            model.addAttribute("user2Name", user2.getName()+"user2");
        }

        return "index";
    }

    @GetMapping("/posts/save")
    public String postsSave(Model model) {

        model.addAttribute("postsSaveRequestDto", new PostsSaveRequestDto());
        return "posts-save";
    }

    @PostMapping("/posts/save")
    public String create(PostsSaveRequestDto postsSaveRequestDto
            /*@AuthenticationPrincipal SessionUser user */) throws Exception {

        postsService.save(postsSaveRequestDto);

        return "redirect:/";
    }
}
