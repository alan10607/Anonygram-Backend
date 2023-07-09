package com.alan10607.leaf.controller;

import com.alan10607.leaf.config.ImgurConfig;
import com.alan10607.leaf.service.ImgurService;
import com.alan10607.leaf.util.ResponseUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@Controller
@RequestMapping(path = "/imgur")
@AllArgsConstructor
@Slf4j
public class ImgurController {
    private final ImgurService imgurService;
    private final ImgurConfig imgurConfig;
    private final ResponseUtil responseUtil;

    @GetMapping("/auth")
    public RedirectView auth(){
        StringBuffer url = new StringBuffer().append(imgurConfig.getAuthorizeUrl())
                    .append("?client_id=").append(imgurConfig.getClientId())
                    .append("&response_type=token");
        return new RedirectView(url.toString());
    }

    @GetMapping("/saveToken")
    public Map<String, String> saveToken(@RequestParam("access_token") String accessToken,
                                         @RequestParam("refresh_token") String refreshToken){
        imgurService.saveToken(accessToken, refreshToken);
        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }

    @PostMapping("/refreshToken")
    public void refreshToken() {
        imgurService.refreshToken();
    }

}