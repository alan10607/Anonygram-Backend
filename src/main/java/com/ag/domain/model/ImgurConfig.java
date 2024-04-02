package com.ag.domain.model;

import com.ag.domain.model.base.ConfigEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@ConfigurationProperties(prefix = "imgur.client")
public class ImgurConfig extends ConfigEntity {

    @Override
    public Type getType() {
        return Type.IMGUR;
    }

    @Field(type = FieldType.Text)
    private String authorizeUrl;

    @Field(type = FieldType.Text)
    private String tokenUrl;

    @Field(type = FieldType.Text)
    private String uploadUrl;

    @Field(type = FieldType.Text)
    private String clientId;

    @Field(type = FieldType.Text)
    private String clientSecret;

    @Field(type = FieldType.Text)
    private String albumId;

    @Field(type = FieldType.Text)
    private String accessToken;

    @Field(type = FieldType.Text)
    private String refreshToken;

    public boolean isAllConfigNotBlank() {
        return isDefaultConfigNotBlank()
                && isAccessTokenNotBlank()
                && isRefreshTokenNotBlank();
    }

    public boolean isDefaultConfigNotBlank() {
        return StringUtils.isNotBlank(authorizeUrl)
                && StringUtils.isNotBlank(tokenUrl)
                && StringUtils.isNotBlank(uploadUrl)
                && StringUtils.isNotBlank(clientId)
                && StringUtils.isNotBlank(clientSecret)
                && StringUtils.isNotBlank(albumId);
    }

    public boolean isAccessTokenNotBlank() {
        return StringUtils.isNotBlank(accessToken);
    }

    public boolean isRefreshTokenNotBlank() {
        return StringUtils.isNotBlank(refreshToken);
    }
}