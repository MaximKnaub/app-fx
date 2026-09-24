package de.mknaub.appfx;

import de.mknaub.appfx.annotations.Link;
import de.mknaub.appfx.annotations.Service;
import de.mknaub.appfx.services.AbstractService;
import javafx.stage.Stage;
import javax.annotation.PostConstruct;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AppFxTest {

    @Test
    void injectsLinksDeclaredOnEveryLevelOfTheClassHierarchy() {
        LeafService leaf = new TestApp().getService(LeafService.class);

        assertAll(
                () -> assertNotNull(leaf.leafLink(), "@Link declared on the service itself"),
                () -> assertNotNull(leaf.midLink(), "@Link declared on the direct superclass"),
                () -> assertNotNull(leaf.grandLink(), "@Link declared two levels up"));
    }

    @Test
    void invokesPostConstructDeclaredOnEveryLevelOfTheClassHierarchyOnce() {
        LeafService leaf = new TestApp().getService(LeafService.class);

        assertAll(
                () -> assertEquals(1, leaf.leafInitCalls, "@PostConstruct declared on the service itself"),
                () -> assertEquals(1, leaf.midInitCalls, "@PostConstruct declared on the direct superclass"),
                () -> assertEquals(1, leaf.grandInitCalls, "@PostConstruct declared two levels up"));
    }

    @Test
    void injectsLinksBeforeInvokingPostConstruct() {
        LeafService leaf = new TestApp().getService(LeafService.class);

        assertAll(
                () -> assertNotNull(leaf.leafLinkDuringInit, "@Link on the service itself"),
                () -> assertNotNull(leaf.midLinkDuringInit, "@Link on the direct superclass"),
                () -> assertNotNull(leaf.grandLinkDuringInit, "@Link two levels up"));
    }

    /** Never launched: services need neither FXML nor a running JavaFX toolkit. */
    static class TestApp extends AppFx {

        @Override
        protected void startApplication(Stage stage) {
        }
    }

    @Service
    public static class LinkedService extends AbstractService {
    }

    public static class GrandService extends AbstractService {

        @Link private LinkedService grandLink;
        int grandInitCalls;
        LinkedService grandLinkDuringInit;

        LinkedService grandLink() {
            return grandLink;
        }

        @PostConstruct
        private void grandInit() {
            grandInitCalls++;
            grandLinkDuringInit = grandLink;
        }
    }

    public static class MidService extends GrandService {

        @Link private LinkedService midLink;
        int midInitCalls;
        LinkedService midLinkDuringInit;

        LinkedService midLink() {
            return midLink;
        }

        @PostConstruct
        private void midInit() {
            midInitCalls++;
            midLinkDuringInit = midLink;
        }
    }

    @Service
    public static class LeafService extends MidService {

        @Link private LinkedService leafLink;
        int leafInitCalls;
        LinkedService leafLinkDuringInit;

        LinkedService leafLink() {
            return leafLink;
        }

        @PostConstruct
        private void leafInit() {
            leafInitCalls++;
            leafLinkDuringInit = leafLink;
        }
    }
}
