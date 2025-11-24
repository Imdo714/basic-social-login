package com.basic.global.config;

import com.basic.api.jwt.JwtProvider;
import com.basic.global.exception.authentication.CustomAuthenticationEntryPoint;
import com.basic.global.jwt.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CorsConfig corsConfig;
    private final JwtProvider jwtProvider;

    public SecurityConfig(CorsConfig corsConfig, JwtProvider jwtProvider) {
        this.corsConfig = corsConfig;
        this.jwtProvider = jwtProvider;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtProvider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(HttpMethod.GET, "/**").permitAll() // GET 요청은 로그인 없이도 접근 가능
                        .requestMatchers("/index", "/kakao/login").permitAll()
                        .anyRequest().authenticated()
                )

                .exceptionHandling(ex -> ex
                        // Security 에서 걸린 애들 즉, authenticated()에 로그인을 안한 애들은 예외처리
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                )

                .addFilterBefore(corsConfig.corsFilter(), ChannelProcessingFilter.class) // ChannelProcessingFilter 실행 전에 CORS 부터 검증
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        ;
        return http.build();
    }

}
