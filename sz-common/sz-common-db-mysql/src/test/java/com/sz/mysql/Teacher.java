package com.sz.mysql;

import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * @ClassName Teacher
 * @Author sz
 * @Date 2024/6/21 13:17
 * @Version 1.0
 */
@Data
@Table(value = "teacher")
public class Teacher {
    private Long id;
    private String name;
    private Long createId;
    private Long areaId;
}
