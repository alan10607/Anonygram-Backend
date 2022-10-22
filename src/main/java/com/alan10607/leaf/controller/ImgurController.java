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
                    .append("&response_type=token");
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return new RedirectView(url.toString());
    }

    @GetMapping("/redirect")
    public String redirect(HttpServletRequest request) {
        return "redirect.html";
    }

    @PostMapping("/saveToken")
    @ResponseBody
    public ResponseEntity saveToken(@RequestBody Map<String,String> data){
        try{
            imgurService.saveToken(data.get("access_token"), data.get("refresh_token"));
            return responseUtil.ok();
        }catch (Exception e){
            log.error(e.getMessage());
            return responseUtil.err(e);
        }
    }

    @PostMapping("/refreshToken")
    @ResponseBody
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