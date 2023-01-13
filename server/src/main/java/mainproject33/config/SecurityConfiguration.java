package mainproject33.config;

import lombok.RequiredArgsConstructor;
import mainproject33.global.security.handler.CustomAccessDeniedHandler;
import mainproject33.global.security.handler.CustomAuthenticationEntryPoint;
import mainproject33.global.security.jwt.JwtTokenizer;
import mainproject33.global.security.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtTokenizer jwtTokenizer;
    private final AuthService authService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(withDefaults())
                .headers().frameOptions().disable()
                .and()

                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                .httpBasic().disable()
                .formLogin().disable()

                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .accessDeniedHandler(new CustomAccessDeniedHandler())
                .and()

                .apply(new CustomFilterConfiguration(jwtTokenizer, authService))
                .and()

                .authorizeRequests(auth -> auth
                        .antMatchers("/h2/**").permitAll()
                        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .antMatchers(HttpMethod.POST, "/api/matches").hasRole("USER")
                        .antMatchers(HttpMethod.PATCH, "/api/matches/{matchId}").hasRole("USER")
                        //.access("@matchBoardService.checkMember(principal, T(Long).parseLong(#matchId)")
                        .antMatchers(HttpMethod.DELETE, "/api/matches/{matchId}").hasRole("USER")
                        //.access("@matchBoardService.checkMember(principal, T(Long).parseLong(#matchId)")

                        .antMatchers(HttpMethod.POST, "/api/boards/**").hasRole("USER")
                        .antMatchers(HttpMethod.PATCH, "/api/boards/{boardId}").hasRole("USER")
                        //.access("@userBoardService.checkMember(principal, T(Long).parserLong(#boardId)")
                        .antMatchers(HttpMethod.DELETE, "/api/boards/{boardId}").hasRole("USER")
                        //.access("@userBoardService.checkMember(principal, T(Long).parserLong(#boardId)")
                        .antMatchers(HttpMethod.POST, "/api/boards/{boardId}/likes").hasRole("USER")

                        .antMatchers(HttpMethod.POST, "/api/boards/{boardId}/comments").hasRole("USER")
                        .antMatchers(HttpMethod.PATCH, "/api/boards/{boardId}/comments/{commentId}").hasRole("USER")
                        //.access("@commentService.checkMember(pricipal, T(Long).parserLong(#commentId)")
                        .antMatchers(HttpMethod.DELETE, "/api/boards/{boardId}/comments/{commentId}").hasRole("USER")
                        //.access("@commentService.checkMember(pricipal, T(Long).parserLong(#commentId)")
                        .antMatchers(HttpMethod.POST,
                                "/api/boards/{boardId}/comments/{commentId}/likes").hasRole("USER")

                        .antMatchers(HttpMethod.POST, "/api/members/logout").hasRole("USER")
                        .antMatchers(HttpMethod.PATCH, "/api/members/{memberId}").hasRole("USER")
                        //.access("T(mainproject33.domain.member.entity.member).cast(principal).getId() "
                                //+ "== T(Long).parseLong(#memberId)")
                        .antMatchers(HttpMethod.DELETE, "/api/members/{memberId}").hasRole("USER")
                        //.access("T(mainproject33.domain.member.entity.member).cast(principal).getId() "
                                //+ "== T(Long).parseLong(#memberId)")
                        .antMatchers(HttpMethod.POST, "/api/members/follows").hasRole("USER")
                        .anyRequest().permitAll()
                )

                .logout().disable();

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowedOriginPatterns(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of("*"));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setExposedHeaders(List.of("Authorization", "refreshToken"));
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }



}