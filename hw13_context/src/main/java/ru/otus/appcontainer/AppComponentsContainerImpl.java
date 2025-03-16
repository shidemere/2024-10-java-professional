package ru.otus.appcontainer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;

@SuppressWarnings("squid:S1068")
public class AppComponentsContainerImpl implements AppComponentsContainer {
    private static final Logger logger = LoggerFactory.getLogger(AppComponentsContainerImpl.class);

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        processConfig(initialConfigClass);
    }

    private void processConfig(Class<?> configClass) {
        checkConfigClass(configClass);

        List<Method> methods = Arrays.stream(configClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(AppComponent.class))
                .sorted(Comparator.comparing(
                        method -> method.getAnnotation(AppComponent.class).order()))
                .toList();

        for (Method method : methods) {
            var appComponentName = method.getAnnotation(AppComponent.class).name();
            if (appComponentsByName.containsKey(appComponentName)) {
                logger.error("Duplicate app component name: {}", appComponentName);
                throw new RuntimeException("Duplicate app component name: " + appComponentName);
            }

            Constructor<?>[] declaredConstructors = configClass.getDeclaredConstructors();
            if (declaredConstructors.length > 1) {
                logger.error("More than one constructor: {}", declaredConstructors.length);
                throw new RuntimeException("Too many constructors: " + declaredConstructors.length);
            }
            try {
                var appObject = configClass.getConstructor().newInstance();
                Parameter[] parameters = method.getParameters();
                Object appComponent;
                if (parameters.length == 0) {
                    appComponent = method.invoke(appObject);
                } else {
                    var methodParamObjects = Arrays.stream(parameters)
                            .map(parameter -> getAppComponent(parameter.getType()))
                            .toArray();
                    appComponent = method.invoke(appObject, methodParamObjects);
                }
                appComponents.add(appComponent);
                appComponentsByName.put(appComponentName, appComponent);

            } catch (Exception e) {
                logger.error("Error while creating bean annotated AppComponent: {}", e.getMessage());
                throw new RuntimeException();
            }
        }
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    // todo Без понятия как пофиксить unchecked cast в этих двух методах

    /**
     * Получение бина по типу его класса/интерфейса
     * @param componentClass тип класса
     * @return бин из контекста
     */
    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        var appComponentsByClass = appComponents.stream()
                .filter(appComponent -> componentClass.isAssignableFrom(appComponent.getClass()))
                .toList();
        if (appComponentsByClass.isEmpty()) {
            throw new RuntimeException("No such AppComponent: " + componentClass.getName());
        }
        if (appComponentsByClass.size() > 1) {
            throw new RuntimeException("There are more than one AppComponent: " + componentClass.getName());
        }
        try {
            return (C) appComponentsByClass.getFirst();
        } catch (ClassCastException cce) {
            throw new RuntimeException("Can't cast to " + componentClass.getName(), cce);
        }
    }

    /**
     * Получение бина по его имени
     * @param componentName имя бина
     * @return бин
     */
    @Override
    public <C> C getAppComponent(String componentName) {
        var object = appComponentsByName.get(componentName);
        if (object != null) {
            try {
                return (C) object;
            } catch (ClassCastException cce) {
                throw new RuntimeException("Can't cast component: " + componentName, cce);
            }
        } else {
            throw new RuntimeException("No such AppComponent: " + componentName);
        }
    }
}
