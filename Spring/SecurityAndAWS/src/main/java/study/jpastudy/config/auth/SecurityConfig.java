package study.jpastudy.config.auth;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import study.jpastudy.domain.user.Role;

@RequiredArgsConstructor
@EnableWebSecurity      // Spring Security 설정들 활성화
@Configuration(proxyBeanMethods = false)
@ConditionalOnDefaultWebSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.
                csrf().disable().headers().frameOptions().disable() // h2-console 화면 사용하기 위해 해당 옵션들 disable
                .and()
                .authorizeRequests() // URL별 권한 관리를 설정하는 옵션의 시작점. 이제 antMatchers 옵션들 사용 가능
                // antMatchers: 권한 관리 대상 지정. URL, HTTP 메소드별로 관리 가능.
                .antMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**", "/api/v2/**").permitAll() // 전체 열람 권한
                .antMatchers("/api/v1/**").hasRole(Role.USER.name()) // USER만 가능
                .anyRequest() // 설정된 값들 이외 나머지 URL들.
                .authenticated() // 그 URL들은 모두 인증된 사용자들에게만 허용. (로그인한 사용자)
                .and()
                .logout().logoutSuccessUrl("/") // 로그아웃 기능의 여러 설정의 진입점. 로그아웃 성공시 / 주소로 이동.
                .and()
                .oauth2Login() // OAuth2 로그인 기능에 대한 여러 설정의 진입점.
                .userInfoEndpoint() // OAuth2 로그인 성공 이후 사용자 정보를 가져올 때의 설정들을 담당.
                .userService(customOAuth2UserService); // 소셜 로그인 성공시 후속 조치를 진행할 UserService 인터페이스의 구현체를
        // 등록. 리소스 서버(소셜서비스들)에서 사용자 정보를 가져온 상태에서 추가로 진행하고자 하는 기능 명시 가능.

                return http.build();
    }
}
