package com.sz.admin.system.service.impl;

import com.sz.admin.system.service.CaptchaService;
import com.sz.core.common.entity.CheckPuzzle;
import com.sz.core.common.entity.PointVO;
import com.sz.core.common.entity.SliderPuzzle;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.AESUtil;
import com.sz.core.util.SlidePuzzleUtil;
import com.sz.core.util.SysConfigUtils;
import com.sz.core.util.Utils;
import com.sz.redis.RedisCache;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * @ClassName CaptchaServiceImpl
 * @Author sz
 * @Date 2025/1/8 17:01
 * @Version 1.0
 */
@Service
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {

    private final RedisCache redisCache;

    @SneakyThrows
    @Override
    public SliderPuzzle getImageCode(HttpServletRequest request) {
        String requestId = Utils.generateSha256Id(Utils.generateCaptchaRequestId(request)); // 根据request标识生成Sha256Id
        String limit = SysConfigUtils.getConfValue("sys.captcha.requestLimit");
        String requestCycle = SysConfigUtils.getConfValue("sys.captcha.requestCycle");
        if (Utils.getIntVal(limit) != 0) {
            redisCache.initializeCaptchaRequestLimit(requestId, Utils.getLongVal(requestCycle));
            Long cacheLimit = redisCache.countCaptchaRequestLimit(requestId);
            CommonResponseEnum.CAPTCHA_LIMIT.assertTrue(cacheLimit > Utils.getLongVal(limit));
        }

        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:/templates/background/*.png"); // 读取背景图片库
        CommonResponseEnum.BACKGROUND_NOT_EXISTS.assertTrue(resources.length == 0);
        Random random = new Random();
        Resource resource = resources[random.nextInt(resources.length)]; // 从背景库中随机获取一张
        SliderPuzzle sliderPuzzle = SlidePuzzleUtil.createImage(resource.getInputStream(), request); // 生成验证码
        CommonResponseEnum.FILE_NOT_EXISTS.assertNull(sliderPuzzle);
        redisCache.limitCaptcha(requestId);
        String expireTime = SysConfigUtils.getConfValue("sys.captcha.expire");
        PointVO pointVO = new PointVO(sliderPuzzle.getPosX(), sliderPuzzle.getPosY(), sliderPuzzle.getSecretKey());

        redisCache.clearCaptcha(requestId); // 清除
        redisCache.putCaptcha(requestId, pointVO, Utils.getLongVal(expireTime)); // 保存到Redis
        sliderPuzzle.setPosX(null); // 清空验证的x坐标
        return sliderPuzzle;
    }

    @Override
    public void checkImageCode(CheckPuzzle checkPuzzle) throws Exception {
        String requestId = checkPuzzle.getRequestId();
        CommonResponseEnum.CAPTCHA_LACK.assertNull(checkPuzzle.getMoveEncrypted());
        CommonResponseEnum.CAPTCHA_EXPIRED.assertFalse(redisCache.existCaptcha(requestId));
        PointVO pointVO = redisCache.getCaptcha(checkPuzzle.getRequestId());
        redisCache.clearCaptcha(requestId); // 用后即消
        String str = AESUtil.aesDecrypt(checkPuzzle.getMoveEncrypted(), pointVO.getSecretKey()); // 解密，获取x位移距离
        int posX = Integer.parseInt(str);
        CommonResponseEnum.CAPTCHA_FAILED.assertTrue(Math.abs(posX - pointVO.getX()) > 3);
    }

}
