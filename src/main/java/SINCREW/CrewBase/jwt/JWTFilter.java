package SINCREW.CrewBase.jwt;

import SINCREW.CrewBase.dto.CustomUserDetails;
import SINCREW.CrewBase.entity.UserEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. request에서 쿠키들을 가져옵니다.
        Cookie[] cookies = request.getCookies();

        String token = null;

        // 2. 쿠키들 중에서 "auth_token"이라는 이름의 쿠키를 찾습니다.
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("auth_token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // 3. 토큰이 존재하지 않으면, 다음 필터로 진행합니다.
        if (token == null) {
            System.out.println("토큰 없음, 다음 필터로 진행");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 4. 토큰의 유효성을 검증합니다.
            // 만료 여부, 유효성 등
            if (jwtUtil.isExpired(token)) {
                System.out.println("토큰 만료, 다음 필터로 진행");
                filterChain.doFilter(request, response);
                return;
            }

            // 5. 토큰에서 username과 role을 추출합니다.
            String username = jwtUtil.getIdentifier(token);
            String role = jwtUtil.getRole(token);

            // 6. SecurityContextHolder에 저장할 Authentication 객체를 생성합니다.
            // UserEntity 객체와 CustomUserDetails 객체를 사용하여 Spring Security가
            // 인지할 수 있는 형태로 만듭니다.
            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(username);
            userEntity.setRole(role);
            CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

            // 7. SecurityContextHolder에 인증 객체를 저장합니다.
            SecurityContextHolder.getContext().setAuthentication(authToken);

            System.out.println("JWT 토큰 유효, 인증 정보 저장 완료: " + username);

            // 8. 다음 필터로 진행합니다.
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            System.err.println("JWT 처리 중 오류 발생: " + e.getMessage());
            // 토큰이 유효하지 않은 경우, 인증 실패로 간주하고 다음 필터로 진행합니다.
            filterChain.doFilter(request, response);
        }
    }
}