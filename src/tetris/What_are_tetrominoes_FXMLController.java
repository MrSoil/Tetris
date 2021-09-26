/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tetris;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 */
public class What_are_tetrominoes_FXMLController implements Initializable {

    /**
     * Initializes the controller class.
     * @param event
     * @throws java.io.IOException
     */
    
    @FXML
    public void GoToMainMenuButtonHandler(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("MainMenu_FXML.fxml"));
        Scene MainMenu = new Scene(root);
        
        Stage MainMenuStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        MainMenuStage.setScene(MainMenu);
        MainMenuStage.show();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
