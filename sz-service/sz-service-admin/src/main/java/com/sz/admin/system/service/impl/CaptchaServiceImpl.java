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

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * CaptchaServiceImpl
 *
 * @author sz
 * @version 1.0
 * @since 2025/1/8 17:01
 */
@Service
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {

    private final RedisCache redisCache;

    private final SecureRandom random = new SecureRandom();

    @SneakyThrows
    @Override
    public SliderPuzzle getImageCode(HttpServletRequest request) {
        String requestId = Utils.generateSha256Id(Utils.generateAgentRequestId(request)); // 根据request标识生成Sha256Id
        int limit = Utils.getIntVal(SysConfigUtils.getConfValue("sys.captcha.requestLimit"));
        String requestCycle = SysConfigUtils.getConfValue("sys.captcha.requestCycle");
        if (Utils.getIntVal(limit) != 0) {
            redisCache.initializeCaptchaRequestLimit(requestId, Utils.getLongVal(requestCycle));
            Long cacheLimit = redisCache.countCaptchaRequestLimit(requestId);
            CommonResponseEnum.CAPTCHA_LIMIT.assertTrue(cacheLimit > Utils.getLongVal(limit));
        }

        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:/templates/background/*.png"); // 读取背景图片库
        CommonResponseEnum.BACKGROUND_NOT_EXISTS.assertTrue(resources.length == 0);
        Resource resource = resources[random.nextInt(resources.length)]; // 从背景库中随机获取一张
        SliderPuzzle sliderPuzzle = SlidePuzzleUtil.createImage(resource.getInputStream(), request); // 生成验证码
        CommonResponseEnum.FILE_NOT_EXISTS.assertNull(sliderPuzzle);

        if (limit != 0) {
            redisCache.limitCaptcha(requestId);
        }

        String expireTime = SysConfigUtils.getConfValue("sys.captcha.expire");
        assert sliderPuzzle != null;
        PointVO pointVO = new PointVO(sliderPuzzle.getPosX(), sliderPuzzle.getPosY(), sliderPuzzle.getSecretKey());

        redisCache.clearCaptcha(requestId); // 清除
        redisCache.putCaptcha(requestId, pointVO, Utils.getLongVal(expireTime)); // 保存到Redis
        sliderPuzzle.setPosX(null); // 清空验证的x坐标
        return sliderPuzzle;
    }

    @Override
    public void checkImageCode(CheckPuzzle checkPuzzle) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String requestId = checkPuzzle.getRequestId();
        CommonResponseEnum.CAPTCHA_LACK.assertNull(checkPuzzle.getMoveEncrypted());
        CommonResponseEnum.CAPTCHA_EXPIRED.assertFalse(redisCache.existCaptcha(requestId));
        PointVO pointVO = redisCache.getCaptcha(checkPuzzle.getRequestId());
        redisCache.clearCaptcha(requestId);

        // 校验滑动时长，防止机器人极速提交（最短 300ms）
        if (checkPuzzle.getStartTime() != null) {
            long elapsed = System.currentTimeMillis() - checkPuzzle.getStartTime();
            CommonResponseEnum.CAPTCHA_FAILED.message("验证过快").assertTrue(elapsed < 300);
        }

        // 解密并校验 x 轴位移，容差 ±6px
        String strX = AESUtil.aesDecrypt(checkPuzzle.getMoveEncrypted(), pointVO.getSecretKey(), checkPuzzle.getIv());
        int posX = 0;
        if (Utils.isNotNull(strX)) {
            posX = (int) Math.round(Double.parseDouble(strX));
        }
        CommonResponseEnum.CAPTCHA_FAILED.assertTrue(Math.abs(posX - pointVO.getX()) > 6);
    }

}
