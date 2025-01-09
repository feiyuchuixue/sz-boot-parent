package com.sz.admin.system.service;

import com.sz.core.common.entity.SliderPuzzle;
import com.sz.core.common.entity.CheckPuzzle;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @ClassName CaptchaService
 * @Author sz
 * @Date 2025/1/8 17:01
 * @Version 1.0
 */
public interface CaptchaService {

    SliderPuzzle getImageCode(HttpServletRequest request);

    void checkImageCode(CheckPuzzle checkPuzzle) throws Exception;
}
