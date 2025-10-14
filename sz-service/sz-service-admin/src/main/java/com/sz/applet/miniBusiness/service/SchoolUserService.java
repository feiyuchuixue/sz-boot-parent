package com.sz.applet.miniBusiness.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.sz.applet.miniBusiness.pojo.bo.SchoolUserCreateBO;
import com.sz.applet.miniBusiness.pojo.bo.SchoolUserListBO;
import com.sz.applet.miniBusiness.pojo.bo.SchoolUserUpdateBO;
import com.sz.applet.miniBusiness.pojo.po.SchoolUser;
import com.sz.applet.miniBusiness.pojo.vo.SchoolUserVO;
import com.sz.core.common.entity.SelectIdsDTO;

import java.util.List;

/**
 * <p>
 * 学校师生表 服务类
 * </p>
 *
 * @author sz
 * @since 2025-10-13
 */
public interface SchoolUserService extends IService<SchoolUser> {

    /**
     * 申请认证为学校师生
     *
     * @param bo 学校师生新增BO
     */
    void create(SchoolUserCreateBO bo);

    /**
     * 更新学校师生
     *
     * @param bo 学校师生更新BO
     */
    void update(SchoolUserUpdateBO bo);

    /**
     * 删除学校师生
     *
     * @param dto 删除参数
     */
    void remove(SelectIdsDTO dto);

    /**
     * 获取学校师生详情
     *
     * @param id 学校师生ID
     * @return 学校师生VO
     */
    SchoolUserVO detail(Long id);

    /**
     * 分页查询学校师生
     *
     * @param bo 查询参数
     * @return 学校师生分页结果
     */
    Page<SchoolUserVO> page(SchoolUserListBO bo);

    /**
     * 列表查询学校师生
     *
     * @param bo 查询参数
     * @return 学校师生列表
     */
    List<SchoolUserVO> list(SchoolUserListBO bo);
    
    /**
     * 审核学校师生认证申请
     *
     * @param id 学校师生ID
     * @param status 审核状态：1-通过，2-拒绝
     * @param remark 审核备注
     */
    void review(Long id, Integer status, String remark);
}