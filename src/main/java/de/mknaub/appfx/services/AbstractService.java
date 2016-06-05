package de.mknaub.appfx.services;

import de.mknaub.appfx.AppFx;
import de.mknaub.appfx.controller.AbstractController;
import de.mknaub.appfx.annotations.Scope;

/**
 *
 * @author Kaifuno
 * @date 11.03.2013
 */
public class AbstractService implements Service {

    protected AppFx application;
    private Scope scope;

    /**
     * Gibt die Instanz der AbstractApplication.java zurück
     *
     * @return
     */
    @Override
    public AppFx getApplication() {
        return application;
    }

    @Override
    public void setApplication(AppFx application) {
        this.application = application;
    }

    @Override public Scope getScope() {
        return this.scope;
    }

    @Override public void setScope(Scope scope) {
        this.scope = scope;
    }

    public final <C extends AbstractController> C getController(Class<C> controllerClass) {
        return application.getController(controllerClass);
    }

    public <C extends AbstractService> C getService(Class<C> serviceClass) {
        return application.getService(serviceClass);
    }

}
