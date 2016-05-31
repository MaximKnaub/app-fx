package de.mknaub.appfxdemo.ctrl;

import de.mknaub.appfx.annotations.Controller;
import de.mknaub.appfx.controller.AbstractController;
import javafx.fxml.FXML;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author maka
 */
@Slf4j
@Controller(url = "/de/mknaub/appfxdemo/fxml/Main.fxml")
public class MainCtrl extends AbstractController {

    @PostConstruct
    private void postConstruct() {
        log.trace("postConstruct(" + getClass().getName() + ")");
    }
    
    @FXML private void onClick(){
        log.trace("click");
    }
}
