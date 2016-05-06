package de.mknaub.applicationfx.services;

import de.mknaub.appfx.application.ApplicationFX;
import de.mknaub.applicationfx.controller.AbstractController;
import de.mknaub.appfx.utils.Scope;

/**
 *
 * @author Kaifuno
 * @date 11.03.2013
 */
public class AbstractService implements Service {

    protected ApplicationFX application;
    private Scope scope;

    /**
     * Gibt die Instanz der AbstractApplication.java zurück
     *
     * @return
     */
    @Override
    public ApplicationFX getApplication() {
        return application;
    }

    @Override
    public void setApplication(ApplicationFX application) {
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
