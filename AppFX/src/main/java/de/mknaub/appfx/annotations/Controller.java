package de.mknaub.appfx.annotations;

import de.mknaub.appfx.utils.Scope;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author maka
 * @date 05.04.2013
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Controller {

    String url();

    String resourceBundle() default "";

    Scope scope() default Scope.SINGLETON;
}
