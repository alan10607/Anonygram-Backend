package com.alan10607.ag.config;

import com.alan10607.auth.config.JwtFilter;
import com.alan10607.auth.constant.RoleType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@Slf4j
@Data
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    private final AuthenticationProvider authenticationProvider;
    public final static String FORUM_PATH = "/forum/**";
    public final static String AUTH_PATH = "/auth/**";
    public final static String REDIS_PATH = "/redis/**";
    public final static String IMGUR_PATH = "/imgur/**";
    public final static String SWAGGER_UI_PATH = "/swagger-ui/**";
    public final static String SWAGGER_V3_PATH = "/v3/api-docs/**";

    /**
     * Web security, 代替WebSecurityConfigurerAdapter.configure(HttpSecurity http)
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()//跨域請求偽造, 測試時禁用
                .cors()
//                .and().formLogin().loginPage("/login").loginProcessingUrl("/loginProcessing")
//                .defaultSuccessUrl("/hub").failureForwardUrl("/login?error")
//                .and().logout().logoutUrl("/logoutProcessing").logoutSuccessUrl("/login?logout")
                .and().authorizeRequests().antMatchers(
                    //thymeleaf
                    "/",
                    "/login",
                    "/register",
                    "/user/createUser",
                    "/hub",
                    "/post/**",
                    "/test/**",
                    "/index/**",
                    "/css/**",
                    "/js/**",
                    "/pic/**",
                    //react api
                    "/auth/**",
                    "/ssl",
                    "/redirect",
                    //test
                        FORUM_PATH, REDIS_PATH, AUTH_PATH, IMGUR_PATH, SWAGGER_UI_PATH, SWAGGER_V3_PATH
                ).permitAll()//公開頁面
                .and().authorizeRequests().antMatchers("/post/**")
                .hasAnyAuthority(RoleType.NORMAL.name(), RoleType.ADMIN.name(), RoleType.ANONYMOUS.name())//限制為jwt權限訪問
                .and().authorizeRequests().anyRequest().hasAuthority(RoleType.ADMIN.name())//限制為admin權限訪問
                .and().exceptionHandling().accessDeniedPage("/err")
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//不使用session
                .and().authenticationProvider(authenticationProvider).addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000/"));
        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 設定自訂錯誤頁面
     * @return
     */
    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer() {
        String errorPage = "/err";//所有錯誤頁面都導到這裡
        return factory -> {
            factory.addErrorPages(
                    new ErrorPage(HttpStatus.NOT_FOUND, errorPage),//404
                    new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, errorPage));//500
        };
    }

}