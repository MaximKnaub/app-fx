package de.mknaub.applicationfx.services;

import de.mknaub.appfx.AppFx;
import de.mknaub.appfx.utils.Scope;

/**
 *
 * @author Kaifuno
 * @date 11.03.2013
 */
public interface Service {

    AppFx getApplication();

    void setApplication(AppFx application);

    Scope getScope();

    void setScope(Scope scope);

}
