package de.mknaub.appfx.annotations;

import de.mknaub.appfx.utils.Scope;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author maka
 * @date 08.04.2013
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Service {

    Scope scope() default Scope.SINGLETON;
}
