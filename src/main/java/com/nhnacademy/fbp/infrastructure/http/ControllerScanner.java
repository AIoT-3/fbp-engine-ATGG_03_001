package com.nhnacademy.fbp.infrastructure.http;

import com.nhnacademy.fbp.infrastructure.http.annotation.RequestMapping;
import com.nhnacademy.fbp.infrastructure.http.annotation.RestController;
import com.nhnacademy.fbp.core.engine.FlowEngine;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
public class ControllerScanner {
    private final Map<Class<?>, Object> beans;

    public ControllerScanner(FlowEngine engine) {
        this.beans = new HashMap<>();

        beans.put(engine.getClass(), engine);
    }

    public List<Route> scan() {
        try {
            Reflections reflections = new Reflections("com.nhnacademy.fbp.api.controller");

            Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(RestController.class);

            List<Route> routes = new ArrayList<>();

            for (Class<?> controller : controllers) {
                Constructor<?> constructor = Arrays.stream(controller.getDeclaredConstructors())
                        .findFirst()
                        .orElseThrow(RuntimeException::new);

                Object[] parameters = Arrays.stream(constructor.getParameterTypes())
                        .map(beans::get)
                        .toArray();

                Object controllerInstance = constructor.newInstance(parameters);

                for (Method method : controller.getMethods()) {
                    RequestMapping mapping = method.getAnnotation(RequestMapping.class);

                    if (mapping != null) {
                        Route route = Route.of(mapping, controllerInstance, method);
                        routes.add(route);
                        log.info("등록된 라우트: {}", route);
                    }
                }
            }

            return routes;
        } catch (Exception e) {
            log.error("컨트롤러 스캔 실패: {}", e.getMessage(), e);
            throw   new RuntimeException(e);
        }
    }
}
