package com.sz.admin.system.controller;

import com.sz.admin.system.service.CommonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;

/**
 * 通用controller
 *
 * @ClassName CommonController
 * @Author sz
 * @Date 2023/12/25 10:07
 * @Version 1.0
 */
@Tag(name = "通用API")
@RestController
@RequestMapping("/common")
@RequiredArgsConstructor
@Slf4j
public class CommonController {

    private final CommonService commonService;

    private final RedisCache redisCache;

    @Operation(summary = "模板下载")
    @GetMapping("/download/templates")
    public void fileDownload(@RequestParam("templateName") String templateName, HttpServletResponse response) {
        try {
            commonService.tempDownload(templateName, response);
        } catch (Exception e) {
            log.error("模板下载文件下载失败", e);
        }
    }

    @Debounce(time = 1000)
    @Operation(summary = "获取验证码")
    @GetMapping(value = "/getImageCode")
    public ApiResult<SliderPuzzle> getImageCode(HttpServletRequest request) throws Exception {
        // 作为滑块验证的图片文件地址
        // todo 需要考虑这部分的图片怎么做
        File file = new File("C:/Users/Administrator/Pictures/test1.png");
        SliderPuzzle sliderPuzzle = SlidePuzzleUtil.createImage(new FileInputStream(file));
        if (sliderPuzzle == null) {
            return ApiResult.error(CommonResponseEnum.FILE_NOT_EXISTS);
        }
        //将图片保存到本地
        //File file1 = new File("D:/c/demo2BigImage.png");
        //File file2 = new File("D:/c/demo2SmallImage.png");
        //ImageIO.write(sliderPuzzleInfo.getBigImage(), "png", file1);
        //ImageIO.write(sliderPuzzleInfo.getSmallImage(), "png", file2);
        // 保存到Redis
        redisCache.putVerify(sliderPuzzle.getLongTime(), sliderPuzzle.getPosX());
        // 返回给前端
        sliderPuzzle.setBigImage(null);
        sliderPuzzle.setSmallImage(null);
        return ApiResult.success(sliderPuzzle);
    }


    @Debounce(time = 1000)
    @Operation(summary = "校验滑块拼图验证码")
    @GetMapping(value = "/verifyImageCode")
    public ApiResult verifyImageCode(@RequestParam(value = "movePosX") Integer movePosX,
                                     @RequestParam(value = "longTime") Long longTime) {
        try {
            String confValue = redisCache.getVerify(longTime);
            if (movePosX == null) {
                return ApiResult.error(CommonResponseEnum.VERIFY_LACK);
            }
            if (confValue == null || confValue.isEmpty()) {
                return ApiResult.error(CommonResponseEnum.VERIFY_EXPIRED);
            }
            Integer posX = Integer.valueOf(confValue);
            //计算偏移量误差
            if (Math.abs(posX - movePosX) > 3) {
                return ApiResult.error(CommonResponseEnum.VERIFY_ERROR);
            } else {
                return ApiResult.success();
            }
        } catch (Exception e) {
            return ApiResult.error(CommonResponseEnum.VERIFY_ERROR);
        }
        //finally {
        //    //校验不删除验证码
        //    redisCache.clearVerify(longTime);
        //}
    }

}
