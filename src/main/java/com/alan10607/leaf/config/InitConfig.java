package com.alan10607.leaf.config;

import com.alan10607.auth.constant.RoleType;
import com.alan10607.auth.dao.RoleDAO;
import com.alan10607.auth.dao.UserDAO;
import com.alan10607.auth.model.ForumUser;
import com.alan10607.auth.model.Role;
import com.alan10607.leaf.util.TimeUtil;
import com.alan10607.system.constant.TxnParamKey;
import com.alan10607.system.service.TxnParamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class InitConfig {
    @Bean
    @Order(1)
    public CommandLineRunner initRoleCommand(RoleDAO roleDAO){
        return args -> {
            List<Role> roleList = Arrays.stream(RoleType.values())
                    .map(roleType -> new Role(roleType.id, roleType.name()))
                    .collect(Collectors.toList());
            roleDAO.saveAll(roleList);
        };
    }

    @Bean
    @Order(2)
    public CommandLineRunner initAdminCommand(RoleDAO roleDAO, UserDAO userDAO, BCryptPasswordEncoder bCryptPasswordEncoder){
        return args -> {
            Role role = roleDAO.findByRoleName(RoleType.ADMIN.name());
            userDAO.findByEmail("alan").orElseGet (() -> {
                ForumUser admin = new ForumUser("alan",
                        "alan",
                        bCryptPasswordEncoder.encode("alan"),
                        Collections.singletonList(role),
                        TimeUtil.now());
                return userDAO.save(admin);
            });
        };
    }

    @Bean
    @Order(3)
    public CommandLineRunner initImgurConfigCommand(ImgurConfig imgurConfig, TxnParamService txnParamService){
        return args -> {
            String accessToken = txnParamService.get(TxnParamKey.IMGUR_ACCESS_TOKEN);
            String refreshToken = txnParamService.get(TxnParamKey.IMGUR_REFRESH_TOKEN);
            if (Strings.isBlank(accessToken) || Strings.isBlank(refreshToken)) {
                log.error("No imgur access token, need admin auth");
            }else {
                imgurConfig.setAccessToken(accessToken);
                imgurConfig.setRefreshToken(refreshToken);
                log.info("Get imgur access token from DB succeeded");
            }
        };
    }
}
