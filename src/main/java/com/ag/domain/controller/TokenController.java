package com.ag.domain.controller;

import com.ag.domain.dto.TokenDTO;
import com.ag.domain.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@AllArgsConstructor
@Tag(name = "Login Authorization")
public class TokenController {
    private final TokenService tokenService;

    @GetMapping("/token")
    @Operation(summary = "Check login authorization")
    public TokenDTO test() {
        return tokenService.test();
    }

    @PostMapping("/token")
    @Operation(summary = "Create JWT")
    public TokenDTO createToken(@RequestBody TokenDTO tokenDTO,
                                HttpServletResponse response) {
        return tokenService.createToken(tokenDTO, response);
    }

    @PostMapping("/anonymousToken")
    @Operation(summary = "Create anonymous JWT")
    public TokenDTO createAnonymousToken(@RequestBody TokenDTO tokenDTO,
                                         HttpServletResponse response) {
        return tokenService.createAnonymousToken(tokenDTO, response);
    }

    @PutMapping("/token")
    @Operation(summary = "Refresh JWT")
    public TokenDTO refreshToken(@RequestBody TokenDTO tokenDTO) {
        return tokenService.refreshToken(tokenDTO);
    }

}