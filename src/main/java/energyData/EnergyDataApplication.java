package energyData;

import energyData.gui.EnergyDateGUI;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EnergyDataApplication {
    public static void main(String[] args) {
        Application.launch(EnergyDateGUI.class, args);
    }
}
