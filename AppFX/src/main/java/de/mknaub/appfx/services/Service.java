package de.mknaub.applicationfx.services;

import de.mknaub.appfx.application.ApplicationFX;
import de.mknaub.appfx.utils.Scope;

/**
 *
 * @author Kaifuno
 * @date 11.03.2013
 */
public interface Service {

    ApplicationFX getApplication();

    void setApplication(ApplicationFX application);

    Scope getScope();

    void setScope(Scope scope);

}
