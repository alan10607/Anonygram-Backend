package com.ag.domain.model;

import com.ag.domain.constant.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.json.JsonObject;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "user")
public class ForumUser {//implements UserDetails {

    @Id
    @Field(type = FieldType.Keyword)
    private String id;
    private String username;

    @Field(type = FieldType.Keyword)
    private String email;
    private String password;
    private List<UserRole> roles;
    private String headUrl;
    private String language;
    private String theme;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime createdTime;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime updatedTime;

    public ForumUser(String id) {
        this.id = id;
    }


//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        List<SimpleGrantedAuthority> authorities = role
//                .stream()
//                .map(leafRole -> new SimpleGrantedAuthority(leafRole.getRoleName()))
//                .collect(Collectors.toList());
//
//        return authorities;
//    }

//    @Override
//    public String getPassword() {
//        return password;
//    }
//
//    @Override
//    public String getUsername() {
//        return username;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }

    public boolean isAnonymous() {
        return StringUtils.isBlank(email);
    }
}