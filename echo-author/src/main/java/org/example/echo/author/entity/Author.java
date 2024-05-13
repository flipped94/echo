package org.example.echo.author.entity;

import lombok.Data;
import org.apache.commons.codec.digest.Md5Crypt;
import org.example.common.enums.GenderEnum;
import org.example.common.util.StringUtil;

/**
 * 作者表
 */
@Data
public class Author {
    private Long id;
    private String email;
    private String nickname;
    private String avatar;
    private String profile;
    private Integer gender;
    private String career;
    private Long birthday;
    private String degree;
    private Integer status;
    private String salt;
    private String password;
    private Long createTime;
    private Long updateTime;

    public static Author forCreate(String email, String password) {
        final Author author = new Author();
        author.setEmail(email);
        author.setNickname("echo_" + email.substring(0, 4));
        author.setAvatar("/default_header.png");
        author.setProfile("");
        author.setGender(GenderEnum.UNKONWN.getCode());
        author.setCareer("");
        author.setBirthday(0L);
        author.setDegree("");
        author.setStatus(1);
        author.setSalt(StringUtil.generateRandomString(20));
        author.setPassword(Md5Crypt.apr1Crypt(password, author.getSalt()));
        author.setCreateTime(System.currentTimeMillis());
        author.setUpdateTime(System.currentTimeMillis());
        return author;
    }
}
