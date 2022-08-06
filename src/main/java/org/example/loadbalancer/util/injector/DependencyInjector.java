package org.example.loadbalancer.util.injector;

import org.example.loadbalancer.util.ClassLoaderUtil;
import org.example.loadbalancer.util.InjectionUtil;
import org.example.loadbalancer.util.annotation.Component;
import org.reflections.Reflections;

import javax.management.RuntimeErrorException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DependencyInjector {

    private final Map<Class<?>, Class<?>> diMap;
    private final Map<Class<?>, Object> applicationScope;

    private static DependencyInjector injector;

    private DependencyInjector() {
        diMap = new HashMap<>();
        applicationScope = new HashMap<>();
    }

    public static void startApplication(Class<?> mainClass) {
        try {
            if (Objects.isNull(injector)) {
                injector = new DependencyInjector();
                injector.initFramework(mainClass);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getService(Class<T> classz) {
        try {
            return (T) injector.getBeanInstance(classz, null, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void initFramework(Class<?> mainClass) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        var classes = ClassLoaderUtil.getClasses(mainClass.getPackageName());
        lookupImplementationClasses(mainClass);
        for (Class<?> classz : classes) {
            if (classz.isAnnotationPresent(Component.class)) {
                var instance = classz.getDeclaredConstructor().newInstance();
                applicationScope.put(classz, instance);
                InjectionUtil.autowire(this, classz, instance);
            }
        }
    }

    private void lookupImplementationClasses(Class<?> mainClass) {
        var reflection = new Reflections(mainClass.getPackage().getName());
        var types = reflection.getTypesAnnotatedWith(Component.class);
        for (Class<?> implementationClass : types) {
            var interfaces = Stream.of(implementationClass.getInterfaces(), implementationClass.getSuperclass().getInterfaces())
                    .flatMap(Arrays::stream).collect(Collectors.toList());
            if (interfaces.isEmpty()) {
                diMap.put(implementationClass, implementationClass);
            } else {
                for (Class<?> anInterface : interfaces) {
                    diMap.put(implementationClass, anInterface);
                }
            }
        }
    }

    public <T> Object getBeanInstance(Class<T> interfaceClass, String fieldName, String qualifier) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        var implementationClass = getImplementationClass(interfaceClass, fieldName, qualifier);
        return getApplicationObject(implementationClass);
    }

    private Object getApplicationObject(Class<?> implementationClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (applicationScope.containsKey(implementationClass)) {
            return applicationScope.get(implementationClass);
        }
        synchronized (applicationScope) {
            var instance = implementationClass.getDeclaredConstructor().newInstance();
            applicationScope.put(implementationClass, instance);
            return instance;
        }
    }

    public <T> Map<String, Object> getBeanInstances(Class<T> interfaceClass) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        var implementationClasses = getImplementationClasses(interfaceClass);
        Map<String, Object> applicationObjects = new HashMap<>();
        for (Map.Entry<String, Class<?>> implementationClassEntry : implementationClasses.entrySet()) {
            var applicationObject = this.getApplicationObject(implementationClassEntry.getValue());
            applicationObjects.put(implementationClassEntry.getKey(), applicationObject);
        }
        return applicationObjects;
    }

    private Class<?> getImplementationClass(Class<?> interfaceClass, final String fieldName, final String qualifier) {
        var implementationClasses = diMap.entrySet().stream()
                .filter(entry -> entry.getValue() == interfaceClass)
                .collect(Collectors.toSet());
        var errorMessage = "";
        if (Objects.isNull(implementationClasses) || implementationClasses.isEmpty()) {
            errorMessage = "there is not implementation class for interface " + interfaceClass.getName();
        } else if (implementationClasses.size() == 1) {
            var implementationClass = implementationClasses.stream().findFirst();
            if (implementationClass.isPresent()) {
                return implementationClass.get().getKey();
            }
        } else if (implementationClasses.size() > 1) {
            var findBy = (Objects.isNull(qualifier) || qualifier.trim().isEmpty()) ? fieldName : qualifier;
            var implementationClass = implementationClasses.stream()
                    .filter(entry -> entry.getKey().getSimpleName().equalsIgnoreCase(findBy))
                    .findAny();
            if (implementationClass.isPresent()) {
                return implementationClass.get().getKey();
            } else {
                errorMessage = "There are " + implementationClasses.size() + " of interface " + interfaceClass.getName()
                        + " Expected single implementation or make use of @Qualifier to resolve conflict";
            }
        }
        throw new RuntimeErrorException(new Error(errorMessage));
    }

    private Map<String, Class<?>> getImplementationClasses(Class<?> interfaceClass) {
        return diMap.entrySet().stream()
                .filter(entry -> entry.getValue() == interfaceClass)
                .collect(Collectors.toMap(classClassEntry -> {
                    var componentValue = classClassEntry.getKey().getAnnotation(Component.class).value();
                    return Objects.isNull(componentValue) || componentValue.isEmpty() ? classClassEntry.getKey().getSimpleName() : componentValue;
                }, Map.Entry::getKey));
    }

}
