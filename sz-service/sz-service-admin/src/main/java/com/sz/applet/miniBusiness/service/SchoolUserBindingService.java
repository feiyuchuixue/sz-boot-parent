package com.sz.applet.miniBusiness.service;

import com.mybatisflex.core.service.IService;
import com.sz.applet.miniBusiness.pojo.bo.SchoolUserBindingCreateBO;
import com.sz.applet.miniBusiness.pojo.bo.SchoolUserBindingListBO;
import com.sz.applet.miniBusiness.pojo.bo.SchoolUserBindingUpdateBO;
import com.sz.applet.miniBusiness.pojo.po.SchoolUserBinding;
import com.sz.applet.miniBusiness.pojo.vo.SchoolUserBindingVO;
import com.sz.core.common.entity.SelectIdsDTO;

import java.util.List;

/**
 * <p>
 * 学校用户绑定小程序用户 服务类
 * </p>
 *
 * @author sz
 * @since 2025-10-13
 */
public interface SchoolUserBindingService extends IService<SchoolUserBinding> {

    /**
     * 申请绑定学校用户
     *
     * @param bo 绑定申请BO
     */
    void create(SchoolUserBindingCreateBO bo);

    /**
     * 更新绑定申请
     *
     * @param bo 绑定更新BO
     */
    void update(SchoolUserBindingUpdateBO bo);

    /**
     * 删除绑定申请
     *
     * @param dto 删除参数
     */
    void remove(SelectIdsDTO dto);

    /**
     * 获取绑定详情
     *
     * @param id 绑定ID
     * @return 绑定VO
     */
    SchoolUserBindingVO detail(Long id);

    /**
     * 分页查询绑定申请
     *
     * @param bo 查询参数
     * @return 绑定分页结果
     */
    Object page(SchoolUserBindingListBO bo);

    /**
     * 列表查询绑定申请
     *
     * @param bo 查询参数
     * @return 绑定列表
     */
    List<SchoolUserBindingVO> list(SchoolUserBindingListBO bo);
    
    /**
     * 审核绑定申请
     *
     * @param id 绑定ID
     * @param status 审核状态：1-通过，2-拒绝
     * @param remark 审核备注
     */
    void review(Long id, Integer status, String remark);
    
    /**
     * 检查小程序用户是否已绑定到指定学校用户
     * 
     * @param miniUserId 小程序用户ID
     * @param schoolUserId 学校用户ID
     * @return true-已绑定，false-未绑定
     */
    boolean isBound(Long miniUserId, Long schoolUserId);
}