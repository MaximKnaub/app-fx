package de.mknaub.applicationfx.controller;

import de.mknaub.appfx.application.ApplicationFX;
import de.mknaub.applicationfx.services.AbstractService;
import de.mknaub.appfx.utils.Scope;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

/**
 * Hier werden alle Methode vom implantiertem Interface überschrieben.<br> In
 * dieser Klasse werden außerdem alle wichtigen Eigenschaften, die jeder
 * Controller beinhalten soll, verwaltet.<br>
 * <ui>
 * <li>application</li>
 * <li>view</li>
 * <li>scope</li>
 * </ui>
 *
 * @author maka
 * @date 01.11.2012
 */
public abstract class AbstractController implements Controller {

    protected ApplicationFX application;
    private final ObjectProperty<Node> viewProperty = new SimpleObjectProperty<>(null);
    private Scope scope;

    /**
     * Gibt die Instanz der AbstractApplication.java zurück
     *
     * @return
     */
    @Override public ApplicationFX getApplication() {
        return application;
    }

    @Override public void setApplication(ApplicationFX application) {
        this.application = application;
    }

    @Override public <C extends AbstractController> C getController(Class<C> controllerClass) {
        return getApplication().getController(controllerClass);
    }

    @Override public <C extends AbstractService> C getService(Class<C> serviceClass) {
        return getApplication().getService(serviceClass);
    }

    @Override public ObjectProperty<Node> viewProperty() {
        return viewProperty;
    }

    /**
     * Gibt die Oberfläche des Aktuellen Controllers zurück
     *
     * @return
     */
    @Override public Node getView() {
        return viewProperty.getValue();
    }

    /**
     * Setzt die Oberfläche des Aktuellen Controllers.<br>
     * Soll nur dann aufgerufen werden wenn der Controller gerade eben geladen
     * wurde.
     *
     * @param view
     */
    @Override public void setView(Node view) {
        this.viewProperty.setValue(view);
    }

    @Override public Scope getScope() {
        return this.scope;
    }

    @Override public void setScope(Scope scope) {
        this.scope = scope;
    }
}
