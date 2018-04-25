package sample;


import com.jfoenix.controls.JFXButton;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    Elevator[] elevators = new Elevator[5];

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        JFXButton button = (JFXButton) root.lookup("#floor20");


        for(int i = 0; i < 5; i++) {
            elevators[i] = new Elevator(root,i+1);
            elevators[i].start();
        }

        Controller controller = new Controller();
        controller.init(root,elevators);
        for(int i = 0; i < 5; i++) {
            elevators[i].setController(controller);
        }




        primaryStage.setTitle("Sim Elevator");
        primaryStage.setScene(new Scene(root, 570, 500));
        primaryStage.show();



    }


    public static void main(String[] args) {
        launch(args);
    }
}
