package cn.idachain.finance.batch.service.external.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * cex接口用户信息
 * Created by liuhailin on 2019/1/30.
 */
@NoArgsConstructor
@Getter
@Setter
public class UserInfo {

    // USER 用户；MERCHANT 商家
    private List<String> roleList;

    /**
     * 用户号
     */
    private String userNo;
    private String firstName;
    private String lastName;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 认证类型：身份证(0),护照(1)
     */
    private Integer certificateType;
    /**
     * 认证状态：未认证(0),处理中(1),""成功(2),失败(3)
     */
    private Integer certificateAuthStatus;
    /**
     * 手机区号
     */
    private String mobileArea;
    /**
     * 手机号
     */
    private String mobile;

    /**
     * 是否设置了安全密码
     */
    private boolean isSecurityPwdSet;
    /**
     * 邀请码
     */
    private String invitationCode;

    /**
     * google验证绑定,0表示未绑定，1表示绑定
     */
    private String googleStatus;

    /**
     * 反钓鱼码
     */
    private String antiPhishing;

    /**
     * 用户状态
     * NORMAL(0,"正常"),
     * FORBIDDEN(1,"封禁");
     */
    private Integer memberStatus;

    /**
     * 应该cexToken是敏感信息，在CexAuthenticationToken中如果toString打印会把cexToken打印出来，所以自己手动toString打印
     * UserInfo
     *
     * @return
     */
    @Override
    public String toString() {
        return "UserInfo{" +
                "roleList=" + roleList +
                ", userNo='" + userNo + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", certificateType=" + certificateType +
                ", certificateAuthStatus=" + certificateAuthStatus +
                ", mobileArea='" + mobileArea + '\'' +
                ", mobile='" + mobile + '\'' +
                ", isSecurityPwdSet=" + isSecurityPwdSet +
                ", invitationCode='" + invitationCode + '\'' +
                ", googleStatus='" + googleStatus + '\'' +
                ", antiPhishing='" + antiPhishing + '\'' +
                ", memberStatus=" + memberStatus +
                '}';
    }
}
