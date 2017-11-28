package io.electra.server.test;

import com.google.common.collect.Lists;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Philip 'JackWhite20' <silencephil@gmail.com>
 */
public class ElectraTest {

    private List<Method> methods = Lists.newArrayList();

    public ElectraTest() {
        findTests();
    }

    private void findTests() {
        for (Method method : getClass().getMethods()) {
            if (method.isAnnotationPresent(Order.class)) {
                methods.add(method);
            }
        }

        methods.sort((m1, m2) -> {
            Order o1 = m1.getAnnotation(Order.class);
            Order o2 = m2.getAnnotation(Order.class);

            if (o1 == null || o2 == null) {
                return -1;
            }

            return o1.value() - o2.value();
        });
    }

    protected void execute() {
        for (Method method : methods) {
            try {
                method.invoke(this);
                System.out.println("Test " + method.getName() + " passed");
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
