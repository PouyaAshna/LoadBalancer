package org.example.loadbalancer.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClassLoaderUtil {

    private ClassLoaderUtil() {
    }

    public static Class<?>[] getClasses(String packageName) throws IOException, ClassNotFoundException {
        var contextClassLoader = Thread.currentThread().getContextClassLoader();
        assert contextClassLoader != null;
        var path = packageName.replace('.', '/');
        var resources = contextClassLoader.getResources(path);
        var dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            var resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        var classes = new ArrayList<Class<?>>();
        for (File dir : dirs) {
            classes.addAll(findClasses(dir, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    public static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        var classes = new ArrayList<Class<?>>();
        if (!directory.exists()) {
            return classes;
        }
        var files = directory.listFiles();
        if (Objects.nonNull(files)) {
            for (File file : files) {
                if (file.isDirectory()) {
                    assert !file.getName().contains(".");
                    classes.addAll(findClasses(file, packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    var className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                    classes.add(Class.forName(className));
                }
            }
        }
        return classes;
    }
}
