/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tetris;

import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class TetrisGame extends Application {
    
    /**
     *
     */
    public static final int TETROMINOES_SIZE = 32; //Tetramino's one side's lenght
    
    @FXML
    public void TetrisStartButtonHandler(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("FXML.fxml"));
        Scene tetris = new Scene(root);
        
        Node target = (Node) event.getTarget();
        Stage TetrisStage = (Stage) target.getScene().getWindow();
        
        TetrisStage.setScene(tetris);
        TetrisStage.show();
    }
    
    @FXML
    public void TetrisMovementAndControlsButtonHandler(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("movement_and_controls_FXML.fxml"));
        Scene movement_and_controls = new Scene(root);

        Node target = (Node) event.getTarget();
        Stage movement_and_controls_stage = (Stage) target.getScene().getWindow();
        
        movement_and_controls_stage.setScene(movement_and_controls);
        movement_and_controls_stage.show();
    }
    
    @FXML
    public void TetrisTheGoalOfGameButtonHandler(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("the_goal_of_the_game_FXML.fxml"));
        Scene the_goal_of_the_game = new Scene(root);

        Node target = (Node) event.getTarget();
        Stage the_goal_of_the_game_stage = (Stage) target.getScene().getWindow();
        
        the_goal_of_the_game_stage.setScene(the_goal_of_the_game);
        the_goal_of_the_game_stage.show();
    }
    
    @FXML
    public void TetrisWhatAreTetrominoesButtonHandler(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("what_are_tetrominoes_FXML.fxml"));
        Scene what_are_tetrominoes = new Scene(root);

        Node target = (Node) event.getTarget();
        Stage what_are_tetrominoes_stage = (Stage) target.getScene().getWindow();
        
        what_are_tetrominoes_stage.setScene(what_are_tetrominoes);
        what_are_tetrominoes_stage.show();
    }
    
    
    
    @Override
    public void start(Stage MainMenuStage) throws Exception {
        MainMenuStage.setOnCloseRequest((WindowEvent e) -> {
            Platform.exit();
            System.exit(0);
        });
        Parent root = FXMLLoader.load(getClass().getResource("MainMenu_FXML.fxml"));
        Scene opening = new Scene(root);
        MainMenuStage.setTitle("TETRIS");
        MainMenuStage.setScene(opening);
        MainMenuStage.show();
    }
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}