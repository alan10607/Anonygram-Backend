package com.alan10607.leaf.service.impl;

import com.alan10607.leaf.constant.LeafRoleType;
import com.alan10607.leaf.dao.LeafRoleDAO;
import com.alan10607.leaf.dao.LeafUserDAO;
import com.alan10607.leaf.dto.LeafUserDTO;
import com.alan10607.leaf.model.LeafRole;
import com.alan10607.leaf.model.LeafUser;
import com.alan10607.leaf.service.JwtService;
import com.alan10607.leaf.service.UserService;
import com.alan10607.leaf.util.RedisKeyUtil;
import com.alan10607.leaf.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService{
    private final JwtService jwtService;
    private final LeafUserDAO leafUserDAO;
    private final LeafRoleDAO leafRoleDAO;
//    private final AuthenticationManager authenticationManager;
    private final RedisTemplate redisTemplate;
    private final RedisKeyUtil keyUtil;
    private final TimeUtil timeUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final HttpSession session;
    private final static int USER_EXPIRE = 3600;
    private final static long ANONYMOUS_ID = -1L;//預設匿名id

    private final static String E_EMAIL = "Email can't be blank or format not correct";
    private final static String E_PW = "Password can't be blank";
    private final static String E_USERNAME = "UserName can't be blank";

    public LeafUserDTO login(
            @NotBlank @Email(message = E_EMAIL) String email,
            @NotBlank(message = E_PW) String pw
    ) {
//        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, pw));//進行驗證
        LeafUser user = leafUserDAO.findByEmail(email)
                            .orElseThrow(() -> new IllegalStateException("User not found"));
        String token = jwtService.createToken(email, user);
        return new LeafUserDTO(user.getUsername(),
                user.isAnonymousId(),
                token);
    }

    public LeafUserDTO loginAnonymity() {
        LeafUser anonymousUser = createAnonymousUser();
        String token = jwtService.createAnonymousToken(anonymousUser);
        return new LeafUserDTO(anonymousUser.getUsername(),
                anonymousUser.isAnonymousId(),
                token);
    }

    public void register(
            @NotBlank @Email(message = E_EMAIL) String email,
            @NotBlank(message = E_USERNAME) String userName,
            @NotBlank(message = E_PW) String pw
    ) {
        createUser(email, userName, pw, LeafRoleType.NORMAL);
    }

    private LeafUser createAnonymousUser() {
        return getAnonymousUser(getSessionBase64());
    }

    public LeafUser getAnonymousUser(String userName) {
        LeafRole role = findRole(LeafRoleType.ANONY.name());
        LeafUser anonymousUser = new LeafUser();
        anonymousUser.setAnonymousId();
        anonymousUser.setUserName(userName);
        anonymousUser.setLeafRole(Arrays.asList(role));
        return anonymousUser;
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

    public LeafUserDTO findUser(
            @NotBlank @Email(message = E_EMAIL) String email
    ) {
        if(Strings.isBlank(email)) throw new IllegalStateException("Email can't be blank");

        return leafUserDAO.findByEmail(email)
                .map(leafUser -> new LeafUserDTO(
                        leafUser.getId(),
                        leafUser.getUsername(),
                        leafUser.getEmail(),
                        leafUser.getLeafRole(),
                        leafUser.getUpdatedDate()
                )).orElseThrow(() -> new IllegalStateException("User not found"));
    }

    public List<LeafUserDTO> findAllUser() {
        List<LeafUserDTO> leafUserDTOList = leafUserDAO.findAll().stream()
                .map((leafUser) -> new LeafUserDTO(
                        leafUser.getId(),
                        leafUser.getUsername(),
                        leafUser.getEmail(),
                        leafUser.getLeafRole(),
                        leafUser.getUpdatedDate())
                ).collect(Collectors.toList());

        return leafUserDTOList;
    }

    public void createUser(
            @NotBlank @Email(message = E_EMAIL) String email,
            @NotBlank(message = E_USERNAME) String userName,
            @NotBlank(message = E_PW) String pw,
            @NotNull LeafRoleType roleType
    ) {
//        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]+$";
//        if(!Pattern.matches(emailRegex, email))
//            throw new IllegalStateException("Email format not correct");

        leafUserDAO.findByEmail(email).ifPresent((l) -> {
            throw new IllegalStateException("Email already exist");
        });

        leafUserDAO.findByUserName(userName).ifPresent((l) -> {
            throw new IllegalStateException("UserName already exist");
        });

        LeafRole leafRole = findRole(roleType.name());
        leafUserDAO.save(new LeafUser(
                userName,
                email,
                bCryptPasswordEncoder.encode(pw),
                Arrays.asList(leafRole),
                timeUtil.now())
        );
    }

    public void updateUserName(
            @NotBlank @Email(message = E_EMAIL) String email,
            @NotBlank(message = E_USERNAME) String userName
    ) {
        LeafUser leafUser = leafUserDAO.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Email not found"));

        leafUser.setUserName(userName);
        leafUserDAO.save(leafUser);
    }

    public void deleteUser(
            @NotBlank @Email(message = E_EMAIL) String email
    ) {
        if(Strings.isBlank(email)) throw new IllegalStateException("Email can't be blank");

        LeafUser leafUser = leafUserDAO.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Email not found"));

        leafUserDAO.delete(leafUser);
    }

    public LeafRole findRole(String roleName) {
        return leafRoleDAO.findByRoleName(roleName);
    }

    public void saveRole(LeafRole leafRole) {
        leafRoleDAO.save(leafRole);
    }

    /**
     * Spring security load username
     * @param email
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public LeafUser loadUserByUsername(String email) throws UsernameNotFoundException {
        LeafUser leafUser = leafUserDAO.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Email not found: %s", email)));

        log.info("Spring security get user by email: {} succeeded", email);

        //org.springframework.security.core.userdetails.User
        return leafUser;
    }

    public String findUserNameFromRedis(String id) {
        String userName = (String) redisTemplate.opsForValue().get(keyUtil.user(id));
        if(userName == null) {
            userName = pullUserNameToRedis(id);
        }
        redisTemplate.expire(keyUtil.user(id), keyUtil.getRanExp(USER_EXPIRE), TimeUnit.SECONDS);

        return userName;
    }

    private String pullUserNameToRedis(String id) {
        String userName = "";
        try {
            LeafUser leafUser = leafUserDAO.findById(Long.parseLong(id))
                    .orElseThrow(() -> new IllegalStateException("User not found"));
            userName = leafUser.getUsername();
        }catch (Exception e){
            userName = id;
            log.info("Pull user name to redis failed, id={}, put id to redis instead", id);
        }

        redisTemplate.opsForValue().set(keyUtil.user(id), userName);
        log.info("Pull user name to redis succeed, id={}", id);

        return userName;
    }

    public void deleteUserNameFromRedis(String id) {
        redisTemplate.delete(keyUtil.user(id));
    }

    @Bean
    public CommandLineRunner userCommand(UserService userService){
        return args -> {
            userService.saveRole(new LeafRole(1L, LeafRoleType.ADMIN.name()));
            userService.saveRole(new LeafRole(2L, LeafRoleType.NORMAL.name()));
            userService.saveRole(new LeafRole(3L, LeafRoleType.ANONY.name()));
            LeafRole leafRole = userService.findRole(LeafRoleType.ADMIN.name());
            if(!leafUserDAO.findByEmail("alan").isPresent()){
                leafUserDAO.save(new LeafUser(
                        "alan",
                        "alan",
                        bCryptPasswordEncoder.encode("alan"),
                        Arrays.asList(leafRole),
                        timeUtil.now()));
            }
        };
    }
}