package com.sz.platform.constant.dict;

/**
 * 账户状态字典常量 对应 sys_dict_type.type_code = "account_status"
 */
public final class AccountStatusConstant {

    private AccountStatusConstant() {
    }

    /** 正常 */
    public static final String NORMAL = "1000001";

    /** 禁用 */
    public static final String DISABLED = "1000002";

    /** 禁言 */
    public static final String MUTED = "1000003";
}
