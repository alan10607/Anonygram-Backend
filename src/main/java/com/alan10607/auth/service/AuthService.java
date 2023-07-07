package com.alan10607.auth.service;

import com.alan10607.auth.constant.RoleType;
import com.alan10607.auth.dto.UserDTO;
import com.alan10607.auth.model.ForumUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Base64;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {
    private final JwtService jwtService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final HttpSession session;

    private static final String E_EMAIL = "Email can't be blank or format not correct";
    private static final String E_PW = "Password can't be blank";
    private static final String E_USERNAME = "UserName can't be blank";

    public UserDTO login(UserDTO userDTO) {
        ForumUser user = (ForumUser) authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(userDTO.getEmail(), userDTO.getPw()))
                .getPrincipal();

        userDTO = new UserDTO(user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getUpdatedDate());
        userDTO.setToken(jwtService.createToken(user));
        return userDTO;
    }

    public UserDTO loginAnonymity() {
        ForumUser user = userService.getTempAnonymousUser(getSessionBase64());
        UserDTO userDTO = new UserDTO(user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getUpdatedDate());
        userDTO.setToken(jwtService.createToken(user));
        return userDTO;
    }

    public void register(UserDTO userDTO) {
        userService.createUser(userDTO, RoleType.NORMAL);
    }


    /**
     * Get temp id by hashing session
     * @return
     */
    private String getSessionBase64(){
        String sessionId = session.getId();//HttpSession is thread safe
        String base64Id = Base64.getEncoder().encodeToString(hashTo6Bytes(sessionId.getBytes()));
        return base64Id;
    }

    /**
     * 每 6 bytes循環取xor, 6 bytes透過Base64編碼剛好是8字元
     * @param bytes
     * @return
     */
    private byte[] hashTo6Bytes(byte[] bytes) {
        byte[] base64 = new byte[6];
        for(int i = 0; i < bytes.length; i++)
            base64[i % 6] ^= (bytes[i] & 0xFF);//& 0xFF: 只取8bits

        return base64;
    }

}