package com.sz.core.common.entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointVO {

    private int x;

    private int y;

    private String secretKey;

    private Long createTime;

    public PointVO(int x, int y, String secretKey) {
        this(x, y, secretKey, System.currentTimeMillis());
    }

}
