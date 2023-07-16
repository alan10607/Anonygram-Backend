package com.alan10607.leaf.controller;

import com.alan10607.leaf.config.ImgurConfig;
import com.alan10607.leaf.service.ImgurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@RestController
@RequestMapping(path = "/imgur")
@AllArgsConstructor
@Tag(name = "Imgur Setting")
public class ImgurController {
    private final ImgurService imgurService;
    private final ImgurConfig imgurConfig;

    /**
     * Need to set Imgur's redirect as https://localhost/redirect?to=/imgur/saveToken
     * @return
     */
    @GetMapping("/auth")
    @Operation(summary = "Redirect to Imgur authorization URL")
    public RedirectView auth(){
        StringBuffer url = new StringBuffer().append(imgurConfig.getAuthorizeUrl())
                    .append("?client_id=").append(imgurConfig.getClientId())
                    .append("&response_type=token");
        return new RedirectView(url.toString());
    }

    @PostMapping("/refreshToken")
    @Operation(summary = "Refresh token of Imgur authorization")
    public Map<String, String> refreshToken() {
        return imgurService.refreshToken();
    }

    @GetMapping("/saveToken")
    @Operation(summary = "Save token of Imgur authorization, used by Imgur redirect")
    public Map<String, String> saveToken(@RequestParam("access_token") String accessToken,
                                         @RequestParam("refresh_token") String refreshToken){
        return imgurService.saveToken(accessToken, refreshToken);
    }

}