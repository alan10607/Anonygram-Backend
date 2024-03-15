package com.ag.domain.service;

import com.ag.domain.dto.UserDTO;
import com.ag.domain.model.ForumUser;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import static com.ag.domain.service.JwtService.ACCESS_TOKEN;

@Service
@AllArgsConstructor
@Slf4j
public class LoginService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UserDTO login(UserDTO userDTO, HttpSession httpSession) {
        ForumUser user = (ForumUser) authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(userDTO.getEmail(), userDTO.getPassword()))
                .getPrincipal();

        userDTO.setAccessToken(jwtService.createAccessToken(user));
        userDTO.setRefreshToken(jwtService.createRefreshToken(user));
        httpSession.setAttribute(ACCESS_TOKEN, userDTO.getAccessToken());
        return userDTO;
    }




//    public void setResponseJwtCookie(HttpServletResponse response, ForumUser user) {
//        UsernamePasswordAuthenticationFilter
//        String accessToken = createAccessToken(user);
//        String refreshToken = createRefreshToken(user);
//        response.addHeader(HttpHeaders.SET_COOKIE, getCookieByJwtToken(ACCESS_TOKEN, accessToken).toString());
//        response.addHeader(HttpHeaders.SET_COOKIE, getCookieByJwtToken(REFRESH_TOKEN, refreshToken).toString());
//        response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, SET_JWT);
//        response.addHeader(SET_JWT, getJwtHeaderValue(accessToken, refreshToken));//for situation when phone's browser rejects cross-site cookies
//    }
//
//    private String getJwtHeaderValue(String accessToken, String refreshToken) {
//        try {
//            Map<String, String> jwtHeader = Map.of(ACCESS_TOKEN, accessToken, REFRESH_TOKEN, refreshToken);
//            return new ObjectMapper().writeValueAsString(jwtHeader);
//        } catch (JsonProcessingException e) {
//            log.error("Failed to create Jwt Header string", e);
//        }
//        return "";
//    }
//
//    private ResponseCookie getCookieByJwtToken(String cookieName, String token) {
//        return ResponseCookie.from(cookieName, token)
//                .maxAge(extractMaxAge(token))
//                .path("/")
//                .httpOnly(true)
//                .secure(true)
//                .sameSite("None")//or default value is Lax
//                .build();
//    }




//    public UserDTO anonymousLogin(HttpServletResponse response) {
//        ForumUser user = userService.getTempAnonymousUser(getUUIDBase64());
//        UserDTO userDTO = UserDTO.from(user);
//
//        jwtService.setResponseJwtCookie(response, user);
//        return userDTO;
//    }
//
//    public void register(UserDTO userDTO) {
//        userService.create(userDTO, RoleType.NORMAL);
//    }
//
//    private String getUUIDBase64(){
//        String tempId = UUID.randomUUID().toString();
//        return Base64.getEncoder().encodeToString(hashTo6Bytes(tempId.getBytes()));
//    }
//
//    /**
//     * If using this function, SessionCreationPolicy.STATELESS will not work
//     * Get temp id by hashing session
//     * @return
//     */
//    private String getSessionBase64(){
//        String sessionId = session.getId();//HttpSession is thread safe
//        return Base64.getEncoder().encodeToString(hashTo6Bytes(sessionId.getBytes()));
//    }
//
//    /**
//     * XOR every 6 bytes in a loop, and encoding those 6 bytes with Base64 results in exactly 8 characters
//     * @param bytes session bytes
//     * @return hash id code
//     */
//    private byte[] hashTo6Bytes(byte[] bytes) {
//        byte[] base64 = new byte[6];
//        for(int i = 0; i < bytes.length; i++)
//            base64[i % 6] ^= (bytes[i] & 0xFF);//& 0xFF: 只取8bits
//
//        return base64;
//    }

}