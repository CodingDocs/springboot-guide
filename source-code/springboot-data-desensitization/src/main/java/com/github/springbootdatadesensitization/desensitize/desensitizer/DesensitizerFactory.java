package com.github.springbootdatadesensitization.desensitize.desensitizer;

import com.github.springbootdatadesensitization.desensitize.desensitizer.impl.AddressDesensitizer;
import com.github.springbootdatadesensitization.desensitize.desensitizer.impl.BankCardDesensitizer;
import com.github.springbootdatadesensitization.desensitize.desensitizer.impl.BirthdayDesensitizer;
import com.github.springbootdatadesensitization.desensitize.desensitizer.impl.DefaultDesensitizer;
import com.github.springbootdatadesensitization.desensitize.desensitizer.impl.EmailDesensitizer;
import com.github.springbootdatadesensitization.desensitize.desensitizer.impl.IdCardDesensitizer;
import com.github.springbootdatadesensitization.desensitize.desensitizer.impl.LandlineDesensitizer;
import com.github.springbootdatadesensitization.desensitize.desensitizer.impl.MobileDesensitizer;
import com.github.springbootdatadesensitization.desensitize.desensitizer.impl.PasswordDesensitizer;
import com.github.springbootdatadesensitization.desensitize.enums.DesensitizationType;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信搜 JavaGuide 回复"面试突击"即可免费领取个人原创的 Java 面试手册
 *
 * @author Guide哥
 * @date 2021/05/10 23:18
 **/
public class DesensitizerFactory {
    public static final Map<DesensitizationType, Desensitizer> desensitizers = new HashMap<>();

    static {
        desensitizers.put(DesensitizationType.ADDRESS, new AddressDesensitizer());
        desensitizers.put(DesensitizationType.BANK_CARD, new BankCardDesensitizer());
        desensitizers.put(DesensitizationType.EMAIL, new EmailDesensitizer());
        desensitizers.put(DesensitizationType.ID_CARD, new IdCardDesensitizer());
        desensitizers.put(DesensitizationType.LANDLINE, new LandlineDesensitizer());
        desensitizers.put(DesensitizationType.MOBILE, new MobileDesensitizer());
        desensitizers.put(DesensitizationType.PASSWORD, new PasswordDesensitizer());
        desensitizers.put(DesensitizationType.DEFAULT, new DefaultDesensitizer());
        desensitizers.put(DesensitizationType.BIRTHDAY, new BirthdayDesensitizer());
    }

    public static Desensitizer get(DesensitizationType desensitizationType) {
        Desensitizer desensitizer = desensitizers.get(desensitizationType);
        if (desensitizer == null) {
            return desensitizers.get(DesensitizationType.DEFAULT);
        }
        return desensitizer;
    }
}
