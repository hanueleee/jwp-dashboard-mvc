package di.stage3.context;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

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

    // 기본 생성자로 빈을 생성한다. (객체 생성)
    // 전달 받은 클래스를 객체로 생성한다.
    private Set<Object> createBeans(final Set<Class<?>> classes) throws Exception {
        Set<Object> beans = new HashSet<>();
        for (Class<?> aClass : classes) {
            Object bean = aClass.getConstructor().newInstance();
            beans.add(bean);
        }
        return beans;
    }

    // 빈 내부에 선언된 필드를 각각 셋팅한다.
    // 각 필드에 빈을 대입(assign)한다.
    // 객체의 내부 필드의 타입에 맞는 객체(bean)를 찾아서 대입(assign)한다. (관계 설정)
    private void setFields(final Object bean) {
        Field[] fields = bean.getClass().getDeclaredFields();
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

    // 빈 컨텍스트(DI)에서 관리하는 빈을 찾아서 반환한다.
    @SuppressWarnings("unchecked")
    public <T> T getBean(final Class<T> aClass) {
        Object o = beans.stream()
                .filter(it -> isAssignableType(aClass, it))
                .findFirst()
                .get();
        return (T) o;
    }
}
