package com.dobbinsoft.netting.im.web;

import com.dobbinsoft.netting.base.utils.JsonUtils;
import com.dobbinsoft.netting.im.application.service.http.BaseHttpService;
import com.dobbinsoft.netting.im.exception.ImErrorCode;
import com.dobbinsoft.netting.im.exception.ImException;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Singleton
public class HttpServiceRouter {

    public final static Map<String, BaseHttpService> serviceMap = new ConcurrentHashMap<>();

    private final static Map<String, Method> cacheMap = new HashMap<>();

    public static void register(BaseHttpService baseHttpService) {
        String group = baseHttpService.group();
        serviceMap.put(group, baseHttpService);
        log.info("[Http Api] Register group: {}", group);
        Method[] methods = baseHttpService.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getParameters().length == 1) {
                String cacheKey = group + "-" + method.getName();
                cacheMap.put(cacheKey, method);
                break;
            }
        }
    }

    public Object call(String group, String method, String body) {
        try {
            BaseHttpService instance = serviceMap.get(group);
            if (instance == null) {
                throw new ImException(ImErrorCode.API_NOT_EXIST);
            }
            String cacheKey = group + "-" + method;
            Method methodInvoke = cacheMap.get(cacheKey);
            if (methodInvoke != null) {
                Parameter[] parameters = methodInvoke.getParameters();
                Class<?> clazz = parameters[0].getType();
                if (clazz != String.class) {
                    Object parse = JsonUtils.parse(body, clazz);
                    return methodInvoke.invoke(instance, parse);
                } else {
                    return methodInvoke.invoke(instance, body);
                }
            }
            throw new ImException(ImErrorCode.API_NOT_EXIST);
        } catch (ImException e) {
            throw e;
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            if (targetException instanceof ImException) {
                throw (ImException)targetException;
            }
            log.error("[IM业务处理] 异常", e);
            throw new ImException(ImErrorCode.SERVICE_ERROR);
        } catch (IllegalAccessException e) {
            log.error("[IM Router] 外部尝试访问非公开方法: {} - {}", group, method);
            throw new ImException(ImErrorCode.API_NOT_EXIST);
        }
    }

}
