package com.nhnacademy.fbp.infrastructure.http;

import com.nhnacademy.fbp.infrastructure.http.annotation.PathVariable;
import com.nhnacademy.fbp.infrastructure.http.annotation.RequestBody;
import com.nhnacademy.fbp.infrastructure.http.annotation.RequestMapping;
import com.nhnacademy.fbp.infrastructure.http.annotation.RequestMethod;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Route(
        Pattern pattern,
        RequestMethod httpMethod,
        Object controller,
        Method actionMethod
) {
    public static Route of(RequestMapping mapping, Object controller, Method method) {
        String regex = mapping.value().replaceAll("\\{([^/]+)\\}", "(?<$1>[^/]+)");
        Pattern pattern = Pattern.compile("^" + regex + "$");
        return new Route(pattern, mapping.method(), controller, method);
    }

    public Optional<Route> execute(String requestURI, String method) {
        RequestMethod requestMethod = RequestMethod.resolve(method);

        if (httpMethod != requestMethod) {
            return Optional.empty();
        }

        Matcher matcher = pattern.matcher(requestURI);

        if (matcher.matches()) {
            return Optional.of(this);
        }

        return Optional.empty();
    }

    public Object invoke(HttpExchange httpExchange) throws InvocationTargetException, IllegalAccessException, IOException {
        String requestURI = httpExchange.getRequestURI().getPath();
        Matcher matcher = pattern.matcher(requestURI);

        Parameter[] declaredParameters = actionMethod.getParameters();
        Object[] parameters = new Object[actionMethod.getParameterCount()];

        if (matcher.matches()) {
            for (int i = 0; i < parameters.length; i++) {
                if (declaredParameters[i].isAnnotationPresent(PathVariable.class)) {
                    String value = matcher.group(declaredParameters[i].getName());

                    parameters[i] = value;
                }

                if (declaredParameters[i].isAnnotationPresent(RequestBody.class)) {
                    byte[] requestBody = httpExchange.getRequestBody().readAllBytes();
                    String value = new String(requestBody, StandardCharsets.UTF_8);

                    parameters[i] = value;
                }
            }
        }

            return actionMethod.invoke(controller, parameters);
    }
}
