package com.github.springbootdatadesensitization.desensitize.desensitizer;

import com.github.springbootdatadesensitization.desensitize.exception.DesensitizationException;

import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 微信搜 JavaGuide 回复"面试突击"即可免费领取个人原创的 Java 面试手册
 * 数据脱敏所使用的的策略
 *
 * @author Guide哥
 * @date 2021/05/10 20:15
 **/
public abstract class AbstractDesensitizer implements Desensitizer {

    /**
     * 左边的明文长度
     */
    private final Integer leftPlainTextLen;
    /**
     * 右边的明文长度
     */
    private Integer rightPlainTextLen;

    public AbstractDesensitizer(Integer leftPlainTextLen) {
        this.leftPlainTextLen = leftPlainTextLen;
    }

    public AbstractDesensitizer(Integer leftPlainTextLen, Integer rightPlainTextLen) {
        this.leftPlainTextLen = leftPlainTextLen;
        this.rightPlainTextLen = rightPlainTextLen;
    }

    @Override
    public String desensitize(String origin) {
        if (origin == null || origin.isEmpty()) {
            return "";
        }
        // 处理邮箱的特殊情况
        if (isEmail(origin)) {
            int index = origin.indexOf("@");
            rightPlainTextLen = origin.length() - index;
        }
        if (leftPlainTextLen == 0 && rightPlainTextLen == 0) {
            return getEncryptedStr(6);
        }
        if (leftPlainTextLen < 0 || rightPlainTextLen < 0) {
            throw new DesensitizationException("leftPlainTextLen and rightPlainTextLen must > 0");
        }
        if (leftPlainTextLen + rightPlainTextLen >= origin.length()) {
            throw new DesensitizationException("leftPlainTextLen+rightPlainTextLen should <= the length of origin");
        }
        StringBuilder result = new StringBuilder();
        result.append(origin, 0, leftPlainTextLen);
        result.append(getEncryptedStr(origin.length() - leftPlainTextLen - rightPlainTextLen));
        result.append(origin, origin.length() - rightPlainTextLen, origin.length());
        return result.toString();
    }

    private Boolean isEmail(String s) {
        String emailRegex = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern emailPattern = Pattern.compile(emailRegex);
        return emailPattern.matcher(s).matches();
    }

    private String getEncryptedStr(int length) {
        return Stream.generate(() -> "*").limit(length).collect(Collectors.joining());
    }
}
