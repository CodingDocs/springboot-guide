package com.github.springbootdatadesensitization.desensitize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.github.springbootdatadesensitization.desensitize.annotation.JsonDesensitization;
import com.github.springbootdatadesensitization.desensitize.desensitizer.Desensitizer;
import com.github.springbootdatadesensitization.desensitize.desensitizer.DesensitizerFactory;
import com.github.springbootdatadesensitization.desensitize.enums.DesensitizationType;

import java.io.IOException;
import java.util.Objects;

/**
 * 微信搜 JavaGuide 回复"面试突击"即可免费领取个人原创的 Java 面试手册
 *
 * @author Guide哥
 * @date 2021/05/10 20:26
 **/
public class JsonDesensitizationSerializer extends JsonSerializer<String> implements ContextualSerializer {


    private DesensitizationType type;

    public JsonDesensitizationSerializer() {
    }

    public JsonDesensitizationSerializer(DesensitizationType type) {
        this.type = type;
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty property) throws JsonMappingException {
        if (property != null) {
            if (Objects.equals(property.getType().getRawClass(), String.class)) {
                JsonDesensitization jsonDesensitization = property.getAnnotation(JsonDesensitization.class);
                if (jsonDesensitization == null) {
                    jsonDesensitization = property.getContextAnnotation(JsonDesensitization.class);
                }
                if (jsonDesensitization != null) {
                    return new JsonDesensitizationSerializer(jsonDesensitization.value());
                }
            }
            return serializerProvider.findValueSerializer(property.getType(), property);
        } else {
            return serializerProvider.findNullValueSerializer(null);
        }
    }

    @Override
    public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        Desensitizer desensitizer = DesensitizerFactory.get(this.type);
        System.out.println(s);
        jsonGenerator.writeString(desensitizer.desensitize(s));
    }
}
