package org.example.echo.mail.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.echo.mail.entity.Mail;

@Mapper
public interface MailMapper {

    Integer save(@Param("mail") Mail mail);

    Integer updateStatus(
            @Param("status") Integer status,
            @Param("updateTime") Long updateTime,
            @Param("id") Long id
    );

}
