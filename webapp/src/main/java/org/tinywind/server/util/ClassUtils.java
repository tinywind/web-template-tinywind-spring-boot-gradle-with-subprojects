package org.tinywind.server.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author tinywind
 * @since 2017-05-14
 */
public class ClassUtils {

    @SuppressWarnings("unchecked")
    public static <E> List<Class<E>> getClasses(String packageName, Class<E> type) throws IOException, ClassNotFoundException {
        return getClasses(packageName).stream()
                .filter(klass -> klass.equals(type)
                        || type.isAnnotation()
                        || Arrays.asList(klass.getClasses()).contains(type)
                        || Arrays.asList(klass.getInterfaces()).contains(type)
                        || isExpands(klass, type))
                .map(e -> (Class<E>) e)
                .distinct()
                .collect(Collectors.toList());
    }

    public static boolean isExpands(Object child, Class<?> parent) {
        return isExpands(child.getClass(), parent);
    }

    public static boolean isExpands(Class<?> child, Class<?> parent) {
        if (child == null || parent == null)
            return false;

        if (child.isInterface() && !parent.isInterface())
            return false;

        if (parent.isInterface() && ofInterface(child, parent))
            return true;

        if (child.equals(parent))
            return true;

        return isExpands(child.getSuperclass(), parent);
    }

    private static boolean ofInterface(Class<?> child, Class<?> parent) {
        if (child == null || parent == null)
            return false;

        if (Objects.equals(child, parent))
            return true;

        final List<Class<?>> childInterfaces = Arrays.asList(child.getInterfaces());

        if (childInterfaces.contains(parent))
            return true;

        if (ofInterface(child.getSuperclass(), parent))
            return true;

        for (Class<?> childInterface : childInterfaces) {
            if (ofInterface(childInterface.getSuperclass(), parent))
                return true;

            for (Class<?> anInterface : childInterface.getInterfaces())
                if (ofInterface(anInterface, parent))
                    return true;
        }

        return false;
    }

    public static List<Class<?>> getClasses() throws ClassNotFoundException, IOException {
        return getClasses("");
    }

    /**
     * from: http://stackoverflow.com/questions/520328/can-you-find-all-classes-in-a-package-using-reflection<br/>
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     */
    public static List<Class<?>> getClasses(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        dirs = dirs.stream().distinct().collect(Collectors.toList());
        ArrayList<Class<?>> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }

    /**
     * from: http://stackoverflow.com/questions/520328/can-you-find-all-classes-in-a-package-using-reflection<br/>
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     */
    private static List<Class<?>> findClasses(File directory, String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();
        if (files == null)
            return classes;

        for (File file : files) {
            final String packageAlias = packageName.length() > 0 ? packageName + '.' : "";
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageAlias + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                try {
                    classes.add(Class.forName(packageAlias + file.getName().substring(0, file.getName().length() - 6)));
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println(packageName);
                    System.err.println(packageAlias + file.getName().substring(0, file.getName().length() - 6));
                }
            }
        }
        return classes;
    }

}
