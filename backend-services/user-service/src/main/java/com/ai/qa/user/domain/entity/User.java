package com.ai.qa.user.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 映射数据库中的用户表，存储系统用户的基本信息
 * 使用JPA注解进行对象关系映射(ORM)
 *
 * @author Rong Wen
 * @version 1.0
 */
@Entity
@Table(name = "user_rw") // 映射数据库表名
@Data // Lombok注解，自动生成getter、setter、toString等方法
public class User {
    /**
     * 用户唯一标识ID
     * 主键，自增长策略，由数据库自动生成
     *
     * @apiNote 示例值: 1, 2, 3...
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


}
