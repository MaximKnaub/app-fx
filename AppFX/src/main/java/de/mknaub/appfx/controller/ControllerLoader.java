package de.mknaub.applicationfx.controller;

import de.mknaub.appfx.AppFx;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 *
 * @author maka
 * @date 05.04.2013
 */
public class ControllerLoader {

    /**
     * Diese Methode ist für das Laden der Controller zuständig.<br> Hierfür
     * muss die URL der FXML Datei übergeben werden.<br> Es <u>darf keine andere
     * Methode</u> für das Laden der Controller verwendet werden.<br> <br> Der
     * <u>zwote Parameter</u> ist die Instance der Klasse die von
     * <u>AbstractApplication.java</u> abgeleitet wird.<br> Da diese Instance in
     * jedem Controller dem Konstruktor übergeben werden.<br> <br> Ein Aufruf
     * der Methode sieht folgendermaßen aus:<br> initView("/fxml/Settings.fxml",
     *
     * @param url
     * @param resourceBundle
     * @param applicationFX
     * @return Controller
     */
    public static Controller loadController(String url, String resourceBundle, AppFx applicationFX) {
        try {
            try (InputStream fxmlStream = ControllerLoader.class.getResourceAsStream(url)) {
                FXMLLoader loader = new FXMLLoader();
                if (resourceBundle != null && resourceBundle.isEmpty() == false) {
                    loader.setResources(ResourceBundle.getBundle(resourceBundle));
                }
                loader.setLocation(ControllerLoader.class.getResource(url));
                Parent parent = (Parent) loader.load(fxmlStream);
                Controller controller = (Controller) loader.getController();
                controller.setView((Parent) parent);
                controller.setApplication(applicationFX);
                return controller;
            } catch (IOException e) {
                throw new RuntimeException(String.format("Unable to load FXML file '%s'", url), e);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(String.format("Unable to load FXML file '%s'", url), e);
        }
    }

    public static de.mknaub.appfx.annotations.Controller getControllerAnnotationViewUrl(Class<? extends AbstractController> clazz) {
        Annotation[] annotations = clazz.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(de.mknaub.appfx.annotations.Controller.class)) {
                return (de.mknaub.appfx.annotations.Controller) clazz.getAnnotation(de.mknaub.appfx.annotations.Controller.class);
            }
        }
        return null;
    }
}
