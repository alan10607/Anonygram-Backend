package com.alan10607.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    private String userName;
    private String email;
    private String pw;

    @ManyToMany(fetch = FetchType.EAGER)//EAGER: 關聯的資料同時取出放入內存, LAZY: 關聯的資料不即時取出, 等要使用再處理
    private List<Role> role = new ArrayList<>();

    private LocalDateTime updatedDate;

    public ForumUser(String userName,
                     String email,
                     String pw,
                     List<Role> role,
                     LocalDateTime updatedDate) {
        this.userName = userName;
        this.email = email;
        this.pw = pw;
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
        return pw;
    }

    @Override
    public String getUsername() {
        return userName;
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

    public void setAnonymousId() {
        this.id = "";
    }

    public boolean isAnonymousId() {
        return this.id.isEmpty();
    }
}