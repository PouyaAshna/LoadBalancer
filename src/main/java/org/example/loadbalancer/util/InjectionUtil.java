package org.example.loadbalancer.util;

import org.example.loadbalancer.util.annotation.Autowired;
import org.example.loadbalancer.util.annotation.Qualifier;
import org.example.loadbalancer.util.injector.DependencyInjector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class InjectionUtil {

    private InjectionUtil() {
    }

    public static void autowire(DependencyInjector injector, Class<?> classz, Object instance) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        var fields = findFields(classz);
        for (Field field : fields) {
            var qualifierName = field.isAnnotationPresent(Qualifier.class) ? field.getAnnotation(Qualifier.class).value() : null;
            Type type = field.getGenericType();
            if (type instanceof ParameterizedType && field.getType().isAssignableFrom(Map.class)) {
                ParameterizedType pType = (ParameterizedType) type;
                var fieldInstance = injector.getBeanInstances((Class<?>) pType.getActualTypeArguments()[1]);
                field.set(instance, fieldInstance);
                autowire(injector, fieldInstance.getClass(), fieldInstance);
            } else {
                var fieldInstance = injector.getBeanInstance(field.getType(), field.getName(), qualifierName);
                field.set(instance, fieldInstance);
                autowire(injector, fieldInstance.getClass(), fieldInstance);
            }
        }
    }

    private static Set<Field> findFields(Class<?> classz) {
        var fields = new HashSet<Field>();
        while (Objects.nonNull(classz)) {
            for (Field declaredField : classz.getDeclaredFields()) {
                if (declaredField.isAnnotationPresent(Autowired.class)) {
                    declaredField.setAccessible(true);
                    fields.add(declaredField);
                }
            }
            classz = classz.getSuperclass();
        }
        return fields;
    }
}
