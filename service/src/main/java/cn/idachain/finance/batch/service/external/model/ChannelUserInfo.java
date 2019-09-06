package cn.idachain.finance.batch.service.external.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 渠道查询用户信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChannelUserInfo {
    /**
     * 用户号
     */
    private String userNo;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机区号
     */
    private String mobileArea;
    /**
     * 手机号
     */
    private String mobile;

    /**
     * 实名信息，认证通过才提供，否则为空
     */
    private String name;


    /**
     * 用户状态
     * NORMAL(0,"正常"),
     * FORBIDDEN(1,"封禁");
     */
    private Integer memberStatus;

    /**
     * 注册时间
     */
    private Date registerTime;

}
