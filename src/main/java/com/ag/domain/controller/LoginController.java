package com.ag.domain.controller;

import com.ag.domain.dto.UserDTO;
import com.ag.domain.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@AllArgsConstructor
@Tag(name = "Login Authorization")
public class LoginController {
    private final LoginService loginService;
//    private final JwtService jwtService;

//    @GetMapping("/test")
//    @Operation(summary = "Check login authorization")
//    public UserDTO test(){
//        try {
//            ForumUser user = AuthUtil.getUser();
//            return UserDTO.from(user);
//        }catch(AnonygramIllegalStateException e) {
//            return new UserDTO();
//        }
//    }

    @PostMapping("/login")
    @Operation(summary = "To login system")
    public UserDTO login(@RequestBody UserDTO userDTO,
                         HttpServletResponse response,
                         HttpSession httpSession){
        return loginService.login(userDTO, response);
    }


//    private final AuthenticationManager authenticationManager;
//    private final SecurityContextHolderStrategy securityContextHolderStrategy;
//    private final SecurityContextRepository securityContextRepository;
//    @PostMapping("/login")
//    public void login(@RequestBody UserDTO loginRequest, HttpServletRequest request, HttpServletResponse response) {
//        UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.unauthenticated(
//                loginRequest.getUsername(), loginRequest.getPassword());
//        Authentication authentication = authenticationManager.authenticate(token);
//        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
//        context.setAuthentication(authentication);
//        securityContextHolderStrategy.setContext(context);
//        securityContextRepository.saveContext(context, request, response);
//        SessionManagementFilter
//    }
//
//    @PostMapping("/anonymous")
//    @Operation(summary = "To login as anonymous user")
//    public UserDTO loginAnonymity(HttpServletResponse response){
//        return authService.anonymousLogin(response);
//    }
//
//    @PostMapping("/register")
//    @Operation(summary = "To register system")
//    public void register(@RequestBody @Validated(UserDTO.registerGroup.class) UserDTO userDTO){
//        authService.register(userDTO);
//    }

}