package SINCREW.CrewBase.jwt;

import SINCREW.CrewBase.dto.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        //클라이언트 요청에서 identifier, password 추출
        String identifier = request.getParameter("identifier");
        String password = obtainPassword(request);

        //스프링 시큐리티에서 identifier와 password를 검증하기 위해서는 token에 담아야 함
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(identifier, password, null);

        //token에 담은 검증을 위한 AuthenticationManager로 전달
        return authenticationManager.authenticate(authToken);
    }

    //로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {

        //UserDetails
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        // 30분을 밀리초로 환산하여 토큰 만료 시간으로 설정합니다. (1000ms * 60s * 30m)
        Long expiredMs = 1000 * 60 * 30L;

        String token = jwtUtil.createJwt(username, role, expiredMs);

        // 2. HttpOnly 쿠키 생성
        // 클라이언트 측 자바스크립트가 쿠키에 접근하는 것을 막아 XSS 공격을 방어합니다.
        Cookie cookie = new Cookie("auth_token", token);
        cookie.setHttpOnly(true);

        // 3. 보안 설정
        // HTTPS 환경에서만 쿠키가 전송되도록 설정 (개발 환경에서는 false로 둘 수 있음)
        cookie.setSecure(false);
//        cookie.setSecure(true);

        // 4. 쿠키 유효 경로 및 시간 설정
        // "/"로 설정하면 도메인의 모든 경로에서 쿠키가 유효합니다.
        cookie.setPath("/");
        // 쿠키의 최대 수명을 JWT 만료 시간과 동일하게 설정합니다.
        cookie.setMaxAge(expiredMs.intValue() / 1000); // 밀리초를 초로 변환

        // 5. 응답에 쿠키 추가
        // 이 시점에 브라우저가 이 쿠키를 받아서 저장하게 됩니다.
        response.addCookie(cookie);

        // 6. 로그인 성공 응답
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("Login successful!");

//        response.addHeader("Authorization", "Bearer " + token);
    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

    }
}
