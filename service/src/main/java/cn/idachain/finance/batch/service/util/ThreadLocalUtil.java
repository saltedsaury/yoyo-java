package cn.idachain.finance.batch.service.util;

import cn.idachain.finance.batch.service.external.model.ChannelUserInfo;

public final class ThreadLocalUtil {

    private static ThreadLocal<String> userNoThreadLocal = new ThreadLocal<String>();

    private static ThreadLocal<String> userNoForHttpTraceLogFilter = new ThreadLocal<String>();

    private static ThreadLocal<String> ipForHttpTraceLogFilter = new ThreadLocal<String>();

    private static ThreadLocal<String> deviceIdThreadLocal = new ThreadLocal<String>();

    private static ThreadLocal<String> sourceThreadLocal = new ThreadLocal<String>();

    private static ThreadLocal<String> cexPassportThreadLocal = new ThreadLocal<String>();

    private static ThreadLocal<String> cexTokenThreadLocal = new ThreadLocal<String>();


    private static ThreadLocal<ChannelUserInfo> channelUserInfoThreadLocal = new ThreadLocal<ChannelUserInfo>();

    public static ChannelUserInfo getChannelUserInfo() {
        return channelUserInfoThreadLocal.get();
    }

    public static void setChannelUserInfo(ChannelUserInfo channelUserInfo) {
        if (channelUserInfo != null) {
            channelUserInfoThreadLocal.set(channelUserInfo);
        }
    }

    public static void removeChannelUserInfo() {
        channelUserInfoThreadLocal.remove();
    }


    /**
     * 来源ip
     */
    private static ThreadLocal<String> requestIpThreadLocal = new ThreadLocal<String>();

    public static String getIpForHttpTraceLogFilter() {
        return ipForHttpTraceLogFilter.get();
    }

    public static void setIpForHttpTraceLogFilter(String ip) {
        if (ip != null) {
            ipForHttpTraceLogFilter.set(ip);
        }
    }

    public static void removeIpForHttpTraceLogFilter() {
        ipForHttpTraceLogFilter.remove();
    }

    public static String getUserNoForHttpTraceLogFilter() {
        return userNoForHttpTraceLogFilter.get();
    }

    public static void setUserNoForHttpTraceLogFilter(String userNo) {
        if (userNo != null) {
            userNoForHttpTraceLogFilter.set(userNo);
        }
    }

    public static void removeUserNoForHttpTraceLogFilter() {
        userNoForHttpTraceLogFilter.remove();
    }

    public static String getUserNo() {
        return userNoThreadLocal.get();
    }

    public static void setUserNo(String userNo) {
        if (userNo != null) {
            userNoThreadLocal.set(userNo);
        }
    }

    public static void removeUserNo() {
        userNoThreadLocal.remove();
    }

    public static String getDeviceId() {
        return deviceIdThreadLocal.get();
    }

    public static void setDeviceId(String deviceId) {
        if (deviceId != null) {
            deviceIdThreadLocal.set(deviceId);
        }
    }

    public static void removeDeviceId() {
        deviceIdThreadLocal.remove();
    }

    public static String getSource() {
        return sourceThreadLocal.get();
    }

    public static void setSource(String source) {
        if (source != null) {
            sourceThreadLocal.set(source);
        }
    }

    public static void removeSource() {
        sourceThreadLocal.remove();
    }

    public static String getRequestIp() {
        return requestIpThreadLocal.get();
    }

    public static void setRequestIp(String requestIP) {
        if (requestIP != null) {
            requestIpThreadLocal.set(requestIP);
        }
    }

    public static void removeRequestIp() {
        requestIpThreadLocal.remove();
    }

    public static String getCexPassport() {
        return cexPassportThreadLocal.get();
    }

    public static void setCexPassport(String cexPassport) {
        if (cexPassport != null) {
            cexPassportThreadLocal.set(cexPassport);
        }
    }

    public static void removeCexPassport(){
        cexPassportThreadLocal.remove();
    }

    public static String getCexToken() {
        return cexTokenThreadLocal.get();
    }

    public static void setCexToken(String cexToken) {
        if (cexToken != null) {
            cexTokenThreadLocal.set(cexToken);
        }
    }

    public static void removeCexToken(){
        cexTokenThreadLocal.remove();
    }

}
