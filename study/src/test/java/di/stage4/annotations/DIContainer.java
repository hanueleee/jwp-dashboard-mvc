package di.stage4.annotations;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 스프링의 BeanFactory, ApplicationContext에 해당되는 클래스
 */
class DIContainer {

    private final Set<Object> beans;

    public DIContainer(final Set<Class<?>> classes) throws Exception {
        this.beans = createBeans(classes);
        for (Object bean : this.beans) {
            setFields(bean);
        }
    }

    public static DIContainer createContainerForPackage(final String rootPackageName) throws Exception {
        Set<Class<?>> classes = ClassPathScanner.getAllClassesInPackage(rootPackageName);
        return new DIContainer(classes);
    }

    private Set<Object> createBeans(final Set<Class<?>> classes) throws Exception {
        Set<Object> beans = new HashSet<>();
        for (Class<?> aClass : classes) {
            Constructor<?> constructor = aClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            Object bean = constructor.newInstance();
            beans.add(bean);
        }
        return beans;
    }

    private void setFields(final Object bean) {
        List<Field> fields = Arrays.stream(bean.getClass().getDeclaredFields())
                .filter(it -> it.isAnnotationPresent(Inject.class))
                .collect(Collectors.toList());
        for (Field field : fields) {
            Class<?> fieldType = field.getType();
            beans.stream()
                    .filter(it -> isAssignableType(fieldType, it))
                    .findFirst()
                    .ifPresent(it -> assignField(bean, field, it));
        }
    }

    private boolean isAssignableType(final Class<?> type, final Object it) {
        Class<?> aClass = it.getClass();
        return type.isAssignableFrom(aClass);
    }

    private void assignField(final Object bean, final Field field, final Object it) {
        field.setAccessible(true);
        try {
            field.set(bean, it);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(final Class<T> aClass) {
        Object o = beans.stream()
                .filter(it -> isAssignableType(aClass, it))
                .findFirst()
                .get();
        return (T) o;
    }
}
