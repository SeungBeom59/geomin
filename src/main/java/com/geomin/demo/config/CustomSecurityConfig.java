package com.geomin.demo.config;

import com.geomin.demo.security.CustomUserDetailsService;
import com.geomin.demo.security.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Slf4j
@Configuration
//@EnableGlobalMethodSecurity(prePostEnabled = true) deprecated 됨
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
// securedEnabled => Secured 애노테이션 사용 여부, prePostEnabled => PreAuthorize 어노테이션 사용 여부.
@RequiredArgsConstructor
public class CustomSecurityConfig {

    private final DataSource dataSource;
    private final CustomUserDetailsService userDetailsService;

    // Spring 3.0 이상 버전부터는 Spring Security 6.0 이상 버전만 사용가능, 기존방식 불가.
    // webSecurityConfigureadapter를 extends 해서 만들기도 했는데 deprecated 되어서 더 이상 사용불가.
    // 무조건 @Bean 등록한 SecurityFilterChain 만들어서 이용!
    // spring security 6.1.0 부터는 메서드 체이닝의 사용을 지향하고 람다식을 통해 함수형을 설정해야함.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("-----------------------configur---------------------");

        http
                // 언급된 주소들만 접근허용 이외 보안적용
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login").permitAll()
                        .anyRequest().authenticated())
                // csrf 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                // fromLogin 사용 및 커스텀페이지 지정
                .formLogin(formlogin -> formlogin
                        .loginPage("/login")                                    // 커스텀 로그인 페이지 사용
                        .usernameParameter("id")                                // html 아이디 파라미터 설정
                        .defaultSuccessUrl("/reception")                        // 성공시 기본 이동 페이지
                        .failureUrl("/login?error=true")     // 실패시 기본 이동 페이지
                        .successHandler(new LoginSuccessHandler())
                )
                // 자동 로그인 사용
                .rememberMe(rememberme -> rememberme
                        .key("12345678")
                        .tokenRepository(persistentTokenRepository())
                        .userDetailsService(userDetailsService)
                        .tokenValiditySeconds(60*60*24*30)
                )
                // 세션 설정
                .sessionManagement(SessionManagement -> SessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)   // 시큐리티가 필요로 할때만 세션 생성
                        .sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::newSession) // 세션 고정 보호: 전략은 새로운 세션 생성
                        .maximumSessions(1)                                         // 사용자의 세션 최대 수 1로 고정(세션 하이 재킹 , 아이디 동시 접속 불가)
                        .maxSessionsPreventsLogin(false)                             // false(기본) 기존 사용자 세션 만료 | true 새로운 사용자 인증예외 발생
                        .expiredUrl("/login?expired=ture")); // 세션 만료시 login으로 이동, expired=true를 줘서 세션만료임을 메시지로 나타내기 가능.

        return http.build();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        return repo;
    }

    // 웹 보안 커스터마이징 인터페이스를 이용하여 웹 보안 추가 설정
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        log.info("-----------------------web confiure---------------------");

        // 정적 파일에 대하여 security 검사를 제외하라.
        // PathRequest.toStaticResources().atCommonLocations()는 정적주소의 위치
        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers("/css/**", "/js/**","/img/**");
    }

    // 패스워드 해시 암호화 클래스 추가
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }



}
