package com.alan10607.auth.service;

import com.alan10607.auth.constant.RoleType;
import com.alan10607.auth.dao.RoleDAO;
import com.alan10607.auth.dao.UserDAO;
import com.alan10607.auth.dto.UserDTO;
import com.alan10607.auth.model.Role;
import com.alan10607.auth.model.ForumUser;
import com.alan10607.leaf.util.RedisKeyUtil;
import com.alan10607.leaf.util.TimeUtil;
import com.alan10607.redis.service.UserNameRedisService;
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
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Validated
public class UserService implements UserDetailsService{
    private final RoleService roleService;
    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final RedisTemplate redisTemplate;
    private final RedisKeyUtil keyUtil;
    private final TimeUtil timeUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private static final int USER_EXPIRE = 3600;

    private static final String E_EMAIL = "Email can't be blank or format not correct";
    private static final String E_PW = "Password can't be blank";
    private static final String E_USERNAME = "UserName can't be blank";

    private final UserNameRedisService userNameRedisService;

    public UserDTO findUser(String email) {
        return userDAO.findByEmail(email)
                .map(gramUser -> new UserDTO(gramUser.getId(),
                        gramUser.getUsername(),
                        gramUser.getEmail(),
                        gramUser.getRole(),
                        gramUser.getUpdatedDate()))
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    public List<UserDTO> findAllUser() {
        return userDAO.findAll().stream()
                .map(gramUser -> new UserDTO(gramUser.getId(),
                        gramUser.getUsername(),
                        gramUser.getEmail(),
                        gramUser.getRole(),
                        gramUser.getUpdatedDate()))
                .collect(Collectors.toList());
    }

    public void createUser(UserDTO userDTO, RoleType roleType) {
        userDAO.findByEmail(userDTO.getEmail()).ifPresent(gramUser -> {
            throw new IllegalStateException("Email already exist");
        });

        userDAO.findByUserName(userDTO.getUserName()).ifPresent(gramUser -> {
            throw new IllegalStateException("UserName already exist");
        });

        Role role = roleService.findRole(roleType.name());
        userDAO.save(new ForumUser(
                userDTO.getUserName(),
                userDTO.getEmail(),
                bCryptPasswordEncoder.encode(userDTO.getPw()),
                Arrays.asList(role),
                timeUtil.now())
        );
    }

    public void deleteUser(String email) {
        ForumUser forumUser = userDAO.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Email not found"));

        userDAO.delete(forumUser);
    }

    /**
     * Spring security load username
     * @param email
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public ForumUser loadUserByUsername(String email) throws UsernameNotFoundException {
        ForumUser forumUser = userDAO.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Email not found: %s", email)));

        log.info("Spring security get user by email: {} succeeded", email);
        return forumUser;//Entity need extend org.springframework.security.core.userdetails.User
    }
    public String getUserName(String userId) {
        String userName = userNameRedisService.get(userId);
        if(Strings.isEmpty(userName)){
            pullToRedis(userId);
            userName = userNameRedisService.get(userId);
        }
        userNameRedisService.expire(userId);
        return userName;
    }

    private void pullToRedis(String userId) {
        String userName = userDAO.findById(userId)
                .map(ForumUser::getId)
                .orElseGet(() -> {//need test
                    log.error("Pull user failed, userId={}, will put empty data to redis", userId);
                    return "";
                });
        userNameRedisService.set(userId, userName);
        userNameRedisService.expire(userId);
        log.info("Pull user to redis succeed, userId={}", userId);
    }
    
    public ForumUser getAnonymousUser(String userName) {
        Role role = roleDAO.findByRoleName(RoleType.ANONYMOUS.name());
        ForumUser anonymousUser = new ForumUser();
        anonymousUser.setAnonymousId();
        anonymousUser.setUserName(userName);
        anonymousUser.setEmail("");
        anonymousUser.setRole(List.of(role));
        return anonymousUser;
    }

    @Bean
    public CommandLineRunner userCommand(RoleService roleService){
        return args -> {
            roleService.saveRole(new Role(1L, RoleType.ADMIN.name()));
            roleService.saveRole(new Role(2L, RoleType.NORMAL.name()));
            roleService.saveRole(new Role(3L, RoleType.ANONYMOUS.name()));
            Role role = roleService.findRole(RoleType.ADMIN.name());
            userDAO.findByEmail("alan").orElseGet(() ->
                    userDAO.save(new ForumUser("alan",
                            "alan",
                            bCryptPasswordEncoder.encode("alan"),
                            Arrays.asList(role),
                            timeUtil.now())));
        };
    }
}