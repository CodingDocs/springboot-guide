package com.github.springbootdatadesensitization.desensitize.enums;

/**
 * 微信搜 JavaGuide 回复"面试突击"即可免费领取个人原创的 Java 面试手册
 *
 * @author Guide哥
 * @date 2021/05/10 20:36
 **/
public enum DesensitizationType {

    DEFAULT,
    /**
     * 座机号
     */
    LANDLINE,
    /**
     * 手机
     */
    MOBILE,
    /**
     * 邮箱
     */
    EMAIL,

    /**
     * 生日🎂
     */
    BIRTHDAY,
    /**
     * 密码
     */
    PASSWORD,
    /**
     * 身份证
     */
    ID_CARD,
    /**
     * 银行卡
     */
    BANK_CARD,
    /**
     * 地址
     */
    ADDRESS,

}
