package com.alan10607.ag.model;

import com.alan10607.ag.constant.LanguageType;
import com.alan10607.ag.constant.ThemeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForumUser implements UserDetails {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;
    private String username;
    private String email;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)//EAGER: Load related object immediately when queried, LAZY: Only load related object when needed
    private List<Role> role = new ArrayList<>();
    private String headUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LanguageType language;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ThemeType theme;

    private LocalDateTime updatedDate;

    public ForumUser(String username,
                     String email,
                     String password,
                     List<Role> role,
                     LocalDateTime updatedDate) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.updatedDate = updatedDate;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = role
                .stream()
                .map(leafRole -> new SimpleGrantedAuthority(leafRole.getRoleName()))
                .collect(Collectors.toList());

        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean isAnonymous() {
        return Strings.isBlank(email);
    }
}