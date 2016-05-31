package de.mknaub.appfx;

import de.mknaub.appfx.annotations.Controller;
import de.mknaub.appfx.annotations.Link;
import de.mknaub.appfx.annotations.Service;
import de.mknaub.appfx.annotations.parser.AnnotationParser;
import de.mknaub.appfx.controller.AbstractController;
import de.mknaub.appfx.controller.ControllerLoader;
import de.mknaub.appfx.services.AbstractService;
import de.mknaub.appfx.utils.Scope;
import static de.mknaub.appfx.utils.Scope.SINGLETON;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javax.annotation.PostConstruct;

/**
 * @author maka
 * @date 01.11.2012
 *
 * In AbstractApplication.java werden einige Regeln und Methoden festgelegt die
 * von abgeleitenden Klassen übernommen und überschrieben werden müssen.<br>
 * <br> Diese Klasse sollte auf keinen Fall modifiziert werden
 *
 */
public abstract class AppFx extends Application {

    public AppFx() {
    }

    abstract public void start(Stage stage) throws Exception;

//    --> old
    protected final Map<Class<? extends Object>, Object> controllers = new HashMap<>();
    private final Map<Class<? extends Object>, Object> services = new HashMap<>();
    private Stage primaryStage;
    @Deprecated
    private List<Class<?>> classes;
    private final AnnotationParser parser = AnnotationParser.getInstance(this, controllers, services);
    //

    public AppFx(final Stage stage, final Class<? extends AbstractController> mainCtrl) {
        this.primaryStage = stage;
        this.primaryStage.setScene(new Scene((Parent) new Pane(), 1024, 768));

        primaryStage.getScene().getStylesheets().add("/de/mknaub/applicationfx/style/css/appfx-bright-theme.css");
        primaryStage.getScene().getStylesheets().add("/de/mknaub/applicationfx/style/css/default-style.css");
        primaryStage.getScene().getStylesheets().add("/de/mknaub/applicationfx/control/control.css");
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Gibt die Instanz der übergebenen Controller Klasse zurück<br> <br>
     * Beispiel Aufruf der Methode:<br> getController(Controller.class);
     *
     * @param <C>
     * @param controllerClass
     * @return
     */
    public final <C extends AbstractController> C getController(Class<C> controllerClass) {
        Controller c = controllerClass.getAnnotation(Controller.class);
        if (c != null) {
            if (c.scope().equals(Scope.PROTOTYPE)) {
                return loadController(controllerClass);
            } else {
                C ctrl = (C) controllers.get(controllerClass);
                if (ctrl == null) {
                    return loadController(controllerClass);
                }
                return (C) controllers.get(controllerClass);
            }
        }
        // TODO throw error because no annotation is present
        return null;
    }

    /**
     * Gibt die Instanz der übergebenen Service Klasse zurück<br> <br>
     * Beispiel Aufruf der Methode:<br> getService(Service.class);
     *
     * @param <C>
     * @param serviceClass
     * @return
     */
    public <C extends AbstractService> C getService(Class<C> serviceClass) {
        Service s = serviceClass.getAnnotation(Service.class);
        if (s != null) {
            if (s.scope().equals(Scope.PROTOTYPE)) {
                return loadService(serviceClass);
            } else {
                C service = (C) services.get(serviceClass);
                if (service == null) {
                    service = loadService(serviceClass);
                }
                return service;
            }
        }
        // TODO throw error because no annotation is present
        return null;
    }

    /**
     *
     * @param <C>
     * @param ctrlClass
     * @return
     */
    public <C extends AbstractController> C loadController(Class<C> ctrlClass) {
        AbstractController ctrl = null;
        try {
            Controller ctrlAnno = ctrlClass.getAnnotation(Controller.class);
            ctrl = (AbstractController) ControllerLoader.loadController(ctrlAnno.url(), ctrlAnno.resourceBundle(), this);
            if (ctrlAnno.scope().equals(Scope.SINGLETON)) {
                controllers.put(ctrlClass, ctrl);
            }
            parseInnerClass(ctrl);
            invokePostConstruct(ctrl);
        } catch (IllegalArgumentException | IllegalAccessException e) {
//            TODO log
        }
        return (C) ctrl;
    }

    /**
     *
     * @param <C>
     * @param serviceClass
     * @return
     */
    public <C extends AbstractService> C loadService(Class<C> serviceClass) {
        AbstractService service = null;
        Service serviceAnno = serviceClass.getAnnotation(Service.class);
        try {
            service = (AbstractService) Arrays.asList(serviceClass.getConstructors()).stream().findFirst().get().newInstance();
            service.setScope(serviceAnno.scope());
            service.setApplication(this);
            if (serviceAnno.scope().equals(SINGLETON)) {
                services.put(serviceClass, service);
            }
            parseInnerClass(service);
            invokePostConstruct(service);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(AppFx.class.getName()).log(Level.SEVERE, null, ex);
            // TODO log
        }
        return (C) service;
    }

    /* ///// UTILS ///// */
    private boolean instanceOf(Class clazz, Class clazz2) {
        if (clazz.equals(clazz2)) {
            return true;
        } else if (clazz.getName().equals(AbstractController.class.getName())
                || clazz.getName().equals(AbstractService.class.getName())
                || clazz.getName().equals("java.lang.Object")) {
            return false;
        } else {
            return instanceOf(clazz.getSuperclass(), clazz2);
        }
    }

    private void parseInnerClass(Object instance) throws IllegalArgumentException, IllegalAccessException {
        check(instance, instance.getClass().getDeclaredFields());
        Class<? extends Object> superclass = instance.getClass().getSuperclass();
        while (instanceOf(superclass, AbstractController.class)
                || instanceOf(superclass, AbstractService.class)) {
            check(instance, superclass.getDeclaredFields());
            superclass = superclass.getClass().getSuperclass();
        }
    }

    private void check(Object instance, Field[] fields) throws IllegalArgumentException, IllegalAccessException {
        for (Field field : fields) {
            Link link = field.getAnnotation(Link.class);
            if (link != null) {
                boolean wasAccesible = field.isAccessible();
                field.setAccessible(true);
                if (instanceOf(field.getType(), AbstractController.class)) {
                    field.set(instance, getController((Class<AbstractController>) field.getType()));
                } else if (instanceOf(field.getType(), AbstractService.class)) {
                    field.set(instance, getService((Class<AbstractService>) field.getType()));
                }
                field.setAccessible(wasAccesible);
            }
        }
    }

    public void invokePostConstruct(Object instance) {
        if (instanceOf(instance.getClass(), AbstractController.class) || instanceOf(instance.getClass(), AbstractService.class)) {
            invokePostConstruct(instance, instance.getClass().getDeclaredMethods());
            Class<? extends Object> superclass = instance.getClass().getSuperclass();
            while (instanceOf(superclass, AbstractController.class)
                    || instanceOf(superclass, AbstractService.class)) {
                invokePostConstruct(instance, superclass.getDeclaredMethods());
                superclass = superclass.getClass().getSuperclass();
            }
        }
    }

    private void invokePostConstruct(Object instance, Method[] methods) {
        Arrays.asList(methods).forEach(method -> {
            PostConstruct annotation = method.getAnnotation(PostConstruct.class);
            if (annotation != null) {
                try {
                    boolean wasAccessible = method.isAccessible();
                    method.setAccessible(true);
                    method.invoke(instance);
                    method.setAccessible(wasAccessible);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(AppFx.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
}
