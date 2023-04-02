package com.alan10607.leaf.service.impl;

import com.alan10607.leaf.constant.LeafRoleType;
import com.alan10607.leaf.dto.LeafUserDTO;
import com.alan10607.leaf.model.LeafUser;
import com.alan10607.leaf.service.JwtService;
import com.alan10607.leaf.service.LoginService;
import com.alan10607.leaf.service.UserService;
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
public class LoginServiceImpl implements LoginService{
    private final JwtService jwtService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final HttpSession session;

    private final static String E_EMAIL = "Email can't be blank or format not correct";
    private final static String E_PW = "Password can't be blank";
    private final static String E_USERNAME = "UserName can't be blank";

    public LeafUserDTO login(
            @NotBlank @Email(message = E_EMAIL) String email,
            @NotBlank(message = E_PW) String pw
    ) {
        LeafUser user = (LeafUser) authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, pw)).getPrincipal();

        String token = jwtService.createToken(user);
        return new LeafUserDTO(user.getUsername(),
                user.isAnonymousId(),
                token);
    }

    public LeafUserDTO loginAnonymity() {
        LeafUser anonymousUser = createAnonymousUser();
        String token = jwtService.createToken(anonymousUser);
        return new LeafUserDTO(anonymousUser.getUsername(),
                anonymousUser.isAnonymousId(),
                token);
    }

    public void register(
            @NotBlank @Email(message = E_EMAIL) String email,
            @NotBlank(message = E_USERNAME) String userName,
            @NotBlank(message = E_PW) String pw
    ) {
        userService.createUser(email, userName, pw, LeafRoleType.NORMAL);
    }

    private LeafUser createAnonymousUser() {
        return userService.getAnonymousUser(getSessionBase64());
    }

    /**
     * 透過sessionId取得暫時id
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