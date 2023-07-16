package com.alan10607.leaf.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(path = "/")
@AllArgsConstructor
@Slf4j
public class WebPageController {

//    @RequestMapping("/hub")
//    public String hub(PostDTO postDTO, Model model){
//        try{
//            model.addAttribute("userId", postDTO.getUserId());
//            model.addAttribute("userName", postDTO.getUserName());
//        }catch (Exception e){
//            log.error(e.getMessage());
//        }
//        return "hub.html";//應對templates下的檔案
//    }
//
//    @RequestMapping("/")
//    public String root(PostDTO postDTO, Model model){
//        return "login.html";
//    }

    @RequestMapping("/index")
    public String index(Model model){
        try{
            String leafName = "leaf";//暫時給個預設
            model.addAttribute("leafName", leafName);
            model.addAttribute("picFileName", "leaf.png");//可以改成從DB查到
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return "index.html";//應對templates下的檔案
    }

    @RequestMapping("/index/{leafName}")
    public String indexWithLeafName(@PathVariable("leafName") String leafName,
                                    HttpServletRequest request,
                                    HttpServletResponse response,
                                    Model model){
        try{
//            LeafDTO leafDTO = new LeafDTO();
//            leafDTO.setLeafName(leafName);
////            leafDTO = null;
//            model.addAttribute("leafName", leafDTO.getLeafName());
            model.addAttribute("picFileName", "leaf.png");
        }catch (Exception e){
            response.setStatus(HttpStatus.NOT_FOUND.value());//查不到則回404
            return err(request, response, model);
        }
        return "index.html";
    }

    @RequestMapping("/login")
    public String admin(Model model) {
        return "login.html";
    }

    @RequestMapping("/register")
    public String register(Model model) {
        return "register.html";
    }

    @RequestMapping("/admin")
    public String manager(Model model) {
        return "admin/admin.html";
    }

    @RequestMapping("/err")
    public String err(HttpServletRequest request, HttpServletResponse response, Model model) {
        try{
            model.addAttribute("status", response.getStatus());
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return "err.html";
    }

    @GetMapping("/ssl")
    public String ssl() {
        return "ssl.html";
    }

    /**
     * Use this redirect to parse url hash parameters.
     * Need 'to' parameter in url to define the redirected url.
     * Ex: https://localhost/redirect?to=/imgur/saveToken#access_token=abc
     * will direct to https://localhost/imgur/saveToken?access_token=abc
     * @return
     */
    @GetMapping("/redirect")
    public String redirectHashToParameter() {
        return "redirect.html";
    }

    @GetMapping("/test")
    public String test(){
        return "Hello Leaf";
    }

}