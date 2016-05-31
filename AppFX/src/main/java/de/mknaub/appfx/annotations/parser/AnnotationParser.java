package de.mknaub.appfx.annotations.parser;

import de.mknaub.appfx.annotations.Controller;
import de.mknaub.appfx.annotations.Link;
import de.mknaub.appfx.annotations.Service;
import de.mknaub.appfx.AppFx;
import de.mknaub.appfx.controller.AbstractController;
import de.mknaub.appfx.controller.ControllerLoader;
import de.mknaub.appfx.services.AbstractService;
import de.mknaub.appfx.utils.Scope;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maka
 * @date 05.04.2013
 */
@Deprecated
public class AnnotationParser {

    private static AnnotationParser instance;
    private static AppFx application;
    private static Map<Class<? extends Object>, Object> controllers = new HashMap<>();
    private static Map<Class<? extends Object>, Object> services = new HashMap<>();

    public static AnnotationParser getInstance(AppFx applicationFX, Map<Class<? extends Object>, Object> controllers, Map<Class<? extends Object>, Object> services) {
        if (instance != null) {
            return instance;
        } else {
            AnnotationParser.application = applicationFX;
            AnnotationParser.controllers.putAll(controllers);
            AnnotationParser.services.putAll(services);
            return new AnnotationParser();
        }
    }

    private AnnotationParser() {
    }

    public void parseClass(final Class clazz) {
        Annotation[] annotations = clazz.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(Controller.class)) {
                final Controller ctrl = (Controller) clazz.getAnnotation(Controller.class);
                try {
//                    AbstractController c = (AbstractController) Arrays.asList(clazz.getConstructors()).stream().findFirst().get().newInstance();
                    AbstractController c = (AbstractController) ControllerLoader.loadController(ctrl.url(), ctrl.resourceBundle(), application);
                    c.setApplication(application);
                    controllers.put(clazz, c);
                } catch (SecurityException | IllegalArgumentException ex) {
                    Logger.getLogger(AnnotationParser.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (annotation.annotationType().equals(Service.class)) {
                try {
                    final Constructor constructor = clazz.getConstructor();
                    final Service service = (Service) annotation;
                    AbstractService abstractService = (AbstractService) Arrays.asList(clazz.getConstructors()).stream().findFirst().get().newInstance();
                    abstractService.setScope(service.scope());
                    abstractService.setApplication(application);
                    services.put(clazz, abstractService);
                } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(AnnotationParser.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void parseInnerClass(Class clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            Annotation[] fieldAnnotations = field.getAnnotations();
            for (Annotation annotation : fieldAnnotations) {
                try {
                    if (annotation.annotationType().equals(Link.class)) {
                        if (instanceOf(field.getType(), AbstractController.class)) {
                            if (instanceOf(clazz, AbstractController.class)) {
                                field.set(controllers.get(clazz), controllers.get(field.getType()));
                            } else if (instanceOf(clazz, AbstractService.class)) {
                                field.set(services.get(clazz), controllers.get(field.getType()));
                            }
                        } else if (instanceOf(field.getType(), AbstractService.class)) {
                            AbstractService service = (AbstractService) services.get(field.getType());
                            if (Scope.PROTOTYPE.equals(service.getScope())) {
                                service = (AbstractService) service.getClass().getDeclaredConstructor(Scope.class).newInstance(Scope.PROTOTYPE);
                                if (instanceOf(clazz, AbstractController.class)) {
                                    field.set(controllers.get(clazz), service);
                                } else if (instanceOf(clazz, AbstractService.class)) {
                                    field.set(services.get(clazz), service);
                                }
                            } else {
                                if (instanceOf(clazz, AbstractController.class)) {
                                    try {

                                        field.set(controllers.get(clazz), services.get(field.getType()));
                                    } catch (NullPointerException e) {
                                    }
                                } else if (instanceOf(clazz, AbstractService.class)) {
                                    field.set(services.get(clazz), services.get(field.getType()));
                                }
                            }
                        }
                    }
                } catch (SecurityException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    java.util.logging.Logger.getLogger(AnnotationParser.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            field.setAccessible(false);
        }
    }

    public void parseInnerClass(AbstractController ctrl, Class clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            Annotation[] fieldAnnotations = field.getAnnotations();
            for (Annotation annotation : fieldAnnotations) {
                try {
                    if (annotation.annotationType().equals(Link.class)) {
                        if (instanceOf(field.getType(), AbstractController.class)) {
                            if (instanceOf(clazz, AbstractController.class)) {
                                field.set(ctrl, controllers.get(field.getType())); // TODO hier null pointer
                            }
                        } else if (instanceOf(field.getType(), AbstractService.class)) {
                            AbstractService service = (AbstractService) services.get(field.getType());
                            if (Scope.PROTOTYPE.equals(service.getScope())) {
                                service = (AbstractService) service.getClass().getDeclaredConstructor(Scope.class).newInstance(Scope.PROTOTYPE);
                                if (instanceOf(clazz, AbstractController.class)) {
                                    field.set(ctrl, service);
                                }
                            } else {
                                if (instanceOf(clazz, AbstractController.class)) {
                                    field.set(ctrl, services.get(field.getType()));
                                }
                            }
                        }
                    }
                } catch (SecurityException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    java.util.logging.Logger.getLogger(AnnotationParser.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            field.setAccessible(false);
        }
    }

    @Deprecated
    private boolean instanceOf(Class clazz, Class clazz2) {
        if (clazz.equals(clazz2)) {
            return true;
        } else {
            if (clazz.getName().equals(AbstractController.class.getName())
                    || clazz.getName().equals(AbstractService.class.getName())) {
                return false;
            } else {
                return instanceOf(clazz.getSuperclass(), clazz2);
            }
        }
    }
}
