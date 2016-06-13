package de.mknaub.appfx.controller;

import de.mknaub.appfx.AppFx;
import de.mknaub.appfx.services.AbstractService;
import de.mknaub.appfx.annotations.Scope;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

/**
 * Schnittstelle für alle Controller<br>
 * Ermöglicht es die Controller dynamisch zu laden.
 *
 * @author maka
 * @date 01.11.2012
 */
public interface Controller {

    ObjectProperty<Node> viewProperty();

    Node getView();

    void setView(Node node);

    AppFx getApplication();

    void setApplication(AppFx application);

    /**
     * Gibt die Instanz der übergebenen Controller Klasse zurück<br> <br>
     * Beispiel Aufruf der Methode:<br> getController(Controller.class);
     *
     * @param <C>
     * @param controllerClass
     * @return
     */
    public <C extends AbstractController> C getController(Class<C> controllerClass);

    /**
     * Gibt die Instanz der übergebenen Service Klasse zurück<br> <br>
     * Beispiel Aufruf der Methode:<br> getService(Service.class);
     *
     * @param <C>
     * @param serviceClass
     * @return
     */
    public <C extends AbstractService> C getService(Class<C> serviceClass);

    Scope getScope();

    void setScope(Scope scope);
}
