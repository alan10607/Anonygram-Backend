package com.alan10607.ag.config;

import com.alan10607.ag.service.auth.JwtService;
import com.alan10607.ag.service.auth.UserService;
import com.alan10607.ag.util.HttpUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@Data
@Slf4j
public class CsrfDoubleSubmitFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsServices;
    private final UserService userService;
    private static final String CSRF_NAME = "X-CSRF-TOKEN";
    private static final String BEARER = "Bearer ";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !HttpUtil.isMatchPath(request.getRequestURI(), SecurityConfig.FORUM_PATH);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String cookieCsrf = HttpUtil.getFromCookie(request, CSRF_NAME);
        String headerCsrf = request.getHeader(CSRF_NAME);
        if(Strings.isBlank(cookieCsrf) || Strings.isBlank(headerCsrf) || !cookieCsrf.equals(headerCsrf)){
            log.info("Invalid CSRF double submit, cookie={}, header={}", cookieCsrf, headerCsrf);
//            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
//            return;
        }
        filterChain.doFilter(request, response);
    }

}