package energyData.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class StageInitializer implements ApplicationListener<EnergyDateGUI.StageReadyEvent> {
    @Value("classpath:/main.fxml")
    private Resource energyDataGUIResource;

    private ApplicationContext applicationContext;

    public StageInitializer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    @Override
    public void onApplicationEvent(EnergyDateGUI.StageReadyEvent event) {
        try {
            // load scene from FMXL file
            FXMLLoader fxmlLoader = new FXMLLoader(energyDataGUIResource.getURL());

            // set application context to use spring boot in FXML controller fully
            fxmlLoader.setControllerFactory(aClass -> applicationContext.getBean(aClass));

            Parent parent = fxmlLoader.load();
            Stage stage = event.getStage();
            stage.setScene(new Scene(parent, 600, 450));

            setupBaseStageSettings(stage);

            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupBaseStageSettings(Stage stage) {
        stage.setTitle("Energy Data");
        stage.setMaxHeight(450);
        stage.setMinHeight(450);

        stage.setMaxWidth(600);
        stage.setMinWidth(600);

        addIcon(stage);
    }

    private void addIcon(Stage stage) {
        InputStream iconStream = getClass().getResourceAsStream("/images/icon.png");
        Image image = new Image(iconStream);

        stage.getIcons().add(image);
    }
}