package com.alan10607.leaf.controller;

import com.alan10607.leaf.config.ImgurConfig;
import com.alan10607.leaf.constant.AutoUserId;
import com.alan10607.leaf.dto.LeafDTO;
import com.alan10607.leaf.dto.PostDTO;
import com.alan10607.leaf.service.ContLikeService;
import com.alan10607.leaf.service.ImgurService;
import com.alan10607.leaf.service.PostService;
import com.alan10607.leaf.util.ResponseUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
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
        StringBuffer url = new StringBuffer();
        try{
            url.append(imgurConfig.getAuthorizeUrl())
                .append("?client_id=").append(imgurConfig.getClientId())
                .append("&response_type=").append(imgurConfig.getResponseType())
                .toString();
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return new RedirectView(url.toString());
    }

    @GetMapping("/redirect")
    public String redirect(HttpServletRequest request) {

//        http://localhost:8080/imgur/redirect
//        // #access_token=6502979e123ca1c03d0d8caaa4608781288bbb07
//        // &expires_in=315360000
//        // &token_type=bearer
//        // &refresh_token=bd2bc63d5fcdb5294c1dae67109b5644222cc119
//        // &account_username=alan10607
//        // &account_id=166067797
        return "redirect.html";
    }


    @PostMapping("/getRedirect")
    @ResponseBody
    public ResponseEntity getRedirect(@RequestBody Map<String,String> data){
        try{
            imgurConfig.setAccessToken(data.get("access_token"));
            imgurConfig.setRefreshToken(data.get("refresh_token"));
            return responseUtil.ok();
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity upload(@RequestParam("image") MultipartFile image) {
        try{
            imgurService.upload(image, imgurConfig.getAccessToken());
            return responseUtil.ok();
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }


    @PostMapping("/refreshToken")
    public ResponseEntity refreshToken() {
        try{
            imgurService.refreshToken();
            return responseUtil.ok();
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }



}