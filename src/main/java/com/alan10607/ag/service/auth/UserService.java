package com.alan10607.ag.service.auth;

import com.alan10607.ag.constant.RoleType;
import com.alan10607.ag.dao.RoleDAO;
import com.alan10607.ag.dao.UserDAO;
import com.alan10607.ag.dto.UserDTO;
import com.alan10607.ag.exception.AnonygramIllegalStateException;
import com.alan10607.ag.model.ForumUser;
import com.alan10607.ag.model.Role;
import com.alan10607.ag.service.redis.LockRedisService;
import com.alan10607.ag.util.TimeUtil;
import com.alan10607.ag.service.redis.UsernameRedisService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserService implements UserDetailsService{
    private final RoleService roleService;
    private final UsernameRedisService usernameRedisService;
    private final LockRedisService lockRedisService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserDAO userDAO;
    private final RoleDAO roleDAO;

    public UserDTO findUser(String email) {
        return userDAO.findByEmail(email)
                .map(gramUser -> new UserDTO(gramUser.getId(),
                        gramUser.getUsername(),
                        gramUser.getEmail(),
                        gramUser.getRole(),
                        gramUser.getUpdatedDate()))
                .orElseThrow(() -> new AnonygramIllegalStateException("User not found"));
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
            throw new AnonygramIllegalStateException("Email already exist");
        });

        userDAO.findByUsername(userDTO.getUsername()).ifPresent(gramUser -> {
            throw new AnonygramIllegalStateException("UserName already exist");
        });

        Role role = roleService.findRole(roleType.name());
        userDAO.save(new ForumUser(
                userDTO.getUsername(),
                userDTO.getEmail(),
                bCryptPasswordEncoder.encode(userDTO.getPassword()),
                Collections.singletonList(role),
                TimeUtil.now())
        );
    }

    public void deleteUser(String email) {
        userDAO.findByEmail(email).ifPresentOrElse(
                forumUser -> userDAO.delete(forumUser),
                () -> { throw new AnonygramIllegalStateException("Email not found"); });
    }

    /**
     * Spring security load username
     * @param email login email
     * @return UserDetails
     * @throws UsernameNotFoundException User not found
     */
    @Override
    public ForumUser loadUserByUsername(String email) throws UsernameNotFoundException {
        ForumUser forumUser = userDAO.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Email not found: %s", email)));

        log.debug("Spring security get user by email: {} succeeded", email);
        return forumUser;//Entity need extend org.springframework.security.core.UserDetails.User
    }
    public String getUsername(String userId) {
        String username = usernameRedisService.get(userId);
        if(Strings.isEmpty(username)){
            lockRedisService.lockByUser(userId, () -> { pullToRedis(userId); });
            username = usernameRedisService.get(userId);
        }
        usernameRedisService.expire(userId);
        return username;
    }

    private void pullToRedis(String userId) {
        String username = userDAO.findById(userId)
                .map(ForumUser::getUsername)
                .orElseGet(() -> {
                    log.error("Pull user failed, userId={}, will put userId as name to redis", userId);
                    return userId;
                });
        usernameRedisService.set(userId, username);
        usernameRedisService.expire(userId);
        log.info("Pull user to redis succeed, userId={}", userId);
    }
    
    public ForumUser getTempAnonymousUser(String tempId) {
        Role role = roleDAO.findByRoleName(RoleType.ANONYMOUS.name());
        return new ForumUser(tempId,
                tempId,
                "",
                "",
                Collections.singletonList(role),
                TimeUtil.now());
    }

}