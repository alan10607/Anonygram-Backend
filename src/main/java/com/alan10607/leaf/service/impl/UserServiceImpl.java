package com.alan10607.leaf.service.impl;

import com.alan10607.leaf.constant.LeafRoleType;
import com.alan10607.leaf.dao.LeafRoleDAO;
import com.alan10607.leaf.dao.LeafUserDAO;
import com.alan10607.leaf.dto.LeafUserDTO;
import com.alan10607.leaf.model.LeafRole;
import com.alan10607.leaf.model.LeafUser;
import com.alan10607.leaf.service.UserService;
import com.alan10607.leaf.util.RedisKeyUtil;
import com.alan10607.leaf.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService{
    private final LeafUserDAO leafUserDAO;
    private final LeafRoleDAO leafRoleDAO;
    private final RedisTemplate redisTemplate;
    private final RedisKeyUtil keyUtil;
    private final TimeUtil timeUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final static int USER_EXPIRE = 3600;

    public LeafUserDTO findUser(String email) {
        if(Strings.isBlank(email)) throw new IllegalStateException("Email can't be blank");

        LeafUser leafUser = leafUserDAO.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        return new LeafUserDTO(leafUser.getId(),
                leafUser.getUsername(),
                leafUser.getEmail(),
                leafUser.getLeafRole(),
                leafUser.getUpdatedDate()
        );
    }

    public List<LeafUserDTO> findAllUser() {
        List<LeafUserDTO> leafUserDTOList = leafUserDAO.findAll().stream()
                .map((leafUser) -> new LeafUserDTO(leafUser.getId(),
                        leafUser.getUsername(),
                        leafUser.getEmail(),
                        leafUser.getLeafRole(),
                        leafUser.getUpdatedDate())
                ).collect(Collectors.toList());

        return leafUserDTOList;
    }

    public void createUser(String email, String userName, String pw, LeafRoleType roleType) {
        if(Strings.isBlank(email)) throw new IllegalStateException("Email can't be blank");
        if(Strings.isBlank(userName)) throw new IllegalStateException("UserName can't be blank");
        if(Strings.isBlank(pw)) throw new IllegalStateException("Password can't be blank");

        leafUserDAO.findByEmail(email).ifPresent((l) -> {
            throw new IllegalStateException("Email already exist");
        });

        LeafRole leafRole = leafRoleDAO.findByRoleName(roleType.name());
        leafUserDAO.save(new LeafUser(userName,
                email,
                bCryptPasswordEncoder.encode(pw),
                Arrays.asList(leafRole),
                timeUtil.now())
        );
    }

    public void updateUserName(String email, String userName) {
        if(Strings.isBlank(email)) throw new IllegalStateException("Email can't be blank");

        LeafUser leafUser = leafUserDAO.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Email not found"));

        leafUser.setUserName(userName);
        leafUserDAO.save(leafUser);
    }

    public void deleteUser(String email) {
        if(Strings.isBlank(email)) throw new IllegalStateException("Email can't be blank");

        LeafUser leafUser = leafUserDAO.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Email not found"));

        leafUserDAO.delete(leafUser);
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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LeafUser leafUser = leafUserDAO.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Spring security get user email: %s not found", email)));

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
            log.error("Pull user name to redis failed, id={}, put id to redis instead", id);
        }

        redisTemplate.opsForValue().set(keyUtil.user(id), userName);
        log.info("Pull user name to redis succeed, id={}", id);

        return userName;
    }

    public void deleteUserNameFromRedis(String id) {
        redisTemplate.delete(keyUtil.user(id));
    }

    @Bean
    CommandLineRunner run(UserService userService){
        return args -> {
            userService.saveRole(new LeafRole(1L, LeafRoleType.ADMIN.name()));
            userService.saveRole(new LeafRole(2L, LeafRoleType.NORMAL.name()));
            LeafRole leafRole = leafRoleDAO.findByRoleName(LeafRoleType.ADMIN.name());
            if(!leafUserDAO.findById(1L).isPresent()){
                leafUserDAO.save(new LeafUser(1L,
                        "alan",
                        "alan",
                        bCryptPasswordEncoder.encode("alan"),
                        Arrays.asList(leafRole),
                        timeUtil.now()));
            }

        };
    }
}