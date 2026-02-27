package com.zentrald.config;

import com.zentrald.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(CustomUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ── 인증 방식 ───────────────────────────────────────────────────────
            // formLogin / logout 은 AuthController에서 직접 처리
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)

            // ── 접근 제어 ───────────────────────────────────────────────────────
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/auth/**", "/account/**",
                                 "/css/**", "/js/**", "/images/**").permitAll()
                .anyRequest().authenticated()
            )

            // ── 화면 위변조 방지 보안 헤더 ────────────────────────────────────────
            .headers(headers -> headers

                // 1. 클릭재킹 방지: iframe 삽입 완전 차단
                .frameOptions(frame -> frame.deny())

                // 2. MIME 스니핑 방지: 선언된 Content-Type 외 해석 차단
                .contentTypeOptions(opt -> {})

                // 3. 반사형 XSS 방지 (구형 브라우저 대상, 최신 브라우저는 CSP로 대응)
                .xssProtection(xss -> xss
                    .headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))

                // 4. Content Security Policy
                //    - default-src 'self'  : 모든 리소스는 동일 출처만 허용
                //    - style-src 'unsafe-inline': 인라인 <style> 허용 (외부 CSS 파일로 전환 시 제거 가능)
                //    - script-src 'self'   : 외부 스크립트 차단
                //    - form-action 'self'  : 폼 전송을 동일 출처로만 제한
                //    - frame-ancestors 'none': 모든 iframe 삽입 차단 (X-Frame-Options 보완)
                //    - base-uri 'self'     : <base> 태그 출처 위조 방지
                .contentSecurityPolicy(csp -> csp.policyDirectives(
                    "default-src 'self'; " +
                    "style-src 'self' 'unsafe-inline'; " +
                    "script-src 'self'; " +
                    "img-src 'self' data:; " +
                    "font-src 'self'; " +
                    "connect-src 'self'; " +
                    "form-action 'self'; " +
                    "frame-ancestors 'none'; " +
                    "base-uri 'self'"
                ))

                // 5. Referrer 정책: 동일 출처엔 전체 경로, 외부엔 출처만 전송
                .referrerPolicy(rp -> rp.policy(
                    ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))

                // 6. HSTS (HTTPS 환경에서 활성화 권장 — 개발 환경에서는 주석 해제 후 사용)
                // .httpStrictTransportSecurity(hsts -> hsts
                //     .includeSubDomains(true)
                //     .maxAgeInSeconds(31_536_000))
            );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }
}
