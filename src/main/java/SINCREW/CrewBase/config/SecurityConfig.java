package SINCREW.CrewBase.config;

import SINCREW.CrewBase.jwt.CustomAccessDeniedHandler;
import SINCREW.CrewBase.jwt.JWTFilter;
import SINCREW.CrewBase.jwt.JWTUtil;
import SINCREW.CrewBase.jwt.LoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    //JWTUtil 주입
    private final JWTUtil jwtUtil;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }



    // 비밀번호 암호화
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf((auth) -> auth
                        .disable())
                .formLogin((auth) -> auth
                        .disable())
                .httpBasic((auth) -> auth
                        .disable());

        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/dashboard").hasRole("USER")

                        .requestMatchers("/login", "/").permitAll()
                        .requestMatchers("/member/**", "/api/**").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                        .anyRequest().authenticated());


        //AuthenticationManager()와 JWTUtil 인수 전달
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);
        //JWTFilter 등록
        http
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        // 익명 사용자를 위한 기본 인증 필터를 비활성화
        http.anonymous(anonymous -> anonymous.disable());

        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.exceptionHandling(exception -> exception
                .accessDeniedPage("/access-denied") // 접근 거부 시 표시할 페이지
                .accessDeniedHandler(new CustomAccessDeniedHandler())
        );

        return http.build();
    }
}