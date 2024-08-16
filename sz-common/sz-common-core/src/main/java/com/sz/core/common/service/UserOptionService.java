package com.sz.core.common.service;

import com.sz.core.common.entity.UserOptionVO;

import java.util.List;

/**
 * @ClassName UserOptionService
 * @Author sz
 * @Date 2024/8/16 8:47
 * @Version 1.0
 */
public interface UserOptionService {
    /**
     * 获取用户Options信息
     * @return
     */
    List<UserOptionVO> getUserOptions();
}
