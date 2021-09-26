/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tetris;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;


public class Controller_of_the_tetris implements Initializable {
    
    /**
     *
     */
    private boolean red_danger = false;
    
    private boolean yellow_danger = false;
    
    private boolean isGameOver = false;
    
    private Timer drop = new Timer();
    
    private Tetrominos_Shape tetromino;
    
    private ArrayList<Tetrominos_Shape> oldTetrominoes = new ArrayList();
    
    private Tetrominos_Shape new_tetromino;
    
    private Image background_image_default = new Image(getClass().getResourceAsStream("/images/background.JPG"));
    
    private Image background_image_yellow = new Image(getClass().getResourceAsStream("/images/background_yellow.jpg"));
    
    private Image background_image_red = new Image(getClass().getResourceAsStream("/images/background_red.jpg"));
    
    private Image gameover_image = new Image(getClass().getResourceAsStream("/images/gameover.PNG"));
    
    private Image paused_image = new Image(getClass().getResourceAsStream("/images/paused.PNG"));
    
    private Image music_on_image = new Image(getClass().getResourceAsStream("/images/music_on.png"));
    
    private Image music_off_image = new Image(getClass().getResourceAsStream("/images/music_off.png"));
    
    private int cleaned_lines = 0;
    
    private boolean isPaused = false;
    
    private boolean pause_clicker = true;
    
    private File file = new File("music/tetris_music.wav");
    
    private Media tetris_media = new Media(file.toURI().toString()); 
    
    private MediaPlayer tetris_mediaplayer = new MediaPlayer(tetris_media);
    
    private boolean IsMplayer = true;
    
    @FXML
    private ImageView ghostground;
    
    @FXML
    private ImageView background;
    
    @FXML
    private ImageView music;
    
    @FXML
    private GridPane tetris_grid;
    
    @FXML
    private Label score;
    
    @FXML
    private Label score_gameover;
    
    @FXML
    private Label level;
    
    @FXML
    private Label level_gameover;
    
    @FXML
    private Button exit;
    
    @FXML
    private Button go_to_mainmenu_gameover;
    
    @FXML
    private Button continue_game;
    
    @FXML
    private void GoToMainMenuButtonHandler(ActionEvent event) throws IOException {
        tetris_mediaplayer.stop();
        drop.cancel();
        Parent root = FXMLLoader.load(getClass().getResource("MainMenu_FXML.fxml"));
        Scene MainMenu = new Scene(root);
        
        Stage MainMenuStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        MainMenuStage.setScene(MainMenu);
        MainMenuStage.show();
    }
    
    @FXML
    private void GoToMainMenuButtonHandler_gameover(ActionEvent event) throws IOException {
        tetris_mediaplayer.stop();
        drop.cancel();
        Parent root = FXMLLoader.load(getClass().getResource("MainMenu_FXML.fxml"));
        Scene MainMenu = new Scene(root);
        
        Stage MainMenuStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        MainMenuStage.setScene(MainMenu);
        MainMenuStage.show();
    }
    
    @FXML
    private void ExitButtonHandler(ActionEvent event) throws IOException {
        tetris_mediaplayer.stop();
        drop.cancel();
        System.exit(0);
    }
    
    @FXML
    private void PauseButtonHandler(ActionEvent event) throws IOException {
        Pause();
    }
    
    @FXML
    private void MusicButtonHandler(ActionEvent event) throws IOException {
        if(IsMplayer == true){
            tetris_mediaplayer.pause();
            IsMplayer = false;
            music.setImage(music_off_image);
        }
        else if(IsMplayer == false){
            tetris_mediaplayer.play();
            IsMplayer = true;
            music.setImage(music_on_image);
        }
    }
    
    @FXML
    private void moveHandler(KeyEvent event) throws IOException {
        switch(event.getCode()){
            case RIGHT:
                if(getIsPaused() == false){
                    move_right(tetromino);
                }
                break;
            case LEFT:
                if(getIsPaused() == false){
                    move_left(tetromino);
                }
                break;
            case DOWN:
                if(getIsPaused() == false){
                    move_down_soft(tetromino);
                }
                break;
            case UP:
                if(getIsPaused() == false){
                    rotation_1(tetromino);
                }
                break;
            case ENTER:
                if(getIsPaused() == false){
                    move_down_hard(tetromino);
                }
                break;
            case ESCAPE:
                Pause();
                break;
        }
    }
    
    /**
     *
     * @param keyEvent
     */
    
    private Node getNodeFromGridPane(int col, int row) {
        for (int i = 0; i < tetris_grid.getChildren().size(); i++) {

            Node child = tetris_grid.getChildren().get(i);

            if (child.isManaged()) {

                Integer column_index = GridPane.getColumnIndex(child);
                Integer row_index = GridPane.getRowIndex(child);
                
                if(column_index != null && row_index != null && column_index == col && row_index == row){
                    return child;
                }
            }
        }
    return null;
    }
 
    private void removeFromGridPane(int col, int row) {
        for (int i = 0; i < tetris_grid.getChildren().size(); i++) {

            Node child = tetris_grid.getChildren().get(i);

            if (child.isManaged()) {

                Integer column_index = GridPane.getColumnIndex(child);
                Integer row_index = GridPane.getRowIndex(child);
                
                if(column_index != null && row_index != null && column_index == col && row_index == row){
                    tetris_grid.getChildren().remove(child);
                }
            }
        }
    }
    
    private void addNodeGridPane(int col, int row, Node node) {
        for (int i = 0; i < tetris_grid.getChildren().size(); i++) {

            Node child = tetris_grid.getChildren().get(i);

            if (child.isManaged()) {

                Integer column_index = GridPane.getColumnIndex(child);
                Integer row_index = GridPane.getRowIndex(child);
                
                if(column_index != null && row_index != null && column_index == col && row_index == row){
                    tetris_grid.getChildren().remove(child);
                }
            }
        }
    }
    
    public void move_right(Tetrominos_Shape tetromino){
        if(tetromino.getFirst_grid_x() < 9 && tetromino.getSecond_grid_x() < 9 && tetromino.getThird_grid_x() < 9
                && tetromino.getFourth_grid_x() < 9
                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 1,tetromino.getFirst_grid_y()) == true && isEmptyGridWithException(tetromino.getSecond_grid_x() + 1,tetromino.getSecond_grid_y()) == true
                && isEmptyGridWithException(tetromino.getThird_grid_x() + 1,tetromino.getThird_grid_y()) == true && isEmptyGridWithException(tetromino.getFourth_grid_x() + 1,tetromino.getFourth_grid_y()) == true
                && getIsGameOver() == false && getIsPaused() == false){
            
            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 1);
            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 1);
            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 1);
            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 1);
            
            tetris_grid.getChildren().remove(tetromino.first);
            tetris_grid.getChildren().remove(tetromino.second);
            tetris_grid.getChildren().remove(tetromino.third);
            tetris_grid.getChildren().remove(tetromino.fourth);
            
            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());
            
        }
        
    }
    
    public void move_left(Tetrominos_Shape tetromino){
        if(tetromino.getFirst_grid_x() > 0 && tetromino.getSecond_grid_x() > 0 && tetromino.getThird_grid_x() > 0
                && tetromino.getFourth_grid_x() > 0 
                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 1,tetromino.getFirst_grid_y()) == true && isEmptyGridWithException(tetromino.getSecond_grid_x() - 1,tetromino.getSecond_grid_y()) == true
                && isEmptyGridWithException(tetromino.getThird_grid_x() - 1,tetromino.getThird_grid_y()) == true && isEmptyGridWithException(tetromino.getFourth_grid_x() - 1,tetromino.getFourth_grid_y()) == true
                && getIsGameOver() == false && getIsPaused() == false){

            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 1);
            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 1);
            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 1);
            
            tetris_grid.getChildren().remove(tetromino.first);
            tetris_grid.getChildren().remove(tetromino.second);
            tetris_grid.getChildren().remove(tetromino.third);
            tetris_grid.getChildren().remove(tetromino.fourth);
            
            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());
            
        }
    }
    
    public void move_down_soft(Tetrominos_Shape tetromino){
        if(tetromino.getFirst_grid_y() < 18 && tetromino.getSecond_grid_y() < 18 && tetromino.getThird_grid_y() < 18
                && tetromino.getFourth_grid_y() < 18
                && isEmptyGridWithException(tetromino.getFirst_grid_x(),tetromino.getFirst_grid_y() + 1) == true && isEmptyGridWithException(tetromino.getSecond_grid_x(),tetromino.getSecond_grid_y() + 1) == true
                && isEmptyGridWithException(tetromino.getThird_grid_x(),tetromino.getThird_grid_y() + 1) == true && isEmptyGridWithException(tetromino.getFourth_grid_x(),tetromino.getFourth_grid_y() + 1) == true
                && getIsGameOver() == false && getIsPaused() == false){
            
            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 1);
            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
            tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 1);
            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);
            
            tetris_grid.getChildren().remove(tetromino.first);
            tetris_grid.getChildren().remove(tetromino.second);
            tetris_grid.getChildren().remove(tetromino.third);
            tetris_grid.getChildren().remove(tetromino.fourth);
            
            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());
            int new_score = Integer.parseInt(score.getText().substring(7)) + 1;
            score.setText("SCORE: " + Integer.toString(new_score));

        }
    }
    
    public void move_down_hard(Tetrominos_Shape tetromino){
        int score_will_added = 0;
        for(int i = 0; i < 18; i++){
            if(tetromino.getFirst_grid_y() < 18 && tetromino.getSecond_grid_y() < 18 && tetromino.getThird_grid_y() < 18
                    && tetromino.getFourth_grid_y() < 18
                    && isEmptyGridWithException(tetromino.getFirst_grid_x(),tetromino.getFirst_grid_y() + 1) == true && isEmptyGridWithException(tetromino.getSecond_grid_x(),tetromino.getSecond_grid_y() + 1) == true
                    && isEmptyGridWithException(tetromino.getThird_grid_x(),tetromino.getThird_grid_y() + 1) == true && isEmptyGridWithException(tetromino.getFourth_grid_x(),tetromino.getFourth_grid_y() + 1) == true
                    && getIsGameOver() == false && getIsPaused() == false){

                tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 1);
                tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 1);
                tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);

                tetris_grid.getChildren().remove(tetromino.first);
                tetris_grid.getChildren().remove(tetromino.second);
                tetris_grid.getChildren().remove(tetromino.third);
                tetris_grid.getChildren().remove(tetromino.fourth);

                tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());
                score_will_added++;
            }
            else{
                int new_score = Integer.parseInt(score.getText().substring(7)) + (score_will_added * 2);
                score.setText("SCORE: " + Integer.toString(new_score));
                break;
            }
        }
    }
    
    public void move_down(Tetrominos_Shape tetromino){
        if(tetromino.getFirst_grid_y() < 18 && tetromino.getSecond_grid_y() < 18 && tetromino.getThird_grid_y() < 18
                && tetromino.getFourth_grid_y() < 18
                && isEmptyGridWithException(tetromino.getFirst_grid_x(),tetromino.getFirst_grid_y() + 1) == true && isEmptyGridWithException(tetromino.getSecond_grid_x(),tetromino.getSecond_grid_y() + 1) == true
                && isEmptyGridWithException(tetromino.getThird_grid_x(),tetromino.getThird_grid_y() + 1) == true && isEmptyGridWithException(tetromino.getFourth_grid_x(),tetromino.getFourth_grid_y() + 1) == true
                && getIsGameOver() == false && getIsPaused() == false){
            
            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 1);
            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
            tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 1);
            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);
            
            tetris_grid.getChildren().remove(tetromino.first);
            tetris_grid.getChildren().remove(tetromino.second);
            tetris_grid.getChildren().remove(tetromino.third);
            tetris_grid.getChildren().remove(tetromino.fourth);
            
            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());
        }
        else{
            if((tetromino.getFirst_grid_y() <= 7 || tetromino.getSecond_grid_y() <= 7 || tetromino.getThird_grid_y() <= 7 || tetromino.getFourth_grid_y() <= 7)
                && (tetromino.getFirst_grid_y() > 4 || tetromino.getSecond_grid_y() > 4 || tetromino.getThird_grid_y() > 4 || tetromino.getFourth_grid_y() > 4)
                && background.getImage() != background_image_yellow && background.getImage() != background_image_red){
                
                background.setImage(background_image_yellow);
                
            }
            else if((tetromino.getFirst_grid_y() <= 4 || tetromino.getSecond_grid_y() <= 4 || tetromino.getThird_grid_y() <= 4 || tetromino.getFourth_grid_y() <= 4)
                    && background.getImage() != background_image_red){
                
                background.setImage(background_image_red);
                
            }
            oldTetrominoes.add(this.tetromino);
            cleaner();
            changer();
            spawning();
        }
    }
    
    public void changer(){
        for(int y = 7; y > 4; y--){
            for(int x = 0; x < 10; x++){
                if(isEmptyGrid(x, y) == true){
                    yellow_danger = false;
                }
                else{
                    yellow_danger = true;
                    break;
                }
            }
            if(yellow_danger){
                break;
            }
        }
        
        for(int y = 4; y > 0; y--){
            for(int x = 0; x < 10; x++){
                if(isEmptyGrid(x, y) == true){
                    red_danger = false;
                }
                else{
                    red_danger = true;
                    break;
                }
            }
            if(red_danger){
                break;
            }
        }
        
        if(yellow_danger == true && red_danger == false && background.getImage() != background_image_yellow){

            background.setImage(background_image_yellow);

        }
        else if(red_danger == true && background.getImage() != background_image_yellow && background.getImage() != background_image_red){

            background.setImage(background_image_red);

        }
        else if(yellow_danger == false && red_danger == false){
            background.setImage(background_image_default);
        }
    }
    
    public void rotation_1(Tetrominos_Shape tetromino){
    if(getIsGameOver() == false && getIsPaused() == false){
        switch(tetromino.getName()){
            case "I":
                switch(tetromino.getForm()){
//--------------------------------------------------------------------------------------------------------------------------------------------------------------
                    case 1:
                        //Test 1
                        if(tetromino.getFirst_grid_x() + 2 < 10 && tetromino.getFirst_grid_y() - 1 < 19 && -1 < tetromino.getFirst_grid_x() + 2 && 0 < tetromino.getFirst_grid_y() - 1
                                && tetromino.getSecond_grid_x() + 1 < 10 && tetromino.getSecond_grid_y() < 19  && -1 < tetromino.getSecond_grid_x() + 1 && 0 < tetromino.getSecond_grid_y()
                                && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() + 1 < 19  && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y() + 1
                                && tetromino.getFourth_grid_x() - 1 < 10 && tetromino.getFourth_grid_y() + 2 < 19  && -1 < tetromino.getFourth_grid_x() - 1 && 0 < tetromino.getFourth_grid_y() + 2
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 2, tetromino.getFirst_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() + 1, tetromino.getSecond_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() - 1, tetromino.getFourth_grid_y() + 2) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 2);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 1);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y());
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 1);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 1);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 2);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 2
                        else if(tetromino.getFirst_grid_x() < 10 && tetromino.getFirst_grid_y() - 1 < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y() - 1
                                && tetromino.getSecond_grid_x() - 1 < 10 && tetromino.getSecond_grid_y() < 19 && -1 < tetromino.getSecond_grid_x() - 1 && 0 < tetromino.getSecond_grid_y()
                                && tetromino.getThird_grid_x() - 2 < 10 && tetromino.getThird_grid_y() + 1 < 19 && -1 < tetromino.getThird_grid_x() - 2 && 0 < tetromino.getThird_grid_y() + 1
                                && tetromino.getFourth_grid_x() - 3 < 10 && tetromino.getFourth_grid_y() + 2 < 19 && -1 < tetromino.getFourth_grid_x() - 3 && 0 < tetromino.getFourth_grid_y() + 2
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() - 1, tetromino.getSecond_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 2, tetromino.getThird_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() - 3, tetromino.getFourth_grid_y() + 2) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 1);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y());
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 2);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 1);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 3);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 2);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 3
                        else if(tetromino.getFirst_grid_x() + 3 < 10 && tetromino.getFirst_grid_y() - 1 < 19 && -1 < tetromino.getFirst_grid_x() + 3 && 0 < tetromino.getFirst_grid_y() - 1
                                && tetromino.getSecond_grid_x() + 2 < 10 && tetromino.getSecond_grid_y() < 19 && -1 < tetromino.getSecond_grid_x() + 2 && 0 < tetromino.getSecond_grid_y()
                                && tetromino.getThird_grid_x() + 1 < 10 && tetromino.getThird_grid_y() + 1 < 19 && -1 < tetromino.getThird_grid_x() + 1 && 0 < tetromino.getThird_grid_y() + 1
                                && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() + 2 < 19 && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y() + 2
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 3, tetromino.getFirst_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() - 2, tetromino.getSecond_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() +1 , tetromino.getThird_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y() + 2) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 3);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 1);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 2);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y());
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 1);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 2);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 4
                        else if(tetromino.getFirst_grid_x() < 10 && tetromino.getFirst_grid_y() < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y()
                                && tetromino.getSecond_grid_x() - 1 < 10 && tetromino.getSecond_grid_y() + 1 < 19 && -1 < tetromino.getSecond_grid_x() - 1 && 0 < tetromino.getSecond_grid_y() + 1
                                && tetromino.getThird_grid_x() - 2 < 10 && tetromino.getThird_grid_y() + 2 < 19 && -1 < tetromino.getThird_grid_x() - 2 && 0 < tetromino.getThird_grid_y() + 2
                                && tetromino.getFourth_grid_x() - 3 < 10 && tetromino.getFourth_grid_y() + 3 < 19 && -1 < tetromino.getFourth_grid_x() - 3 && 0 < tetromino.getFourth_grid_y() + 3
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() - 1, tetromino.getSecond_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 2, tetromino.getThird_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() - 3, tetromino.getFourth_grid_y() + 3) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y());
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 2);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 3);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 3);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 5 
                        else if(tetromino.getFirst_grid_x() + 3 < 10 && tetromino.getFirst_grid_y() - 3 < 19 && -1 < tetromino.getFirst_grid_x() + 3 && 0 < tetromino.getFirst_grid_y() - 3
                                && tetromino.getSecond_grid_x() + 2 < 10 && tetromino.getSecond_grid_y() - 2 < 19 && -1 < tetromino.getSecond_grid_x() + 2 && 0 < tetromino.getSecond_grid_y() - 2
                                && tetromino.getThird_grid_x() + 1 < 10 && tetromino.getThird_grid_y() - 1 < 19 && -1 < tetromino.getThird_grid_x() + 1 && 0 < tetromino.getThird_grid_y() - 1
                                && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() < 19 && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y()
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 3, tetromino.getFirst_grid_y() - 3) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() + 2, tetromino.getSecond_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() + 1, tetromino.getThird_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y()) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 3);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 3);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 2);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 2);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 1);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y());

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        else{
                            break;
                        }
                        break;
//--------------------------------------------------------------------------------------------------------------------------------------------------------------
                    case 2:
                        //Test 1
                        if(tetromino.getFirst_grid_x() - 2 < 10 && tetromino.getFirst_grid_y() + 2 < 19 && -1 < tetromino.getFirst_grid_x() - 2 && 0 < tetromino.getFirst_grid_y() + 2
                                && tetromino.getSecond_grid_x() - 1 < 10 && tetromino.getSecond_grid_y() + 1 < 19 && -1 < tetromino.getSecond_grid_x() - 1 && 0 < tetromino.getSecond_grid_y() + 1
                                && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() < 19 && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() + 1 < 10 && tetromino.getFourth_grid_y() - 1 < 19 && -1 < tetromino.getFourth_grid_x() + 1 && 0 < tetromino.getFourth_grid_y() - 1
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 2, tetromino.getFirst_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() - 1, tetromino.getSecond_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() + 1, tetromino.getFourth_grid_y() - 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 2);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 2);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 1);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 2
                        else if(tetromino.getFirst_grid_x() - 3 < 10 && tetromino.getFirst_grid_y() + 2 < 19 && -1 < tetromino.getFirst_grid_x() - 3 && 0 < tetromino.getFirst_grid_y() + 2
                                && tetromino.getSecond_grid_x() - 2 < 10 && tetromino.getSecond_grid_y() + 1< 19 && -1 < tetromino.getSecond_grid_x() - 2 && 0 < tetromino.getSecond_grid_y() + 1
                                && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y() < 19 && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() - 1 < 19 && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y() - 1
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 3, tetromino.getFirst_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() - 2, tetromino.getSecond_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y() - 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 3);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 2);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 2);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 3
                        else if(tetromino.getFirst_grid_x() < 10 && tetromino.getFirst_grid_y() + 2 < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y() + 2
                                && tetromino.getSecond_grid_x() + 1 < 10 && tetromino.getSecond_grid_y() + 1 < 19 && -1 < tetromino.getSecond_grid_x() + 1 && 0 < tetromino.getSecond_grid_y() + 1
                                && tetromino.getThird_grid_x() + 2 < 10 && tetromino.getThird_grid_y() < 19 && -1 < tetromino.getThird_grid_x() + 2 && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() + 3 < 10 && tetromino.getFourth_grid_y() - 1 < 19 && -1 < tetromino.getFourth_grid_x() + 3 && 0 < tetromino.getFourth_grid_y() - 1
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() + 1, tetromino.getSecond_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() + 2, tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() + 3, tetromino.getFourth_grid_y() - 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 2);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 2);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 3);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 4
                        else if(tetromino.getFirst_grid_x() - 3 < 10 && tetromino.getFirst_grid_y() < 19 && -1 < tetromino.getFirst_grid_x() - 3 && 0 < tetromino.getFirst_grid_y()
                                && tetromino.getSecond_grid_x() - 2 < 10 && tetromino.getSecond_grid_y() - 1 < 19 && -1 < tetromino.getSecond_grid_x() - 2 && 0 < tetromino.getSecond_grid_y() - 1
                                && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y() - 2 < 19 && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y() - 2
                                && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() - 3 < 19 && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y() - 3
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 3, tetromino.getFirst_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() - 2, tetromino.getSecond_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y() - 3) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 3);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y());
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 2);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 3);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 5
                        else if(tetromino.getFirst_grid_x() < 10 && tetromino.getFirst_grid_y() + 3 < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y() + 3
                                && tetromino.getSecond_grid_x() + 1 < 10 && tetromino.getSecond_grid_y() + 2 < 19 && -1 < tetromino.getSecond_grid_x() + 1 && 0 < tetromino.getSecond_grid_y() + 2
                                && tetromino.getThird_grid_x() + 2 < 10 && tetromino.getThird_grid_y() + 1 < 19 && -1 < tetromino.getThird_grid_x() + 2 && 0 < tetromino.getThird_grid_y() + 1
                                && tetromino.getFourth_grid_x() + 3 < 10 && tetromino.getFourth_grid_y() < 19 && -1 < tetromino.getFourth_grid_x() + 3 && 0 < tetromino.getFourth_grid_y()
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y() + 3) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() + 1, tetromino.getSecond_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() + 2, tetromino.getThird_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() + 3, tetromino.getFourth_grid_y()) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 3);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 2);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 2);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 1);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 3);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y());

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        else{
                            break;
                        }
                        break;
//--------------------------------------------------------------------------------------------------------------------------------------------------------------
                    case 3:
                        //Test 1
                        if(tetromino.getFirst_grid_x() + 1 < 10 && tetromino.getFirst_grid_y() - 2 < 19 && -1 < tetromino.getFirst_grid_x() + 1 && 0 < tetromino.getFirst_grid_y() - 2
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() - 1 < 19 && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y() - 1
                                && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y() < 19 && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() - 2 < 10 && tetromino.getFourth_grid_y() + 1 < 19 && -1 < tetromino.getFourth_grid_x() - 2 && 0 < tetromino.getFourth_grid_y() + 1
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 1, tetromino.getFirst_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() - 2, tetromino.getFourth_grid_y() + 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 2);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 2);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 2
                        else if(tetromino.getFirst_grid_x() + 3 < 10 && tetromino.getFirst_grid_y() - 2 < 19 && -1 < tetromino.getFirst_grid_x() + 3 && 0 < tetromino.getFirst_grid_y() - 2
                                && tetromino.getSecond_grid_x() + 2 < 10 && tetromino.getSecond_grid_y() - 1 < 19 && -1 < tetromino.getSecond_grid_x() + 2 && 0 < tetromino.getSecond_grid_y() - 1
                                && tetromino.getThird_grid_x() + 1 < 10 && tetromino.getThird_grid_y() < 19 && -1 < tetromino.getThird_grid_x() + 1 && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() + 1 < 19 && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y() + 1
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 3, tetromino.getFirst_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() + 2, tetromino.getSecond_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() + 1, tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y() + 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 3);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 2);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 2);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 3
                        else if(tetromino.getFirst_grid_x() < 10 && tetromino.getFirst_grid_y() - 2 < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y() - 2
                                && tetromino.getSecond_grid_x() - 1 < 10 && tetromino.getSecond_grid_y() - 1 < 19 && -1 < tetromino.getSecond_grid_x() - 1 && 0 < tetromino.getSecond_grid_y() - 1
                                && tetromino.getThird_grid_x() - 2 < 10 && tetromino.getThird_grid_y() < 19 && -1 < tetromino.getThird_grid_x() - 2 && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() - 3 < 10 && tetromino.getFourth_grid_y() + 1 < 19 && -1 < tetromino.getFourth_grid_x() - 3 && 0 < tetromino.getFourth_grid_y() + 1
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() - 1, tetromino.getSecond_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 2, tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() - 3, tetromino.getFourth_grid_y() + 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 2);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 2);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 3);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 4
                        else if(tetromino.getFirst_grid_x() + 3 < 10 && tetromino.getFirst_grid_y() - 3 < 19 && -1 < tetromino.getFirst_grid_x() + 3 && 0 < tetromino.getFirst_grid_y() - 3
                                && tetromino.getSecond_grid_x() + 2 < 10 && tetromino.getSecond_grid_y() - 2 < 19 && -1 < tetromino.getSecond_grid_x() + 2 && 0 < tetromino.getSecond_grid_y() - 2
                                && tetromino.getThird_grid_x() + 1 < 10 && tetromino.getThird_grid_y() - 1 < 19 && -1 < tetromino.getThird_grid_x() + 1 && 0 < tetromino.getThird_grid_y() - 1
                                && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() < 19 && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y()
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 3, tetromino.getFirst_grid_y() - 3) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() + 2, tetromino.getSecond_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() + 1, tetromino.getThird_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y()) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 3);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 3);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 2);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 2);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 1);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y());

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 5
                        else if(tetromino.getFirst_grid_x() < 10 && tetromino.getFirst_grid_y() < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y()
                                && tetromino.getSecond_grid_x() - 1 < 10 && tetromino.getSecond_grid_y() + 1 < 19 && -1 < tetromino.getSecond_grid_x() - 1 && 0 < tetromino.getSecond_grid_y() + 1
                                && tetromino.getThird_grid_x() - 2 < 10 && tetromino.getThird_grid_y() + 2 < 19 && -1 < tetromino.getThird_grid_x() - 2 && 0 < tetromino.getThird_grid_y() + 2
                                && tetromino.getFourth_grid_x() - 3 < 10 && tetromino.getFourth_grid_y() + 3 < 19 && -1 < tetromino.getFourth_grid_x() - 3 && 0 < tetromino.getFourth_grid_y() + 3
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() - 1, tetromino.getSecond_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 2, tetromino.getThird_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() - 3, tetromino.getFourth_grid_y() + 3) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y());
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 2);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 3);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 3);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        else{
                            break;
                        }
                        break;
//--------------------------------------------------------------------------------------------------------------------------------------------------------------
                    case 4:
                        //Test 1
                        if(tetromino.getFirst_grid_x() - 1 < 10 && tetromino.getFirst_grid_y() + 1 < 19 && -1 < tetromino.getFirst_grid_x() - 1 && 0 < tetromino.getFirst_grid_y() + 1
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() < 19 && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y()
                                && tetromino.getThird_grid_x() + 1 < 10 && tetromino.getThird_grid_y() - 1 < 19 && -1 < tetromino.getThird_grid_x() + 1 && 0 < tetromino.getThird_grid_y() - 1
                                && tetromino.getFourth_grid_x() + 2 < 10 && tetromino.getFourth_grid_y() - 2 < 19 && -1 < tetromino.getFourth_grid_x() + 2 && 0 < tetromino.getFourth_grid_y() - 2
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 1, tetromino.getFirst_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() + 1, tetromino.getThird_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() + 2, tetromino.getFourth_grid_y() - 2) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 1);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y());
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 1);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 2);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 2);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 2
                        else if(tetromino.getFirst_grid_x() < 10 && tetromino.getFirst_grid_y() + 1 < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y() + 1
                                && tetromino.getSecond_grid_x() + 1 < 10 && tetromino.getSecond_grid_y() < 19 && -1 < tetromino.getSecond_grid_x() + 1 && 0 < tetromino.getSecond_grid_y()
                                && tetromino.getThird_grid_x() + 2 < 10 && tetromino.getThird_grid_y() - 1 < 19 && -1 < tetromino.getThird_grid_x() + 2 && 0 < tetromino.getThird_grid_y() - 1
                                && tetromino.getFourth_grid_x() + 3 < 10 && tetromino.getFourth_grid_y() - 2 < 19 && -1 < tetromino.getFourth_grid_x() + 3 && 0 < tetromino.getFourth_grid_y() - 2
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() + 1, tetromino.getSecond_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() + 2, tetromino.getThird_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() + 3, tetromino.getFourth_grid_y() - 2) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 1);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y());
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 2);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 1);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 3);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 2);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 3
                        else if(tetromino.getFirst_grid_x() - 3 < 10 && tetromino.getFirst_grid_y() + 1 < 19 && -1 < tetromino.getFirst_grid_x() - 3 && 0 < tetromino.getFirst_grid_y() + 1
                                && tetromino.getSecond_grid_x() - 2 < 10 && tetromino.getSecond_grid_y() < 19 && -1 < tetromino.getSecond_grid_x() - 2 && 0 < tetromino.getSecond_grid_y()
                                && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y() - 1 < 19 && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y() - 1
                                && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() - 2 < 19 && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y() - 2
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 3, tetromino.getFirst_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() - 2, tetromino.getSecond_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y() - 2) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 3);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 1);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 2);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y());
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 1);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 2);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 4
                        else if(tetromino.getFirst_grid_x() < 10 && tetromino.getFirst_grid_y() + 3 < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y() + 3
                                && tetromino.getSecond_grid_x() + 1 < 10 && tetromino.getSecond_grid_y() + 2 < 19 && -1 < tetromino.getSecond_grid_x() + 1 && 0 < tetromino.getSecond_grid_y() + 2
                                && tetromino.getThird_grid_x() + 2 < 10 && tetromino.getThird_grid_y() + 1 < 19 && -1 < tetromino.getThird_grid_x() + 2 && 0 < tetromino.getThird_grid_y() + 1
                                && tetromino.getFourth_grid_x() + 3 < 10 && tetromino.getFourth_grid_y() < 19 && -1 < tetromino.getFourth_grid_x() + 3 && 0 < tetromino.getFourth_grid_y()
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y() + 3) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() + 1, tetromino.getSecond_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() + 2, tetromino.getThird_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() + 3, tetromino.getFourth_grid_y()) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 3);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 2);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 2);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 1);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 3);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y());

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 5
                        else if(tetromino.getFirst_grid_x() - 3 < 10 && tetromino.getFirst_grid_y() < 19 && -1 < tetromino.getFirst_grid_x() - 3 && 0 < tetromino.getFirst_grid_y()
                                && tetromino.getSecond_grid_x() - 2 < 10 && tetromino.getSecond_grid_y() - 1 < 19 && -1 < tetromino.getSecond_grid_x() - 2 && 0 < tetromino.getSecond_grid_y() - 1
                                && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y() - 2 < 19 && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y() - 2
                                && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() - 3 < 19 && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y() - 3
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 3, tetromino.getFirst_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() - 2, tetromino.getSecond_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y() - 3) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 3);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y());
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 2);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 3);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        else{
                            break;
                        }
                        break;
                }
                break;
            case "S":
                switch(tetromino.getForm()){
                    case 1:
                        //Test 1
                        if(tetromino.getFirst_grid_x() < 10 && tetromino.getFirst_grid_y() + 2 < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y() + 2
                                && tetromino.getSecond_grid_x() + 1 < 10 && tetromino.getSecond_grid_y() + 1 < 19  && -1 < tetromino.getSecond_grid_x() + 1 && 0 < tetromino.getSecond_grid_y() + 1
                                && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() < 19  && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() + 1 < 10 && tetromino.getFourth_grid_y() - 1 < 19  && -1 < tetromino.getFourth_grid_x() + 1 && 0 < tetromino.getFourth_grid_y() - 1
                                 
                                && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() + 1, tetromino.getSecond_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() + 1, tetromino.getFourth_grid_y() - 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 2);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 1);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 2
                        else if(tetromino.getFirst_grid_x() - 1 < 10 && tetromino.getFirst_grid_y() + 2 < 19 && -1 < tetromino.getFirst_grid_x() - 1 && 0 < tetromino.getFirst_grid_y() + 2
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() + 1 < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y() + 1
                                && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y() < 19  && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() - 1 < 19  && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y() - 1

                                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 1, tetromino.getFirst_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y() - 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 2);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 3
                        else if(tetromino.getFirst_grid_x() - 1 < 10 && tetromino.getFirst_grid_y() + 1 < 19 && -1 < tetromino.getFirst_grid_x() - 1 && 0 < tetromino.getFirst_grid_y() + 1
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y()
                                && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y() - 1 < 19  && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y() - 1
                                && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() - 2 < 19  && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y() - 2

                                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 1, tetromino.getFirst_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y() - 2) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 1);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y());
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 1);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 2);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 4
                        else if(tetromino.getFirst_grid_x() < 10 && tetromino.getFirst_grid_y() + 4 < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y() + 4
                                && tetromino.getSecond_grid_x() + 1 < 10 && tetromino.getSecond_grid_y() + 3 < 19  && -1 < tetromino.getSecond_grid_x() + 1 && 0 < tetromino.getSecond_grid_y() + 3
                                && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() + 2 < 19  && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y() + 2
                                && tetromino.getFourth_grid_x() + 1 < 10 && tetromino.getFourth_grid_y() + 1 < 19  && -1 < tetromino.getFourth_grid_x() + 1 && 0 < tetromino.getFourth_grid_y() + 1

                                && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y() + 4) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() + 1, tetromino.getSecond_grid_y() + 3) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() + 1, tetromino.getFourth_grid_y() + 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 4);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 3);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 1);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 5 
                        else if(tetromino.getFirst_grid_x() - 1 < 10 && tetromino.getFirst_grid_y() + 4 < 19 && -1 < tetromino.getFirst_grid_x() - 1 && 0 < tetromino.getFirst_grid_y() + 4
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() + 3 < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y() + 3
                                && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y() + 2 < 19  && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y() + 2
                                && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() + 1 < 19  && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y() + 1

                                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 1, tetromino.getFirst_grid_y() + 4) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y() + 3) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y() + 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 4);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 3);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        else{
                            break;
                        }
                        break;
//--------------------------------------------------------------------------------------------------------------------------------------------------------------
                    case 2:
                        //Test 1
                        if(tetromino.getFirst_grid_x() - 2 < 10 && tetromino.getFirst_grid_y() < 19 && -1 < tetromino.getFirst_grid_x() - 2 && 0 < tetromino.getFirst_grid_y()
                               && tetromino.getSecond_grid_x() - 1 < 10 && tetromino.getSecond_grid_y() + 1 < 19  && -1 < tetromino.getSecond_grid_x() - 1 && 0 < tetromino.getSecond_grid_y() + 1
                                && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() < 19  && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() + 1 < 10 && tetromino.getFourth_grid_y() + 1 < 19  && -1 < tetromino.getFourth_grid_x() + 1 && 0 < tetromino.getFourth_grid_y() + 1

                                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 2, tetromino.getFirst_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() - 1, tetromino.getSecond_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() + 1, tetromino.getFourth_grid_y() + 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 2);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y());
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 1);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 2
                        else if(tetromino.getFirst_grid_x() - 1 < 10 && tetromino.getFirst_grid_y() < 19 && -1 < tetromino.getFirst_grid_x() - 1 && 0 < tetromino.getFirst_grid_y()
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() + 1 < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y() + 1
                                && tetromino.getThird_grid_x() + 1 < 10 && tetromino.getThird_grid_y() < 19  && -1 < tetromino.getThird_grid_x() + 1 && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() + 2 < 10 && tetromino.getFourth_grid_y() + 1 < 19  && -1 < tetromino.getFourth_grid_x() + 2 && 0 < tetromino.getFourth_grid_y() + 1

                                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 1, tetromino.getFirst_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() + 1, tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() + 2, tetromino.getFourth_grid_y() + 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y());
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 2);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 3
                        else if(tetromino.getFirst_grid_x() - 1 < 10 && tetromino.getFirst_grid_y() + 1 < 19 && -1 < tetromino.getFirst_grid_x() - 1 && 0 < tetromino.getFirst_grid_y() + 1
                               && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() + 2 < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y() + 2
                                && tetromino.getThird_grid_x() + 1 < 10 && tetromino.getThird_grid_y() + 1 < 19  && -1 < tetromino.getThird_grid_x() + 1 && 0 < tetromino.getThird_grid_y() + 1
                                && tetromino.getFourth_grid_x() + 2 < 10 && tetromino.getFourth_grid_y() + 2 < 19  && -1 < tetromino.getFourth_grid_x() + 2 && 0 < tetromino.getFourth_grid_y() + 2

                                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 1, tetromino.getFirst_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() + 1, tetromino.getThird_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() + 2, tetromino.getFourth_grid_y() + 2) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 1);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 2);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 1);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 2);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 2);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 4
                        else if(tetromino.getFirst_grid_x() - 2 < 10 && tetromino.getFirst_grid_y() - 2 < 19 && -1 < tetromino.getFirst_grid_x() - 2 && 0 < tetromino.getFirst_grid_y() - 2
                                && tetromino.getSecond_grid_x() - 1 < 10 && tetromino.getSecond_grid_y() - 1 < 19  && -1 < tetromino.getSecond_grid_x() - 1 && 0 < tetromino.getSecond_grid_y() - 1
                                && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() - 2 < 19  && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y() - 2
                                && tetromino.getFourth_grid_x() + 1 < 10 && tetromino.getFourth_grid_y() - 1 < 19  && -1 < tetromino.getFourth_grid_x() + 1 && 0 < tetromino.getFourth_grid_y() - 1

                                && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y()) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 2);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 2);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 1);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 5
                        else if(tetromino.getFirst_grid_x() - 1 < 10 && tetromino.getFirst_grid_y() - 2 < 19 && -1 < tetromino.getFirst_grid_x() - 1 && 0 < tetromino.getFirst_grid_y() - 2
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() - 1 < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y() - 1
                                && tetromino.getThird_grid_x() + 1 < 10 && tetromino.getThird_grid_y() - 2 < 19  && -1 < tetromino.getThird_grid_x() + 1 && 0 < tetromino.getThird_grid_y() - 2
                                && tetromino.getFourth_grid_x() + 2 < 10 && tetromino.getFourth_grid_y() - 1 < 19  && -1 < tetromino.getFourth_grid_x() + 2 && 0 < tetromino.getFourth_grid_y() - 1

                                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 1, tetromino.getFirst_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() + 1, tetromino.getThird_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() + 2, tetromino.getFourth_grid_y() -1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 2);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() -1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 2);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        else{
                            break;
                        }
                        break;
//--------------------------------------------------------------------------------------------------------------------------------------------------------------
                    case 3:
                        //Test 1
                        if(tetromino.getFirst_grid_x() < 10 && tetromino.getFirst_grid_y() - 2 < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y() - 2
                                && tetromino.getSecond_grid_x() - 1 < 10 && tetromino.getSecond_grid_y() - 1 < 19  && -1 < tetromino.getSecond_grid_x() - 1 && 0 < tetromino.getSecond_grid_y() - 1
                                && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() < 19  && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() - 1 < 10 && tetromino.getFourth_grid_y() + 1 < 19  && -1 < tetromino.getFourth_grid_x() - 1 && 0 < tetromino.getFourth_grid_y() + 1

                                && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() - 1, tetromino.getSecond_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() - 1, tetromino.getFourth_grid_y() + 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 2);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 1);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 2
                        else if(tetromino.getFirst_grid_x() + 1 < 10 && tetromino.getFirst_grid_y() - 2 < 19 && -1 < tetromino.getFirst_grid_x() + 1 && 0 < tetromino.getFirst_grid_y() - 2
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() - 1 < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y() - 1
                                && tetromino.getThird_grid_x() + 1 < 10 && tetromino.getThird_grid_y() < 19  && -1 < tetromino.getThird_grid_x() + 1 && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() + 1 < 19  && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y() + 1

                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 1, tetromino.getFirst_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() + 1, tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y() + 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 2);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 3
                        else if(tetromino.getFirst_grid_x() + 1 < 10 && tetromino.getFirst_grid_y() - 3 < 19 && -1 < tetromino.getFirst_grid_x() + 1 && 0 < tetromino.getFirst_grid_y() - 3
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() - 2 < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y() - 2
                                && tetromino.getThird_grid_x() + 1 < 10 && tetromino.getThird_grid_y() - 1 < 19  && -1 < tetromino.getThird_grid_x() + 1 && 0 < tetromino.getThird_grid_y() - 1
                                && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() < 19  && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y()

                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 1, tetromino.getFirst_grid_y() - 3) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() + 1, tetromino.getThird_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y()) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 3);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 2);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 1);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y());

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 4
                        else if(tetromino.getFirst_grid_x() < 10 && tetromino.getFirst_grid_y() < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y()
                               && tetromino.getSecond_grid_x() - 1 < 10 && tetromino.getSecond_grid_y() + 1 < 19  && -1 < tetromino.getSecond_grid_x() - 1 && 0 < tetromino.getSecond_grid_y() + 1
                                && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() + 2 < 19  && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y() + 2
                                && tetromino.getFourth_grid_x() - 1 < 10 && tetromino.getFourth_grid_y() + 3 < 19  && -1 < tetromino.getFourth_grid_x() - 1 && 0 < tetromino.getFourth_grid_y() + 3

                                && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() - 1, tetromino.getSecond_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() - 1, tetromino.getFourth_grid_y() + 3) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y());
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 1);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 3);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 5
                        else if(tetromino.getFirst_grid_x() + 1 < 10 && tetromino.getFirst_grid_y() < 19 && -1 < tetromino.getFirst_grid_x() + 1 && 0 < tetromino.getFirst_grid_y()
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() + 1 < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y() + 1
                                && tetromino.getThird_grid_x() + 1 < 10 && tetromino.getThird_grid_y() + 2 < 19  && -1 < tetromino.getThird_grid_x() + 1 && 0 < tetromino.getThird_grid_y() + 2
                                && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y()  + 3 < 19  && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y() + 3

                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 1, tetromino.getFirst_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() + 1, tetromino.getThird_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y() + 3) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y());
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 3);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        else{
                            break;
                        }
                        break;
//--------------------------------------------------------------------------------------------------------------------------------------------------------------
                    case 4:
                        //Test 1
                        if(tetromino.getFirst_grid_x() + 2 < 10 && tetromino.getFirst_grid_y() < 19 && -1 < tetromino.getFirst_grid_x() + 2 && 0 < tetromino.getFirst_grid_y()
                               && tetromino.getSecond_grid_x() + 1 < 10 && tetromino.getSecond_grid_y() - 1 < 19  && -1 < tetromino.getSecond_grid_x() + 1 && 0 < tetromino.getSecond_grid_y() - 1
                                && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() < 19  && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() - 1 < 10 && tetromino.getFourth_grid_y() - 1 < 19  && -1 < tetromino.getFourth_grid_x() - 1 && 0 < tetromino.getFourth_grid_y() - 1

                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 2, tetromino.getFirst_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() + 1, tetromino.getSecond_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() - 1, tetromino.getFourth_grid_y() - 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 2);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y());
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 1);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 2
                        else if(tetromino.getFirst_grid_x() + 1 < 10 && tetromino.getFirst_grid_y() < 19 && -1 < tetromino.getFirst_grid_x() + 1 && 0 < tetromino.getFirst_grid_y()
                               && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() - 1 < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y() - 1
                                && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y() < 19  && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() - 2 < 10 && tetromino.getFourth_grid_y() - 1 < 19  && -1 < tetromino.getFourth_grid_x() - 2 && 0 < tetromino.getFourth_grid_y() - 1

                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 1, tetromino.getFirst_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() - 2, tetromino.getFourth_grid_y() - 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y());
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 2);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 3
                        else if(tetromino.getFirst_grid_x() + 1 < 10 && tetromino.getFirst_grid_y() + 1 < 19 && -1 < tetromino.getFirst_grid_x() + 1 && 0 < tetromino.getFirst_grid_y() + 1
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y()
                                && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y() + 1 < 19  && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y() + 1
                                && tetromino.getFourth_grid_x() - 2 < 10 && tetromino.getFourth_grid_y() < 19  && -1 < tetromino.getFourth_grid_x() - 2 && 0 < tetromino.getFourth_grid_y()

                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 1, tetromino.getFirst_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() - 2, tetromino.getFourth_grid_y()) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 1);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y());
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 1);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 2);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y());

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 4
                        else if(tetromino.getFirst_grid_x() + 2 < 10 && tetromino.getFirst_grid_y() - 2 < 19 && -1 < tetromino.getFirst_grid_x() + 2 && 0 < tetromino.getFirst_grid_y() - 2
                               && tetromino.getSecond_grid_x() + 1 < 10 && tetromino.getSecond_grid_y() - 3 < 19  && -1 < tetromino.getSecond_grid_x() + 1 && 0 < tetromino.getSecond_grid_y() - 3
                                && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() - 2 < 19  && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y() - 2
                                && tetromino.getFourth_grid_x() - 1 < 10 && tetromino.getFourth_grid_y() - 3 < 19  && -1 < tetromino.getFourth_grid_x() - 1 && 0 < tetromino.getFourth_grid_y() - 3

                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 2, tetromino.getFirst_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() + 1, tetromino.getSecond_grid_y() - 3) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() - 1, tetromino.getFourth_grid_y() - 3 ) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 2);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 2);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 3);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 1);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 3);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 5
                        else if(tetromino.getFirst_grid_x() + 1 < 10 && tetromino.getFirst_grid_y() - 2 < 19 && -1 < tetromino.getFirst_grid_x() + 1 && 0 < tetromino.getFirst_grid_y() - 2
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() - 3 < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y() - 3
                                && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y() - 2 < 19  && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y() - 2
                                && tetromino.getFourth_grid_x() - 2 < 10 && tetromino.getFourth_grid_y() - 3 < 19  && -1 < tetromino.getFourth_grid_x() - 2 && 0 < tetromino.getFourth_grid_y() - 3

                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 1, tetromino.getFirst_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y() - 3) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() - 2, tetromino.getFourth_grid_y() - 3) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 2);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 3);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 2);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 3);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        else{
                            break;
                        }
                        break;
                }
                break;
            case "Z":
                switch(tetromino.getForm()){
                    case 1:
                        //Test 1
                        if(tetromino.getFirst_grid_x() + 2 < 10 && tetromino.getFirst_grid_y() < 19 && -1 < tetromino.getFirst_grid_x() + 2 && 0 < tetromino.getFirst_grid_y()
                                && tetromino.getSecond_grid_x() + 1 < 10 && tetromino.getSecond_grid_y() + 1 < 19  && -1 < tetromino.getSecond_grid_x() + 1 && 0 < tetromino.getSecond_grid_y() + 1
                                && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() < 19  && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() - 1 < 10 && tetromino.getFourth_grid_y() + 1 < 19  && -1 < tetromino.getFourth_grid_x() - 1 && 0 < tetromino.getFourth_grid_y() + 1
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 2, tetromino.getFirst_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() + 1, tetromino.getSecond_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() - 1, tetromino.getFourth_grid_y() + 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 2);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y());
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 1);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 2
                        else if(tetromino.getFirst_grid_x() + 1 < 10 && tetromino.getFirst_grid_y() < 19 && -1 < tetromino.getFirst_grid_x() + 1 && 0 < tetromino.getFirst_grid_y()
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() + 1 < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y() + 1
                                && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y() < 19  && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() - 2 < 10 && tetromino.getFourth_grid_y() + 1 < 19  && -1 < tetromino.getFourth_grid_x() - 2 && 0 < tetromino.getFourth_grid_y() + 1

                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 1, tetromino.getFirst_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() - 2, tetromino.getFourth_grid_y() + 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y());
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 2);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 3
                        else if(tetromino.getFirst_grid_x() + 1 < 10 && tetromino.getFirst_grid_y() - 1 < 19 && -1 < tetromino.getFirst_grid_x() + 1 && 0 < tetromino.getFirst_grid_y() - 1
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y()
                                && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y() - 1 < 19  && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y() - 1
                                && tetromino.getFourth_grid_x() - 2 < 10 && tetromino.getFourth_grid_y() < 19  && -1 < tetromino.getFourth_grid_x() - 2 && 0 < tetromino.getFourth_grid_y()

                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 1, tetromino.getFirst_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() - 2, tetromino.getFourth_grid_y()) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 1);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y());
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 1);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 2);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y());

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 4
                        else if(tetromino.getFirst_grid_x() + 2 < 10 && tetromino.getFirst_grid_y() + 2 < 19 && -1 < tetromino.getFirst_grid_x() + 2 && 0 < tetromino.getFirst_grid_y() + 2
                                && tetromino.getSecond_grid_x() + 1 < 10 && tetromino.getSecond_grid_y() + 3 < 19  && -1 < tetromino.getSecond_grid_x() + 1 && 0 < tetromino.getSecond_grid_y() + 3
                                && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y() + 2 < 19  && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y() + 2
                                && tetromino.getFourth_grid_x() - 2 < 10 && tetromino.getFourth_grid_y() + 3 < 19  && -1 < tetromino.getFourth_grid_x() - 2 && 0 < tetromino.getFourth_grid_y() + 3

                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 2, tetromino.getFirst_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() + 1, tetromino.getSecond_grid_y() + 3) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() -2, tetromino.getFourth_grid_y() + 3) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 2);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 2);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 3);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 2);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 3);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 5 
                        else if(tetromino.getFirst_grid_x() + 1 < 10 && tetromino.getFirst_grid_y() + 2 < 19 && -1 < tetromino.getFirst_grid_x() + 1 && 0 < tetromino.getFirst_grid_y() + 2
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() + 3 < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y() + 3
                                && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y() + 2 < 19  && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y() + 2
                                && tetromino.getFourth_grid_x() - 2 < 10 && tetromino.getFourth_grid_y() + 3 < 19  && -1 < tetromino.getFourth_grid_x() - 2 && 0 < tetromino.getFourth_grid_y() + 3

                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 1, tetromino.getFirst_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y() + 3) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() - 2, tetromino.getFourth_grid_y() + 3) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 2);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 3);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 2);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 3);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        else{
                            break;
                        }
                        break;
//--------------------------------------------------------------------------------------------------------------------------------------------------------------
                    case 2:
                        //Test 1
                        if(tetromino.getFirst_grid_x() < 10 && tetromino.getFirst_grid_y() + 2 < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y() + 2
                               && tetromino.getSecond_grid_x() - 1 < 10 && tetromino.getSecond_grid_y() + 1 < 19  && -1 < tetromino.getSecond_grid_x() - 1 && 0 < tetromino.getSecond_grid_y() + 1
                                && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() < 19  && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() - 1 < 10 && tetromino.getFourth_grid_y() - 1 < 19  && -1 < tetromino.getFourth_grid_x() - 1 && 0 < tetromino.getFourth_grid_y() - 1

                                && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() - 1, tetromino.getSecond_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() - 1, tetromino.getFourth_grid_y() - 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 2);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 1);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 2
                        else if(tetromino.getFirst_grid_x() + 1 < 10 && tetromino.getFirst_grid_y() + 2 < 19 && -1 < tetromino.getFirst_grid_x() + 1 && 0 < tetromino.getFirst_grid_y() + 2
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() + 1 < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y() + 1
                                && tetromino.getThird_grid_x() + 1 < 10 && tetromino.getThird_grid_y() < 19  && -1 < tetromino.getThird_grid_x() + 1 && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() - 1 < 19  && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y() - 1

                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 1, tetromino.getFirst_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() + 1, tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y() - 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 2);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 3
                        else if(tetromino.getFirst_grid_x() + 1 < 10 && tetromino.getFirst_grid_y() + 2 < 19 && -1 < tetromino.getFirst_grid_x() + 1 && 0 < tetromino.getFirst_grid_y() + 2
                               && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() + 2 < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y() + 2
                                && tetromino.getThird_grid_x() + 1 < 10 && tetromino.getThird_grid_y() + 1 < 19  && -1 < tetromino.getThird_grid_x() + 1 && 0 < tetromino.getThird_grid_y() + 1
                                && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() < 19  && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y()

                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 1, tetromino.getFirst_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() + 1, tetromino.getThird_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y()) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 2);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 2);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 1);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y());

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 4
                        else if(tetromino.getFirst_grid_x() < 10 && tetromino.getFirst_grid_y() < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y()
                                && tetromino.getSecond_grid_x() - 1 < 10 && tetromino.getSecond_grid_y() - 1 < 19  && -1 < tetromino.getSecond_grid_x() - 1 && 0 < tetromino.getSecond_grid_y() - 1
                                && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() - 2 < 19  && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y() - 2
                                && tetromino.getFourth_grid_x() - 1 < 10 && tetromino.getFourth_grid_y() - 3 < 19  && -1 < tetromino.getFourth_grid_x() - 1 && 0 < tetromino.getFourth_grid_y() - 3

                                && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() - 1, tetromino.getSecond_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() - 1, tetromino.getFourth_grid_y() - 3) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y());
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 1);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 3);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 5
                        else if(tetromino.getFirst_grid_x() + 1 < 10 && tetromino.getFirst_grid_y() < 19 && -1 < tetromino.getFirst_grid_x() + 1 && 0 < tetromino.getFirst_grid_y()
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() - 1 < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y() - 1
                                && tetromino.getThird_grid_x() + 1 < 10 && tetromino.getThird_grid_y() - 2 < 19  && -1 < tetromino.getThird_grid_x() + 1 && 0 < tetromino.getThird_grid_y() - 2
                                && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() - 3 < 19  && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y() - 3

                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 1, tetromino.getFirst_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() + 1, tetromino.getThird_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y() - 3) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y());
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 3);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        else{
                            break;
                        }
                        break;
//--------------------------------------------------------------------------------------------------------------------------------------------------------------
                    case 3:
                        //Test 1
                        if(tetromino.getFirst_grid_x() - 2 < 10 && tetromino.getFirst_grid_y() < 19 && -1 < tetromino.getFirst_grid_x() - 2 && 0 < tetromino.getFirst_grid_y()
                                && tetromino.getSecond_grid_x() - 1 < 10 && tetromino.getSecond_grid_y() - 1 < 19  && -1 < tetromino.getSecond_grid_x() - 1 && 0 < tetromino.getSecond_grid_y() - 1
                                && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() < 19  && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() + 1 < 10 && tetromino.getFourth_grid_y() - 1 < 19  && -1 < tetromino.getFourth_grid_x() + 1 && 0 < tetromino.getFourth_grid_y() - 1

                                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 2, tetromino.getFirst_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() - 1, tetromino.getSecond_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() + 1, tetromino.getFourth_grid_y() - 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 2);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y());
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 1);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 2
                        else if(tetromino.getFirst_grid_x() - 1 < 10 && tetromino.getFirst_grid_y() < 19 && -1 < tetromino.getFirst_grid_x() - 1 && 0 < tetromino.getFirst_grid_y()
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() - 1 < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y() - 1
                                && tetromino.getThird_grid_x() + 1 < 10 && tetromino.getThird_grid_y() < 19  && -1 < tetromino.getThird_grid_x() + 1 && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() + 2 < 10 && tetromino.getFourth_grid_y() - 1 < 19  && -1 < tetromino.getFourth_grid_x() + 2 && 0 < tetromino.getFourth_grid_y() - 1

                                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 1, tetromino.getFirst_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() + 1, tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() + 2, tetromino.getFourth_grid_y() - 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y());
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 2);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 3
                        else if(tetromino.getFirst_grid_x() - 1 < 10 && tetromino.getFirst_grid_y() - 1 < 19 && -1 < tetromino.getFirst_grid_x() - 1 && 0 < tetromino.getFirst_grid_y() - 1
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() - 2 < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y() - 2
                                && tetromino.getThird_grid_x() + 1 < 10 && tetromino.getThird_grid_y() - 1 < 19  && -1 < tetromino.getThird_grid_x() + 1 && 0 < tetromino.getThird_grid_y() - 1
                                && tetromino.getFourth_grid_x() + 2 < 10 && tetromino.getFourth_grid_y() - 2 < 19  && -1 < tetromino.getFourth_grid_x() + 2 && 0 < tetromino.getFourth_grid_y() - 2

                                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 1, tetromino.getFirst_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() + 1, tetromino.getThird_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() + 2, tetromino.getFourth_grid_y() - 2) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 1);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 2);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 1);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 2);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 2);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 4
                        else if(tetromino.getFirst_grid_x() - 2 < 10 && tetromino.getFirst_grid_y() + 2 < 19 && -1 < tetromino.getFirst_grid_x() - 2 && 0 < tetromino.getFirst_grid_y() + 2
                               && tetromino.getSecond_grid_x() - 1 < 10 && tetromino.getSecond_grid_y() + 1 < 19  && -1 < tetromino.getSecond_grid_x() - 1 && 0 < tetromino.getSecond_grid_y() + 1
                                && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() + 2 < 19  && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y() + 2
                                && tetromino.getFourth_grid_x() + 1 < 10 && tetromino.getFourth_grid_y() + 1 < 19  && -1 < tetromino.getFourth_grid_x() + 1 && 0 < tetromino.getFourth_grid_y() + 1

                                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 2, tetromino.getFirst_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() - 1, tetromino.getSecond_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() + 1, tetromino.getFourth_grid_y() + 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 2);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 2);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 1);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 5
                        else if(tetromino.getFirst_grid_x() - 1 < 10 && tetromino.getFirst_grid_y() + 2 < 19 && -1 < tetromino.getFirst_grid_x() - 1 && 0 < tetromino.getFirst_grid_y() + 2
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() + 1 < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y() + 1
                                && tetromino.getThird_grid_x() + 1 < 10 && tetromino.getThird_grid_y() + 2 < 19  && -1 < tetromino.getThird_grid_x() + 1 && 0 < tetromino.getThird_grid_y() + 2
                                && tetromino.getFourth_grid_x() + 2 < 10 && tetromino.getFourth_grid_y() + 1 < 19  && -1 < tetromino.getFourth_grid_x() + 2 && 0 < tetromino.getFourth_grid_y() + 1

                                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 1, tetromino.getFirst_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() + 1, tetromino.getThird_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() + 2, tetromino.getFourth_grid_y() + 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 2);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 2);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        else{
                            break;
                        }
                        break;
//--------------------------------------------------------------------------------------------------------------------------------------------------------------
                    case 4:
                        //Test 1
                        if(tetromino.getFirst_grid_x() < 10 && tetromino.getFirst_grid_y() - 2 < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y() - 2
                               && tetromino.getSecond_grid_x() + 1 < 10 && tetromino.getSecond_grid_y() - 1 < 19  && -1 < tetromino.getSecond_grid_x() + 1 && 0 < tetromino.getSecond_grid_y() - 1
                                && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() < 19  && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() + 1 < 10 && tetromino.getFourth_grid_y() + 1 < 19  && -1 < tetromino.getFourth_grid_x() + 1 && 0 < tetromino.getFourth_grid_y() + 1

                                && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() + 1, tetromino.getSecond_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() + 1, tetromino.getFourth_grid_y() + 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 2);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 1);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 2
                        else if(tetromino.getFirst_grid_x() - 1 < 10 && tetromino.getFirst_grid_y() - 2 < 19 && -1 < tetromino.getFirst_grid_x() - 1 && 0 < tetromino.getFirst_grid_y() - 2
                               && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() - 1 < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y() - 1
                                && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y() < 19  && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() + 1 < 19  && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y() + 1

                                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 1, tetromino.getFirst_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y() + 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 2);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 3
                        else if(tetromino.getFirst_grid_x() - 1 < 10 && tetromino.getFirst_grid_y() - 1 < 19 && -1 < tetromino.getFirst_grid_x() - 1 && 0 < tetromino.getFirst_grid_y() - 1
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y()
                                && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y() + 1 < 19  && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y() + 1
                                && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() + 2 < 19  && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y() + 2

                                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 1, tetromino.getFirst_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y() + 2) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 1);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y());
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 1);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 2);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 4
                        else if(tetromino.getFirst_grid_x() < 10 && tetromino.getFirst_grid_y() - 4 < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y() - 4
                               && tetromino.getSecond_grid_x() + 1 < 10 && tetromino.getSecond_grid_y() - 3 < 19  && -1 < tetromino.getSecond_grid_x() + 1 && 0 < tetromino.getSecond_grid_y() - 3
                                && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() - 2 < 19  && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y() - 2
                                && tetromino.getFourth_grid_x() + 1 < 10 && tetromino.getFourth_grid_y() - 1 < 19  && -1 < tetromino.getFourth_grid_x() + 1 && 0 < tetromino.getFourth_grid_y() - 1

                                && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y() - 4) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() + 1, tetromino.getSecond_grid_y() - 3) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() + 1, tetromino.getFourth_grid_y() - 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 4);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 3);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 1);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 5
                        else if(tetromino.getFirst_grid_x() - 1 < 10 && tetromino.getFirst_grid_y() - 4 < 19 && -1 < tetromino.getFirst_grid_x() - 1 && 0 < tetromino.getFirst_grid_y() - 4
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() - 3 < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y() - 3
                                && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y() - 2 < 19  && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y() - 2
                                && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() - 1 < 19  && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y() - 1

                                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 1, tetromino.getFirst_grid_y() - 4) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y() - 3) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y() - 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 4);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 3);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        else{
                            break;
                        }
                        break;
                }
                break;
                
            default:
                rotation_2(tetromino);
                break;
            }
        
        }
    }
    
    public void rotation_2(Tetrominos_Shape tetromino){
        if(getIsGameOver() == false && getIsPaused() == false){
            switch(tetromino.getName()){
            case "L":
                switch(tetromino.getForm()){
//--------------------------------------------------------------------------------------------------------------------------------------------------------------
                    case 1:
                        //Test 1
                        if(tetromino.getFirst_grid_x() < 10 && tetromino.getFirst_grid_y() + 2 < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y() + 2
                                && tetromino.getSecond_grid_x() - 1 < 10 && tetromino.getSecond_grid_y() + 1 < 19  && -1 < tetromino.getSecond_grid_x() - 1 && 0 < tetromino.getSecond_grid_y() + 1
                                && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() < 19  && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() + 1 < 10 && tetromino.getFourth_grid_y() - 1 < 19  && -1 < tetromino.getFourth_grid_x() + 1 && 0 < tetromino.getFourth_grid_y() - 1
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() - 1, tetromino.getSecond_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() + 1, tetromino.getFourth_grid_y() - 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 2);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 1);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 2
                        else if(tetromino.getFirst_grid_x() - 1 < 10 && tetromino.getFirst_grid_y() + 2 < 19 && -1 < tetromino.getFirst_grid_x() - 1 && 0 < tetromino.getFirst_grid_y() + 2
                                && tetromino.getSecond_grid_x() - 2 < 10 && tetromino.getSecond_grid_y() + 1 < 19  && -1 < tetromino.getSecond_grid_x() - 2 && 0 < tetromino.getSecond_grid_y() + 1
                                && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y() < 19  && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() - 1 < 19  && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y() - 1
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 1, tetromino.getFirst_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() - 2, tetromino.getSecond_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y() - 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 2);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 2);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 3
                        else if(tetromino.getFirst_grid_x() - 1 < 10 && tetromino.getFirst_grid_y() + 1 < 19 && -1 < tetromino.getFirst_grid_x() - 1 && 0 < tetromino.getFirst_grid_y() + 1
                                && tetromino.getSecond_grid_x() - 2 < 10 && tetromino.getSecond_grid_y() < 19  && -1 < tetromino.getSecond_grid_x() - 2 && 0 < tetromino.getSecond_grid_y()
                                && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y() - 1 < 19  && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y() - 1
                                && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() - 2< 19  && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y() - 2
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 1, tetromino.getFirst_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() - 2, tetromino.getSecond_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y() - 2) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 1);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 2);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y());
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 1);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 2);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 4
                        else if(tetromino.getFirst_grid_x() < 10 && tetromino.getFirst_grid_y() + 4 < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y() + 4
                                && tetromino.getSecond_grid_x() - 1 < 10 && tetromino.getSecond_grid_y() + 3 < 19  && -1 < tetromino.getSecond_grid_x() - 1 && 0 < tetromino.getSecond_grid_y() + 3
                                && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() + 2 < 19  && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y() + 2
                                && tetromino.getFourth_grid_x() + 1 < 10 && tetromino.getFourth_grid_y() + 1 < 19  && -1 < tetromino.getFourth_grid_x() + 1 && 0 < tetromino.getFourth_grid_y() + 1
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y() + 4) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() - 1, tetromino.getSecond_grid_y() + 3) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() + 1, tetromino.getFourth_grid_y() + 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 4);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 3);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 1);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 5 
                        else if(tetromino.getFirst_grid_x() - 1 < 10 && tetromino.getFirst_grid_y() + 4 < 19 && -1 < tetromino.getFirst_grid_x() - 1 && 0 < tetromino.getFirst_grid_y() + 4
                                && tetromino.getSecond_grid_x() - 2 < 10 && tetromino.getSecond_grid_y() + 3 < 19  && -1 < tetromino.getSecond_grid_x() - 2 && 0 < tetromino.getSecond_grid_y() + 3
                                && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y() + 2 < 19  && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y() + 2
                                && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() + 1 < 19  && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y() + 1
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 1, tetromino.getFirst_grid_y() + 4) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() - 2, tetromino.getSecond_grid_y() + 3) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y() + 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 4);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 2);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 3);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        else{
                            break;
                        }
                        break;
//--------------------------------------------------------------------------------------------------------------------------------------------------------------
                    case 2:
                        //Test 1
                        if(tetromino.getFirst_grid_x() - 2 < 10 && tetromino.getFirst_grid_y() < 19 && -1 < tetromino.getFirst_grid_x() - 2 && 0 < tetromino.getFirst_grid_y()
                                && tetromino.getSecond_grid_x() - 1 < 10 && tetromino.getSecond_grid_y() - 1 < 19  && -1 < tetromino.getSecond_grid_x() - 1 && 0 < tetromino.getSecond_grid_y() - 1
                                && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() < 19  && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() + 1 < 10 && tetromino.getFourth_grid_y() + 1 < 19  && -1 < tetromino.getFourth_grid_x() + 1 && 0 < tetromino.getFourth_grid_y() + 1
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 2, tetromino.getFirst_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() - 1, tetromino.getSecond_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() + 1, tetromino.getFourth_grid_y() + 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 2);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y());
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 1);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 2
                        else if(tetromino.getFirst_grid_x() - 1 < 10 && tetromino.getFirst_grid_y() < 19 && -1 < tetromino.getFirst_grid_x() - 1 && 0 < tetromino.getFirst_grid_y()
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() - 1 < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y() - 1
                                && tetromino.getThird_grid_x() + 1 < 10 && tetromino.getThird_grid_y() < 19  && -1 < tetromino.getThird_grid_x() + 1 && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() + 2 < 10 && tetromino.getFourth_grid_y() + 1 < 19  && -1 < tetromino.getFourth_grid_x() + 2 && 0 < tetromino.getFourth_grid_y() + 1
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 1, tetromino.getFirst_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() + 1, tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() + 2, tetromino.getFourth_grid_y() + 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y());
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 2);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 3
                        else if(tetromino.getFirst_grid_x() - 1 < 10 && tetromino.getFirst_grid_y() + 1 < 19 && -1 < tetromino.getFirst_grid_x() - 1 && 0 < tetromino.getFirst_grid_y() + 1
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y()
                                && tetromino.getThird_grid_x() + 1 < 10 && tetromino.getThird_grid_y() + 1 < 19  && -1 < tetromino.getThird_grid_x() + 1 && 0 < tetromino.getThird_grid_y() + 1
                                && tetromino.getFourth_grid_x() + 2 < 10 && tetromino.getFourth_grid_y() + 2 < 19  && -1 < tetromino.getFourth_grid_x() + 2 && 0 < tetromino.getFourth_grid_y() + 2
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 1, tetromino.getFirst_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() + 1, tetromino.getThird_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() + 2, tetromino.getFourth_grid_y() + 2) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 1);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y());
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 1);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 2);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 2);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 4
                        else if(tetromino.getFirst_grid_x() - 2 < 10 && tetromino.getFirst_grid_y() - 2 < 19 && -1 < tetromino.getFirst_grid_x() - 2 && 0 < tetromino.getFirst_grid_y() - 2
                                && tetromino.getSecond_grid_x() - 1 < 10 && tetromino.getSecond_grid_y() - 3 < 19  && -1 < tetromino.getSecond_grid_x() - 1 && 0 < tetromino.getSecond_grid_y() - 3
                                && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() - 2 < 19  && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y() - 2
                                && tetromino.getFourth_grid_x() + 1 < 10 && tetromino.getFourth_grid_y() - 1 < 19  && -1 < tetromino.getFourth_grid_x() + 1 && 0 < tetromino.getFourth_grid_y() - 1
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 2, tetromino.getFirst_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() - 1, tetromino.getSecond_grid_y() - 3) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() + 1, tetromino.getFourth_grid_y() - 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 2);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 2);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 3);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 1);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 5
                        else if(tetromino.getFirst_grid_x() - 1 < 10 && tetromino.getFirst_grid_y() - 2 < 19 && -1 < tetromino.getFirst_grid_x() - 1 && 0 < tetromino.getFirst_grid_y() - 2
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() - 3 < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y() - 3
                                && tetromino.getThird_grid_x() + 1 < 10 && tetromino.getThird_grid_y() - 2 < 19  && -1 < tetromino.getThird_grid_x() + 1 && 0 < tetromino.getThird_grid_y() - 2
                                && tetromino.getFourth_grid_x() + 2 < 10 && tetromino.getFourth_grid_y() - 1 < 19  && -1 < tetromino.getFourth_grid_x() + 2 && 0 < tetromino.getFourth_grid_y() - 1
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 1, tetromino.getFirst_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y() - 3) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() + 1, tetromino.getThird_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() + 2, tetromino.getFourth_grid_y() - 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 2);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 3);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 2);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        else{
                            break;
                        }
                        break;
//--------------------------------------------------------------------------------------------------------------------------------------------------------------
                    case 3:
                        //Test 1
                        if(tetromino.getFirst_grid_x() < 10 && tetromino.getFirst_grid_y() - 2 < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y() - 2
                                && tetromino.getSecond_grid_x() + 1 < 10 && tetromino.getSecond_grid_y() - 1 < 19  && -1 < tetromino.getSecond_grid_x() + 1 && 0 < tetromino.getSecond_grid_y() - 1
                                && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() < 19  && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() - 1 < 10 && tetromino.getFourth_grid_y() + 1 < 19  && -1 < tetromino.getFourth_grid_x() - 1 && 0 < tetromino.getFourth_grid_y() + 1
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() + 1, tetromino.getSecond_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() - 1, tetromino.getFourth_grid_y() + 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 2);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 1);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 2
                        else if(tetromino.getFirst_grid_x() + 1 < 10 && tetromino.getFirst_grid_y() - 2 < 19 && -1 < tetromino.getFirst_grid_x() + 1 && 0 < tetromino.getFirst_grid_y() - 2
                                && tetromino.getSecond_grid_x() + 2 < 10 && tetromino.getSecond_grid_y() - 1 < 19  && -1 < tetromino.getSecond_grid_x() + 2 && 0 < tetromino.getSecond_grid_y() - 1
                                && tetromino.getThird_grid_x() + 1 < 10 && tetromino.getThird_grid_y() < 19  && -1 < tetromino.getThird_grid_x() + 1 && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() + 1 < 19  && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y() + 1
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 1, tetromino.getFirst_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() + 2, tetromino.getSecond_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() + 1, tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y() + 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 2);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 2);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 3
                        else if(tetromino.getFirst_grid_x() + 1 < 10 && tetromino.getFirst_grid_y() - 3 < 19 && -1 < tetromino.getFirst_grid_x() + 1 && 0 < tetromino.getFirst_grid_y() - 3
                                && tetromino.getSecond_grid_x() + 2 < 10 && tetromino.getSecond_grid_y() - 2 < 19  && -1 < tetromino.getSecond_grid_x() + 2 && 0 < tetromino.getSecond_grid_y() - 2
                                && tetromino.getThird_grid_x() + 1 < 10 && tetromino.getThird_grid_y() - 1 < 19  && -1 < tetromino.getThird_grid_x() + 1 && 0 < tetromino.getThird_grid_y() - 1
                                && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() < 19  && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y()
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 1, tetromino.getFirst_grid_y() - 3) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() + 2, tetromino.getSecond_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() + 1, tetromino.getThird_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y()) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 3);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 2);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 2);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 1);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y());

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 4
                        else if(tetromino.getFirst_grid_x() < 10 && tetromino.getFirst_grid_y() < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y()
                                && tetromino.getSecond_grid_x() + 1 < 10 && tetromino.getSecond_grid_y() + 1 < 19  && -1 < tetromino.getSecond_grid_x() + 1 && 0 < tetromino.getSecond_grid_y() + 1
                                && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() + 2 < 19  && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y() + 2
                                && tetromino.getFourth_grid_x() - 1 < 10 && tetromino.getFourth_grid_y() + 3 < 19  && -1 < tetromino.getFourth_grid_x() - 1 && 0 < tetromino.getFourth_grid_y() + 3
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() + 1, tetromino.getSecond_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() - 1, tetromino.getFourth_grid_y() + 3) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y());
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 1);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 3);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 5
                        else if(tetromino.getFirst_grid_x() + 1 < 10 && tetromino.getFirst_grid_y() < 19 && -1 < tetromino.getFirst_grid_x() + 1 && 0 < tetromino.getFirst_grid_y()
                                && tetromino.getSecond_grid_x() + 2 < 10 && tetromino.getSecond_grid_y() + 1 < 19  && -1 < tetromino.getSecond_grid_x() + 2 && 0 < tetromino.getSecond_grid_y() + 1
                                && tetromino.getThird_grid_x() + 1 < 10 && tetromino.getThird_grid_y() + 2 < 19  && -1 < tetromino.getThird_grid_x() + 1 && 0 < tetromino.getThird_grid_y() + 2
                                && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() + 3 < 19  && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y() + 3
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 1, tetromino.getFirst_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() + 2, tetromino.getSecond_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() + 1, tetromino.getThird_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y() + 3) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y());
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 2);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 3);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        else{
                            break;
                        }
                        break;
//--------------------------------------------------------------------------------------------------------------------------------------------------------------
                    case 4:
                        //Test 1
                        if(tetromino.getFirst_grid_x() + 2 < 10 && tetromino.getFirst_grid_y() < 19 && -1 < tetromino.getFirst_grid_x() + 2 && 0 < tetromino.getFirst_grid_y()
                                && tetromino.getSecond_grid_x() + 1 < 10 && tetromino.getSecond_grid_y() + 1 < 19  && -1 < tetromino.getSecond_grid_x() + 1 && 0 < tetromino.getSecond_grid_y() + 1
                                && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() < 19  && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() - 1 < 10 && tetromino.getFourth_grid_y() - 1 < 19  && -1 < tetromino.getFourth_grid_x() - 1 && 0 < tetromino.getFourth_grid_y() - 1
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 2, tetromino.getFirst_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() + 1, tetromino.getSecond_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() - 1, tetromino.getFourth_grid_y() - 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 2);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y());
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 1);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 2
                        else if(tetromino.getFirst_grid_x() + 1 < 10 && tetromino.getFirst_grid_y() < 19 && -1 < tetromino.getFirst_grid_x() + 1 && 0 < tetromino.getFirst_grid_y()
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() + 1 < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y() + 1
                                && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y() < 19  && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() - 2 < 10 && tetromino.getFourth_grid_y() - 1 < 19  && -1 < tetromino.getFourth_grid_x() - 2 && 0 < tetromino.getFourth_grid_y() - 1
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 1, tetromino.getFirst_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() - 2, tetromino.getFourth_grid_y() - 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y());
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 2);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 3
                        else if(tetromino.getFirst_grid_x() + 1 < 10 && tetromino.getFirst_grid_y() + 1 < 19 && -1 < tetromino.getFirst_grid_x() + 1 && 0 < tetromino.getFirst_grid_y() + 1
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() + 2 < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y() + 2
                                && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y() + 1 < 19  && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y() + 1
                                && tetromino.getFourth_grid_x() - 2 < 10 && tetromino.getFourth_grid_y() < 19  && -1 < tetromino.getFourth_grid_x() - 2 && 0 < tetromino.getFourth_grid_y()
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 1, tetromino.getFirst_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() - 2, tetromino.getFourth_grid_y()) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 1);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 2);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 1);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 2);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y());

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 4
                        else if(tetromino.getFirst_grid_x() + 2 < 10 && tetromino.getFirst_grid_y() - 2 < 19 && -1 < tetromino.getFirst_grid_x() + 2 && 0 < tetromino.getFirst_grid_y() - 2
                                && tetromino.getSecond_grid_x() + 1 < 10 && tetromino.getSecond_grid_y() - 1 < 19  && -1 < tetromino.getSecond_grid_x() + 1 && 0 < tetromino.getSecond_grid_y() - 1
                                && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() - 2 < 19  && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y() - 2
                                && tetromino.getFourth_grid_x() - 1 < 10 && tetromino.getFourth_grid_y() - 3 < 19  && -1 < tetromino.getFourth_grid_x() - 1 && 0 < tetromino.getFourth_grid_y() - 3
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 2, tetromino.getFirst_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() + 1, tetromino.getSecond_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() - 1, tetromino.getFourth_grid_y() - 3) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 2);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 2);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 1);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 3);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 5
                        else if(tetromino.getFirst_grid_x() + 1 < 10 && tetromino.getFirst_grid_y() - 2 < 19 && -1 < tetromino.getFirst_grid_x() + 1 && 0 < tetromino.getFirst_grid_y() - 2
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() - 1 < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y() - 1
                                && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y() - 2 < 19  && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y() - 2
                                && tetromino.getFourth_grid_x() - 2 < 10 && tetromino.getFourth_grid_y() - 3 < 19  && -1 < tetromino.getFourth_grid_x() - 2 && 0 < tetromino.getFourth_grid_y() - 3
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 1, tetromino.getFirst_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() - 2, tetromino.getFourth_grid_y() - 3) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 2);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 2);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 3);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        else{
                            break;
                        }
                        break;
                }
                break;
            case "J":
                switch(tetromino.getForm()){
                case 1:
                //Test 1
                if(tetromino.getFirst_grid_x() + 2 < 10 && tetromino.getFirst_grid_y()  < 19 && -1 < tetromino.getFirst_grid_x() + 2 && 0 < tetromino.getFirst_grid_y() 
                        && tetromino.getSecond_grid_x() + 1 < 10 && tetromino.getSecond_grid_y() -1 < 19  && -1 < tetromino.getSecond_grid_x() + 1 && 0 < tetromino.getSecond_grid_y() -1
                        && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y()  < 19  && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y()
                        && tetromino.getFourth_grid_x() - 1 < 10 && tetromino.getFourth_grid_y() + 1 < 19  && -1 < tetromino.getFourth_grid_x() - 1 && 0 < tetromino.getFourth_grid_y() + 1
                        
                        && isEmptyGridWithException(tetromino.getFirst_grid_x() + 2, tetromino.getFirst_grid_y() ) == true
                        && isEmptyGridWithException(tetromino.getSecond_grid_x() + 1, tetromino.getSecond_grid_y() -1 ) == true
                        && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y() ) == true
                        && isEmptyGridWithException(tetromino.getFourth_grid_x() - 1, tetromino.getFourth_grid_y() + 1) == true){
                    
                    tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 2);
                    tetromino.setFirst_grid_y(tetromino.getFirst_grid_y());
                    
                    tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() +1);
                    tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() -1);
                    
                    tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                    tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                    
                    tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() -1);
                    tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);

                    tetris_grid.getChildren().remove(tetromino.first);
                    tetris_grid.getChildren().remove(tetromino.second);
                    tetris_grid.getChildren().remove(tetromino.third);
                    tetris_grid.getChildren().remove(tetromino.fourth);

                    tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                    tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                    tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                    tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                    tetromino.RotationOfForm();
                }
                //Test 2
                else if(tetromino.getFirst_grid_x() +1 < 10 && tetromino.getFirst_grid_y()  < 19 && -1 < tetromino.getFirst_grid_x() +1 && 0 < tetromino.getFirst_grid_y()
                        && tetromino.getSecond_grid_x()  < 10 && tetromino.getSecond_grid_y() -1 < 19 && -1 < tetromino.getSecond_grid_x()  && 0 < tetromino.getSecond_grid_y() -1
                        && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y()  < 19 && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y() 
                        && tetromino.getFourth_grid_x() - 2 < 10 && tetromino.getFourth_grid_y() + 1 < 19 && -1 < tetromino.getFourth_grid_x() - 2 && 0 < tetromino.getFourth_grid_y() + 1
                        
                        && isEmptyGridWithException(tetromino.getFirst_grid_x() +1 , tetromino.getFirst_grid_y()) == true
                        && isEmptyGridWithException(tetromino.getSecond_grid_x() , tetromino.getSecond_grid_y() -1 ) == true
                        && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y() ) == true
                        && isEmptyGridWithException(tetromino.getFourth_grid_x() - 2, tetromino.getFourth_grid_y() + 1) == true){
                    
                    tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() +1);
                    tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() );
                    
                    tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                    tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 1);
                    
                    tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
                    tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                    
                    tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 2);
                    tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);

                    tetris_grid.getChildren().remove(tetromino.first);
                    tetris_grid.getChildren().remove(tetromino.second);
                    tetris_grid.getChildren().remove(tetromino.third);
                    tetris_grid.getChildren().remove(tetromino.fourth);

                    tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                    tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                    tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                    tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                    tetromino.RotationOfForm();
                }
                //Test 3
                else if(tetromino.getFirst_grid_x() + 1 < 10 && tetromino.getFirst_grid_y() - 1 < 19 && -1 < tetromino.getFirst_grid_x() + 1 && 0 < tetromino.getFirst_grid_y() - 1
                        && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() -2 < 19 && -1 < tetromino.getSecond_grid_x()  && 0 < tetromino.getSecond_grid_y() -2
                        && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y() - 1 < 19 && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y() - 1
                        && tetromino.getFourth_grid_x() -2 < 10 && tetromino.getFourth_grid_y()  < 19 && -1 < tetromino.getFourth_grid_x() -2 && 0 < tetromino.getFourth_grid_y()
                        
                        && isEmptyGridWithException(tetromino.getFirst_grid_x() + 1, tetromino.getFirst_grid_y() - 1) == true
                        && isEmptyGridWithException(tetromino.getSecond_grid_x() , tetromino.getSecond_grid_y()-2 ) == true
                        && isEmptyGridWithException(tetromino.getThird_grid_x() -1 , tetromino.getThird_grid_y() - 1) == true
                        && isEmptyGridWithException(tetromino.getFourth_grid_x() -2 , tetromino.getFourth_grid_y()) == true){
                    
                    tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 1);
                    tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 1);
                    
                    tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                    tetromino.setSecond_grid_y(tetromino.getSecond_grid_y()-2);
                    
                    tetromino.setThird_grid_x(tetromino.getThird_grid_x() -1 );
                    tetromino.setThird_grid_y(tetromino.getThird_grid_y() -1);
                    
                    tetromino.setFourth_grid_x(tetromino.getFourth_grid_x()-2);
                    tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() );

                    tetris_grid.getChildren().remove(tetromino.first);
                    tetris_grid.getChildren().remove(tetromino.second);
                    tetris_grid.getChildren().remove(tetromino.third);
                    tetris_grid.getChildren().remove(tetromino.fourth);

                    tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                    tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                    tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                    tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                    tetromino.RotationOfForm();
                }
                //Test 4
                else if(tetromino.getFirst_grid_x() +2 < 10 && tetromino.getFirst_grid_y() +2 < 19 && -1 < tetromino.getFirst_grid_x() +2 && 0 < tetromino.getFirst_grid_y() +2
                        && tetromino.getSecond_grid_x() +1 < 10 && tetromino.getSecond_grid_y() + 1 < 19 && -1 < tetromino.getSecond_grid_x() + 1 && 0 < tetromino.getSecond_grid_y() + 1
                        && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() + 2 < 19 && -1 < tetromino.getThird_grid_x()  && 0 < tetromino.getThird_grid_y() + 2
                        && tetromino.getFourth_grid_x() - 1 < 10 && tetromino.getFourth_grid_y() + 3 < 19 && -1 < tetromino.getFourth_grid_x() - 1 && 0 < tetromino.getFourth_grid_y() + 3
                        
                        && isEmptyGridWithException(tetromino.getFirst_grid_x() +2 , tetromino.getFirst_grid_y()+2) == true
                        && isEmptyGridWithException(tetromino.getSecond_grid_x() +1, tetromino.getSecond_grid_y() + 1) == true
                        && isEmptyGridWithException(tetromino.getThird_grid_x() , tetromino.getThird_grid_y() + 2) == true
                        && isEmptyGridWithException(tetromino.getFourth_grid_x() - 1, tetromino.getFourth_grid_y() + 3) == true){
                    
                    tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() +2);
                    tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() +2 );
                    
                    tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 1);
                    tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                    
                    tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                    tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 2);
                    
                    tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 1);
                    tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 3);

                    tetris_grid.getChildren().remove(tetromino.first);
                    tetris_grid.getChildren().remove(tetromino.second);
                    tetris_grid.getChildren().remove(tetromino.third);
                    tetris_grid.getChildren().remove(tetromino.fourth);

                    tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                    tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                    tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                    tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                    tetromino.RotationOfForm();
                }
                //Test 5 
                else if(tetromino.getFirst_grid_x() + 1 < 10 && tetromino.getFirst_grid_y() + 2 < 19 && -1 < tetromino.getFirst_grid_x() + 1 && 0 < tetromino.getFirst_grid_y() + 2
                        && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() + 1 < 19 && -1 < tetromino.getSecond_grid_x()  && 0 < tetromino.getSecond_grid_y() +1 
                        && tetromino.getThird_grid_x()  -1 <10 && tetromino.getThird_grid_y() + 2 < 19 && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y() + 2
                        && tetromino.getFourth_grid_x() -2 < 10 && tetromino.getFourth_grid_y() +3 < 19 && -1 < tetromino.getFourth_grid_x() -2 && 0 < tetromino.getFourth_grid_y() +3
                        
                        && isEmptyGridWithException(tetromino.getFirst_grid_x() + 1, tetromino.getFirst_grid_y() + 2) == true
                        && isEmptyGridWithException(tetromino.getSecond_grid_x() , tetromino.getSecond_grid_y() + 1) == true
                        && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y() +2) == true
                        && isEmptyGridWithException(tetromino.getFourth_grid_x() -2 , tetromino.getFourth_grid_y()+3) == true){
                    
                    tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 1);
                    tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 2);
                    
                    tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                    tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                    
                    tetromino.setThird_grid_x(tetromino.getThird_grid_x() -1);
                    tetromino.setThird_grid_y(tetromino.getThird_grid_y() +2);
                    
                    tetromino.setFourth_grid_x(tetromino.getFourth_grid_x()-2);
                    tetromino.setFourth_grid_y(tetromino.getFourth_grid_y()+3);

                    tetris_grid.getChildren().remove(tetromino.first);
                    tetris_grid.getChildren().remove(tetromino.second);
                    tetris_grid.getChildren().remove(tetromino.third);
                    tetris_grid.getChildren().remove(tetromino.fourth);

                    tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                    tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                    tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                    tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                    tetromino.RotationOfForm();
                }
                else{
                    break;
                }
                break;
//--------------------------------------------------------------------------------------------------------------------------------------------------------------
            case 2:
                //Test 1
                if(tetromino.getFirst_grid_x()  < 10 && tetromino.getFirst_grid_y() + 2 < 19 && -1 < tetromino.getFirst_grid_x()  && 0 < tetromino.getFirst_grid_y() + 2
                        && tetromino.getSecond_grid_x() +1 < 10 && tetromino.getSecond_grid_y() + 1 < 19 && -1 < tetromino.getSecond_grid_x() + 1 && 0 < tetromino.getSecond_grid_y() + 1
                        && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() < 19 && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y()
                        && tetromino.getFourth_grid_x() - 1 < 10 && tetromino.getFourth_grid_y() - 1 < 19 && -1 < tetromino.getFourth_grid_x() - 1 && 0 < tetromino.getFourth_grid_y() - 1
                        
                        && isEmptyGridWithException(tetromino.getFirst_grid_x() , tetromino.getFirst_grid_y() + 2) == true
                        && isEmptyGridWithException(tetromino.getSecond_grid_x() +1, tetromino.getSecond_grid_y() + 1) == true
                        && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y()) == true
                        && isEmptyGridWithException(tetromino.getFourth_grid_x() -1 , tetromino.getFourth_grid_y() - 1) == true){
                    
                    tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() );
                    tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 2);
                    
                    tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 1);
                    tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                    
                    tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                    tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                    
                    tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 1);
                    tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 1);

                    tetris_grid.getChildren().remove(tetromino.first);
                    tetris_grid.getChildren().remove(tetromino.second);
                    tetris_grid.getChildren().remove(tetromino.third);
                    tetris_grid.getChildren().remove(tetromino.fourth);

                    tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                    tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                    tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                    tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                    tetromino.RotationOfForm();
                }
                //Test 2
                else  if(tetromino.getFirst_grid_x()  < 10 && tetromino.getFirst_grid_y() + 2 < 19 && -1 < tetromino.getFirst_grid_x()  && 0 < tetromino.getFirst_grid_y() + 2
                        && tetromino.getSecond_grid_x() +1 < 10 && tetromino.getSecond_grid_y() + 1 < 19 && -1 < tetromino.getSecond_grid_x() + 1 && 0 < tetromino.getSecond_grid_y() + 1
                        && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() < 19 && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y()
                        && tetromino.getFourth_grid_x() - 1 < 10 && tetromino.getFourth_grid_y() - 1 < 19 && -1 < tetromino.getFourth_grid_x() - 1 && 0 < tetromino.getFourth_grid_y() - 1
                        
                        && isEmptyGridWithException(tetromino.getFirst_grid_x() , tetromino.getFirst_grid_y() + 2) == true
                        && isEmptyGridWithException(tetromino.getSecond_grid_x() +1, tetromino.getSecond_grid_y() + 1) == true
                        && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y()) == true
                        && isEmptyGridWithException(tetromino.getFourth_grid_x() -1 , tetromino.getFourth_grid_y() - 1) == true){
                    
                    tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() );
                    tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 2);
                    
                    tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 1);
                    tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                    
                    tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                    tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                    
                    tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 1);
                    tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 1);

                    tetris_grid.getChildren().remove(tetromino.first);
                    tetris_grid.getChildren().remove(tetromino.second);
                    tetris_grid.getChildren().remove(tetromino.third);
                    tetris_grid.getChildren().remove(tetromino.fourth);

                    tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                    tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                    tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                    tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                    tetromino.RotationOfForm();
                }
                //Test 3
                else if(tetromino.getFirst_grid_x() +1 < 10 && tetromino.getFirst_grid_y() + 3 < 19 && -1 < tetromino.getFirst_grid_x() +1 && 0 < tetromino.getFirst_grid_y() + 3
                        && tetromino.getSecond_grid_x() + 2 < 10 && tetromino.getSecond_grid_y() + 2 < 19 && -1 < tetromino.getSecond_grid_x() + 2 && 0 < tetromino.getSecond_grid_y() + 2
                        && tetromino.getThird_grid_x() + 1 < 10 && tetromino.getThird_grid_y() +1 < 19 && -1 < tetromino.getThird_grid_x() + 1 && 0 < tetromino.getThird_grid_y() + 1
                        && tetromino.getFourth_grid_x()  < 10 && tetromino.getFourth_grid_y() < 19 && -1 < tetromino.getFourth_grid_x()  && 0 < tetromino.getFourth_grid_y() 
                        
                        && isEmptyGridWithException(tetromino.getFirst_grid_x() +1, tetromino.getFirst_grid_y() + 3) == true
                        && isEmptyGridWithException(tetromino.getSecond_grid_x() + 2, tetromino.getSecond_grid_y() + 2) == true
                        && isEmptyGridWithException(tetromino.getThird_grid_x() + 1, tetromino.getThird_grid_y() +1 ) == true
                        && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y()) == true){
                    
                    tetromino.setFirst_grid_x(tetromino.getFirst_grid_x()+1);
                    tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 3);
                    
                    tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 2);
                    tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 2);
                    
                    tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 1);
                    tetromino.setThird_grid_y(tetromino.getThird_grid_y()+ 1);
                    
                    tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                    tetromino.setFourth_grid_y(tetromino.getFourth_grid_y());

                    tetris_grid.getChildren().remove(tetromino.first);
                    tetris_grid.getChildren().remove(tetromino.second);
                    tetris_grid.getChildren().remove(tetromino.third);
                    tetris_grid.getChildren().remove(tetromino.fourth);

                    tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                    tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                    tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                    tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                    tetromino.RotationOfForm();
                }
                //Test 4
                else if(tetromino.getFirst_grid_x()  < 10 && tetromino.getFirst_grid_y() < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y()
                        && tetromino.getSecond_grid_x() +1 < 10 && tetromino.getSecond_grid_y() - 1 < 19 && -1 < tetromino.getSecond_grid_x() +1 && 0 < tetromino.getSecond_grid_y() - 1
                        && tetromino.getThird_grid_x()  < 10 && tetromino.getThird_grid_y() - 2 < 19 && -1 < tetromino.getThird_grid_x()  && 0 < tetromino.getThird_grid_y() - 2
                        && tetromino.getFourth_grid_x() -1 < 10 && tetromino.getFourth_grid_y() - 3 < 19 && -1 < tetromino.getFourth_grid_x() -1 && 0 < tetromino.getFourth_grid_y() - 3
                        
                        && isEmptyGridWithException(tetromino.getFirst_grid_x() , tetromino.getFirst_grid_y()) == true
                        && isEmptyGridWithException(tetromino.getSecond_grid_x() +1, tetromino.getSecond_grid_y() - 1) == true
                        && isEmptyGridWithException(tetromino.getThird_grid_x() , tetromino.getThird_grid_y() - 2) == true
                        && isEmptyGridWithException(tetromino.getFourth_grid_x() - 1, tetromino.getFourth_grid_y() - 3) == true){
                    
                    tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                    tetromino.setFirst_grid_y(tetromino.getFirst_grid_y());
                    
                    tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 1);
                    tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 1);
                    
                    tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                    tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 2);
                    
                    tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() -1 );
                    tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 3);

                    tetris_grid.getChildren().remove(tetromino.first);
                    tetris_grid.getChildren().remove(tetromino.second);
                    tetris_grid.getChildren().remove(tetromino.third);
                    tetris_grid.getChildren().remove(tetromino.fourth);

                    tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                    tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                    tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                    tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                    tetromino.RotationOfForm();
                }
                //Test 5
                else if(tetromino.getFirst_grid_x() +1 < 10 && tetromino.getFirst_grid_y() < 19 && -1 < tetromino.getFirst_grid_x() +1 && 0 < tetromino.getFirst_grid_y() 
                        && tetromino.getSecond_grid_x() + 2 < 10 && tetromino.getSecond_grid_y() -1 < 19 && -1 < tetromino.getSecond_grid_x() + 2 && 0 < tetromino.getSecond_grid_y() - 1
                        && tetromino.getThird_grid_x() + 1 < 10 && tetromino.getThird_grid_y() - 2 < 19 && -1 < tetromino.getThird_grid_x() + 1 && 0 < tetromino.getThird_grid_y() - 2
                        && tetromino.getFourth_grid_x()  < 10 && tetromino.getFourth_grid_y() -3 < 19 && -1 < tetromino.getFourth_grid_x()  && 0 < tetromino.getFourth_grid_y() -3
                        
                        && isEmptyGridWithException(tetromino.getFirst_grid_x() +1 , tetromino.getFirst_grid_y() ) == true
                        && isEmptyGridWithException(tetromino.getSecond_grid_x() + 2, tetromino.getSecond_grid_y() - 1) == true
                        && isEmptyGridWithException(tetromino.getThird_grid_x() + 1, tetromino.getThird_grid_y() -2) == true
                        && isEmptyGridWithException(tetromino.getFourth_grid_x() , tetromino.getFourth_grid_y()-3)  == true){
                    
                    tetromino.setFirst_grid_x(tetromino.getFirst_grid_x()+ 1);
                    tetromino.setFirst_grid_y(tetromino.getFirst_grid_y());
                    
                    tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 2);
                    tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() -1);
                    
                    tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 1);
                    tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 2);
                    
                    tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                    tetromino.setFourth_grid_y(tetromino.getFourth_grid_y()-3);

                    tetris_grid.getChildren().remove(tetromino.first);
                    tetris_grid.getChildren().remove(tetromino.second);
                    tetris_grid.getChildren().remove(tetromino.third);
                    tetris_grid.getChildren().remove(tetromino.fourth);

                    tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                    tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                    tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                    tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                    tetromino.RotationOfForm();
                }
                else{
                    break;
                }
                break;
//--------------------------------------------------------------------------------------------------------------------------------------------------------------
            case 3:
                //Test 1
                if(tetromino.getFirst_grid_x() - 2 < 10 && tetromino.getFirst_grid_y() < 19 && -1 < tetromino.getFirst_grid_x() - 2 && 0 < tetromino.getFirst_grid_y()
                        && tetromino.getSecond_grid_x() - 1< 10 && tetromino.getSecond_grid_y() + 1 < 19 && -1 < tetromino.getSecond_grid_x() -1 && 0 < tetromino.getSecond_grid_y() +1
                        && tetromino.getThird_grid_x()  < 10 && tetromino.getThird_grid_y() < 19 && -1 < tetromino.getThird_grid_x()  && 0 < tetromino.getThird_grid_y()
                        && tetromino.getFourth_grid_x() +1 < 10 && tetromino.getFourth_grid_y() - 1 < 19 && -1 < tetromino.getFourth_grid_x() +1 && 0 < tetromino.getFourth_grid_y() - 1
                        
                        && isEmptyGridWithException(tetromino.getFirst_grid_x() -2 , tetromino.getFirst_grid_y()) == true
                        && isEmptyGridWithException(tetromino.getSecond_grid_x() -1, tetromino.getSecond_grid_y() + 1) == true
                        && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y()) == true
                        && isEmptyGridWithException(tetromino.getFourth_grid_x() + 1, tetromino.getFourth_grid_y() - 1) == true){
                    
                    tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 2);
                    tetromino.setFirst_grid_y(tetromino.getFirst_grid_y());
                    
                    tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() -1 );
                    tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() +1);
                    
                    tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                    tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                    
                    tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() +1);
                    tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() -1);

                    tetris_grid.getChildren().remove(tetromino.first);
                    tetris_grid.getChildren().remove(tetromino.second);
                    tetris_grid.getChildren().remove(tetromino.third);
                    tetris_grid.getChildren().remove(tetromino.fourth);

                    tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                    tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                    tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                    tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                    tetromino.RotationOfForm();
                }
                //Test 2
                else if(tetromino.getFirst_grid_x() -1 < 10 && tetromino.getFirst_grid_y() < 19 && -1 < tetromino.getFirst_grid_x() -1 && 0 < tetromino.getFirst_grid_y()
                        && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() - 1 < 19 && -1 < tetromino.getSecond_grid_x()  && 0 < tetromino.getSecond_grid_y() - 1
                        && tetromino.getThird_grid_x() + 1 < 10 && tetromino.getThird_grid_y() < 19 && -1 < tetromino.getThird_grid_x() + 1 && 0 < tetromino.getThird_grid_y()
                        && tetromino.getFourth_grid_x() +2 < 10 && tetromino.getFourth_grid_y() - 1 < 19 && -1 < tetromino.getFourth_grid_x() +2 && 0 < tetromino.getFourth_grid_y() - 1
                        
                        && isEmptyGridWithException(tetromino.getFirst_grid_x() -1, tetromino.getFirst_grid_y()) == true
                        && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y() + 1) == true
                        && isEmptyGridWithException(tetromino.getThird_grid_x() + 1, tetromino.getThird_grid_y()) == true
                        && isEmptyGridWithException(tetromino.getFourth_grid_x() +2, tetromino.getFourth_grid_y() -1 ) == true){
                    
                    tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() -1);
                    tetromino.setFirst_grid_y(tetromino.getFirst_grid_y());
                    
                    tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                    tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() +1);
                    
                    tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 1);
                    tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                    
                    tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() +2);
                    tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() -1);

                    tetris_grid.getChildren().remove(tetromino.first);
                    tetris_grid.getChildren().remove(tetromino.second);
                    tetris_grid.getChildren().remove(tetromino.third);
                    tetris_grid.getChildren().remove(tetromino.fourth);

                    tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                    tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                    tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                    tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                    tetromino.RotationOfForm();
                }
                //Test 3
                else if(tetromino.getFirst_grid_x() -1 < 10 && tetromino.getFirst_grid_y() - 1 < 19 && -1 < tetromino.getFirst_grid_x() -1 && 0 < tetromino.getFirst_grid_y() - 1
                        && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() < 19 && -1 < tetromino.getSecond_grid_x()  && 0 < tetromino.getSecond_grid_y() 
                        && tetromino.getThird_grid_x() + 1 < 10 && tetromino.getThird_grid_y() -1 < 19 && -1 < tetromino.getThird_grid_x() + 1 && 0 < tetromino.getThird_grid_y() -1
                        && tetromino.getFourth_grid_x() +2  < 10 && tetromino.getFourth_grid_y() - 2 < 19 && -1 < tetromino.getFourth_grid_x() +2 && 0 < tetromino.getFourth_grid_y() - 2
                        
                        && isEmptyGridWithException(tetromino.getFirst_grid_x() -1 , tetromino.getFirst_grid_y() - 1) == true
                        && isEmptyGridWithException(tetromino.getSecond_grid_x() , tetromino.getSecond_grid_y() ) == true
                        && isEmptyGridWithException(tetromino.getThird_grid_x() +1, tetromino.getThird_grid_y()-1) == true
                        && isEmptyGridWithException(tetromino.getFourth_grid_x() +2, tetromino.getFourth_grid_y() -2) == true){
                    
                    tetromino.setFirst_grid_x(tetromino.getFirst_grid_x()-1);
                    tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 1);
                    
                    tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                    tetromino.setSecond_grid_y(tetromino.getSecond_grid_y());
                    
                    tetromino.setThird_grid_x(tetromino.getThird_grid_x()+1);
                    tetromino.setThird_grid_y(tetromino.getThird_grid_y()-1);
                    
                    tetromino.setFourth_grid_x(tetromino.getFourth_grid_x()+2);
                    tetromino.setFourth_grid_y(tetromino.getFourth_grid_y()-2);

                    tetris_grid.getChildren().remove(tetromino.first);
                    tetris_grid.getChildren().remove(tetromino.second);
                    tetris_grid.getChildren().remove(tetromino.third);
                    tetris_grid.getChildren().remove(tetromino.fourth);

                    tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                    tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                    tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                    tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                    tetromino.RotationOfForm();
                }
                //Test 4
                else if(tetromino.getFirst_grid_x() -2 < 10 && tetromino.getFirst_grid_y() + 2 < 19 && -1 < tetromino.getFirst_grid_x() -2 && 0 < tetromino.getFirst_grid_y() +2
                        && tetromino.getSecond_grid_x() -1 < 10 && tetromino.getSecond_grid_y() +3 < 19 && -1 < tetromino.getSecond_grid_x() -1 && 0 < tetromino.getSecond_grid_y() +3
                        && tetromino.getThird_grid_x()  < 10 && tetromino.getThird_grid_y() +2 < 19 && -1 < tetromino.getThird_grid_x()  && 0 < tetromino.getThird_grid_y() +2
                        && tetromino.getFourth_grid_x()+1 < 10 && tetromino.getFourth_grid_y() +1 < 19 && -1 < tetromino.getFourth_grid_x() +1 && 0 < tetromino.getFourth_grid_y() +1
                        
                        && isEmptyGridWithException(tetromino.getFirst_grid_x() -2 , tetromino.getFirst_grid_y() +2) == true
                        && isEmptyGridWithException(tetromino.getSecond_grid_x() -1, tetromino.getSecond_grid_y() +3) == true
                        && isEmptyGridWithException(tetromino.getThird_grid_x() , tetromino.getThird_grid_y() +2) == true
                        && isEmptyGridWithException(tetromino.getFourth_grid_x() +1 , tetromino.getFourth_grid_y() +1) == true){
                    
                    tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 2);
                    tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() +2);
                    
                    tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() -1);
                    tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() +3);
                    
                    tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                    tetromino.setThird_grid_y(tetromino.getThird_grid_y()+2);
                    
                    tetromino.setFourth_grid_x(tetromino.getFourth_grid_x()-1);
                    tetromino.setFourth_grid_y(tetromino.getFourth_grid_y()+1);

                    tetris_grid.getChildren().remove(tetromino.first);
                    tetris_grid.getChildren().remove(tetromino.second);
                    tetris_grid.getChildren().remove(tetromino.third);
                    tetris_grid.getChildren().remove(tetromino.fourth);

                    tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                    tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                    tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                    tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                    tetromino.RotationOfForm();
                }
                //Test 5
                else if(tetromino.getFirst_grid_x() -1 < 10 && tetromino.getFirst_grid_y() +2 < 19 && -1 < tetromino.getFirst_grid_x() -1  && 0 < tetromino.getFirst_grid_y() +2
                        && tetromino.getSecond_grid_x()  < 10 && tetromino.getSecond_grid_y() + 3 < 19 && -1 < tetromino.getSecond_grid_x()  && 0 < tetromino.getSecond_grid_y() + 3
                        && tetromino.getThird_grid_x() +1 < 10 && tetromino.getThird_grid_y() + 2 < 19 && -1 < tetromino.getThird_grid_x() +1 && 0 < tetromino.getThird_grid_y() + 2
                        && tetromino.getFourth_grid_x() +2 < 10 && tetromino.getFourth_grid_y() + 1 < 19 && -1 < tetromino.getFourth_grid_x() +2 && 0 < tetromino.getFourth_grid_y() + 1
                        
                        && isEmptyGridWithException(tetromino.getFirst_grid_x() -1 , tetromino.getFirst_grid_y()+2) == true
                        && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y() + 3) == true
                        && isEmptyGridWithException(tetromino.getThird_grid_x() +1, tetromino.getThird_grid_y() + 2) == true
                        && isEmptyGridWithException(tetromino.getFourth_grid_x() +2, tetromino.getFourth_grid_y() + 1) == true){
                    
                    tetromino.setFirst_grid_x(tetromino.getFirst_grid_x()-1);
                    tetromino.setFirst_grid_y(tetromino.getFirst_grid_y()+2);
                    
                    tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                    tetromino.setSecond_grid_y(tetromino.getSecond_grid_y()+ 3);
                    
                    tetromino.setThird_grid_x(tetromino.getThird_grid_x() +1);
                    tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 2);
                    
                    tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() +2);
                    tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);

                    tetris_grid.getChildren().remove(tetromino.first);
                    tetris_grid.getChildren().remove(tetromino.second);
                    tetris_grid.getChildren().remove(tetromino.third);
                    tetris_grid.getChildren().remove(tetromino.fourth);

                    tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                    tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                    tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                    tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                    tetromino.RotationOfForm();
                }
                else{
                    break;
                }
                break;
//--------------------------------------------------------------------------------------------------------------------------------------------------------------
            case 4:
                //Test 1
                if(tetromino.getFirst_grid_x()  < 10 && tetromino.getFirst_grid_y() - 1 < 19 && -1 < tetromino.getFirst_grid_x()  && 0 < tetromino.getFirst_grid_y() -1
                        && tetromino.getSecond_grid_x() -1 < 10 && tetromino.getSecond_grid_y() < 19 && -1 < tetromino.getSecond_grid_x() -1 && 0 < tetromino.getSecond_grid_y()
                        && tetromino.getThird_grid_x()  < 10 && tetromino.getThird_grid_y() + 1 < 19 && -1 < tetromino.getThird_grid_x()  && 0 < tetromino.getThird_grid_y() + 1
                        && tetromino.getFourth_grid_x() + 1 < 10 && tetromino.getFourth_grid_y() + 2 < 19 && -1 < tetromino.getFourth_grid_x() + 1 && 0 < tetromino.getFourth_grid_y() + 2
                        
                        && isEmptyGridWithException(tetromino.getFirst_grid_x() , tetromino.getFirst_grid_y() - 2-1) == true
                        && isEmptyGridWithException(tetromino.getSecond_grid_x()-1, tetromino.getSecond_grid_y()) == true
                        && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y() + 1) == true
                        && isEmptyGridWithException(tetromino.getFourth_grid_x() + 1, tetromino.getFourth_grid_y() + 2) == true){
                    
                    tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                    tetromino.setFirst_grid_y(tetromino.getFirst_grid_y()-1);
                    
                    tetromino.setSecond_grid_x(tetromino.getSecond_grid_x()-1);
                    tetromino.setSecond_grid_y(tetromino.getSecond_grid_y());
                    
                    tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                    tetromino.setThird_grid_y(tetromino.getThird_grid_y()+1);
                    
                    tetromino.setFourth_grid_x(tetromino.getFourth_grid_x()+1);
                    tetromino.setFourth_grid_y(tetromino.getFourth_grid_y()+2);

                    tetris_grid.getChildren().remove(tetromino.first);
                    tetris_grid.getChildren().remove(tetromino.second);
                    tetris_grid.getChildren().remove(tetromino.third);
                    tetris_grid.getChildren().remove(tetromino.fourth);

                    tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                    tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                    tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                    tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                    tetromino.RotationOfForm();
                }
                //Test 2
                else if(tetromino.getFirst_grid_x() -1< 10 && tetromino.getFirst_grid_y() -2 < 19 && -1 < tetromino.getFirst_grid_x() -1 && 0 < tetromino.getFirst_grid_y() -2
                        && tetromino.getSecond_grid_x() -2 < 10 && tetromino.getSecond_grid_y() -1 < 19 && -1 < tetromino.getSecond_grid_x() -2 && 0 < tetromino.getSecond_grid_y() -1
                        && tetromino.getThird_grid_x() -1 < 10 && tetromino.getThird_grid_y() < 19 && -1 < tetromino.getThird_grid_x() -1 && 0 < tetromino.getThird_grid_y() 
                        && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() - 1 < 19 && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y() - 1
                        
                        && isEmptyGridWithException(tetromino.getFirst_grid_x()-1, tetromino.getFirst_grid_y() -2) == true
                        && isEmptyGridWithException(tetromino.getSecond_grid_x() -2, tetromino.getSecond_grid_y()-1) == true
                        && isEmptyGridWithException(tetromino.getThird_grid_x() -1, tetromino.getThird_grid_y() ) == true
                        && isEmptyGridWithException(tetromino.getFourth_grid_x() , tetromino.getFourth_grid_y() - 1) == true){
                    
                    tetromino.setFirst_grid_x(tetromino.getFirst_grid_x()-1);
                    tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 2);
                    
                    tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 2);
                    tetromino.setSecond_grid_y(tetromino.getSecond_grid_y()-1);
                    
                    tetromino.setThird_grid_x(tetromino.getThird_grid_x() -1);
                    tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                    
                    tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                    tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 1);

                    tetris_grid.getChildren().remove(tetromino.first);
                    tetris_grid.getChildren().remove(tetromino.second);
                    tetris_grid.getChildren().remove(tetromino.third);
                    tetris_grid.getChildren().remove(tetromino.fourth);

                    tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                    tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                    tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                    tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                    tetromino.RotationOfForm();
                }
                //Test 3
                else if(tetromino.getFirst_grid_x() - 1< 10 && tetromino.getFirst_grid_y() -1 < 19 && -1 < tetromino.getFirst_grid_x() - 1 && 0 < tetromino.getFirst_grid_y() - 1
                        && tetromino.getSecond_grid_x() - 2 < 10 && tetromino.getSecond_grid_y() < 19 && -1 < tetromino.getSecond_grid_x() - 2 && 0 < tetromino.getSecond_grid_y()
                        && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y() +1 < 19 && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y() +1
                        && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() + 2 < 19 && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y() + 2
                        
                        && isEmptyGridWithException(tetromino.getFirst_grid_x() - 1, tetromino.getFirst_grid_y() -1) == true
                        && isEmptyGridWithException(tetromino.getSecond_grid_x() - 2, tetromino.getSecond_grid_y()) == true
                        && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y() + 1) == true
                        && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y() + 2) == true){
                    
                    tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 1);
                    tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 1);
                    
                    tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 2);
                    tetromino.setSecond_grid_y(tetromino.getSecond_grid_y());
                    
                    tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
                    tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 1);
                    
                    tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                    tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 2);

                    tetris_grid.getChildren().remove(tetromino.first);
                    tetris_grid.getChildren().remove(tetromino.second);
                    tetris_grid.getChildren().remove(tetromino.third);
                    tetris_grid.getChildren().remove(tetromino.fourth);

                    tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                    tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                    tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                    tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                    tetromino.RotationOfForm();
                }
                //Test 4
                else if(tetromino.getFirst_grid_x() < 10 && tetromino.getFirst_grid_y() -4 < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y() - 4
                        && tetromino.getSecond_grid_x() - 1 < 10 && tetromino.getSecond_grid_y() -3 < 19 && -1 < tetromino.getSecond_grid_x() - 1 && 0 < tetromino.getSecond_grid_y() - 3
                        && tetromino.getThird_grid_x()  < 10 && tetromino.getThird_grid_y() -2 < 19 && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y() -2
                        && tetromino.getFourth_grid_x() + 1 < 10 && tetromino.getFourth_grid_y() -1< 19 && -1 < tetromino.getFourth_grid_x() + 1 && 0 < tetromino.getFourth_grid_y() -1
                        
                        && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y() - 4) == true
                        && isEmptyGridWithException(tetromino.getSecond_grid_x() - 1, tetromino.getSecond_grid_y() - 3) == true
                        && isEmptyGridWithException(tetromino.getThird_grid_x() , tetromino.getThird_grid_y() - 2) == true
                        && isEmptyGridWithException(tetromino.getFourth_grid_x() + 1, tetromino.getFourth_grid_y()-1) == true){
                    
                    tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                    tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 4);
                    
                    tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 1);
                    tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 3);
                    
                    tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                    tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 2);
                    
                    tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 1);
                    tetromino.setFourth_grid_y(tetromino.getFourth_grid_y()-1);

                    tetris_grid.getChildren().remove(tetromino.first);
                    tetris_grid.getChildren().remove(tetromino.second);
                    tetris_grid.getChildren().remove(tetromino.third);
                    tetris_grid.getChildren().remove(tetromino.fourth);

                    tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                    tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                    tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                    tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                    tetromino.RotationOfForm();
                }
                //Test 5
                else if(tetromino.getFirst_grid_x() - 1 < 10 && tetromino.getFirst_grid_y() -4 < 19 && -1 < tetromino.getFirst_grid_x() - 1 && 0 < tetromino.getFirst_grid_y() -4
                         && tetromino.getSecond_grid_x() - 2 < 10 && tetromino.getSecond_grid_y() - 3 < 19 && -1 < tetromino.getSecond_grid_x() - 2 && 0 < tetromino.getSecond_grid_y() - 3
                        && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y() - 2 < 19 && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y() - 2
                        && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() - 1 < 19 && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y() - 1
                        
                        && isEmptyGridWithException(tetromino.getFirst_grid_x() - 1, tetromino.getFirst_grid_y()-4) == true
                        && isEmptyGridWithException(tetromino.getSecond_grid_x() - 2, tetromino.getSecond_grid_y() - 3) == true
                        && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y() - 2) == true
                        && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y() - 1) == true){
                    
                    tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 1);
                    tetromino.setFirst_grid_y(tetromino.getFirst_grid_y()-4);
                    
                    tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 2);
                    tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 3);
                    
                    tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
                    tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 2);
                    
                    tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                    tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 1);

                    tetris_grid.getChildren().remove(tetromino.first);
                    tetris_grid.getChildren().remove(tetromino.second);
                    tetris_grid.getChildren().remove(tetromino.third);
                    tetris_grid.getChildren().remove(tetromino.fourth);

                    tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                    tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                    tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                    tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                    tetromino.RotationOfForm();
                }
                else{
                    break;
                }
                break;
                }
                break;
            case "T":
                switch(tetromino.getForm()){
                    case 1:
                        //Test 1
                        if(tetromino.getFirst_grid_x() + 1 < 10 && tetromino.getFirst_grid_y() + 1 < 19 && -1 < tetromino.getFirst_grid_x() + 1 && 0 < tetromino.getFirst_grid_y() + 1
                                && tetromino.getSecond_grid_x() + 1 < 10 && tetromino.getSecond_grid_y() - 1 < 19  && -1 < tetromino.getSecond_grid_x() + 1 && 0 < tetromino.getSecond_grid_y() -1
                                && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() < 19  && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() - 1 < 10 && tetromino.getFourth_grid_y() + 1 < 19  && -1 < tetromino.getFourth_grid_x() - 1 && 0 < tetromino.getFourth_grid_y() + 1
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 1, tetromino.getFirst_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() + 1, tetromino.getSecond_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() - 1, tetromino.getFourth_grid_y() + 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 1);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 1);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 2
                        else if(tetromino.getFirst_grid_x() < 10 && tetromino.getFirst_grid_y() + 1 < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y() + 1
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() - 1 < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y() - 1
                                && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y() < 19  && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() - 2 < 10 && tetromino.getFourth_grid_y() + 1 < 19  && -1 < tetromino.getFourth_grid_x() - 2 && 0 < tetromino.getFourth_grid_y() + 1
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() - 2, tetromino.getFourth_grid_y() + 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 1);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 2);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 3
                        else if(tetromino.getFirst_grid_x() < 10 && tetromino.getFirst_grid_y() < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y()
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() - 2 < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y() - 2
                                && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y() - 1 < 19  && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y() - 1
                                && tetromino.getFourth_grid_x() - 2 < 10 && tetromino.getFourth_grid_y() < 19  && -1 < tetromino.getFourth_grid_x() - 2 && 0 < tetromino.getFourth_grid_y()
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() - 2, tetromino.getFourth_grid_y()) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y());
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 2);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 1);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 2);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y());

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 4
                        else if(tetromino.getFirst_grid_x() + 1 < 10 && tetromino.getFirst_grid_y() + 3 < 19 && -1 < tetromino.getFirst_grid_x() + 1 && 0 < tetromino.getFirst_grid_y() + 3
                               && tetromino.getSecond_grid_x() + 1 < 10 && tetromino.getSecond_grid_y() + 1 < 19  && -1 < tetromino.getSecond_grid_x() + 1 && 0 < tetromino.getSecond_grid_y() + 1
                                && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() + 2 < 19  && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y() + 2
                                && tetromino.getFourth_grid_x() - 1 < 10 && tetromino.getFourth_grid_y() + 3 < 19  && -1 < tetromino.getFourth_grid_x() - 1 && 0 < tetromino.getFourth_grid_y() + 3
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 1, tetromino.getFirst_grid_y() + 3) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() + 1, tetromino.getSecond_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() - 1, tetromino.getFourth_grid_y() + 3) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 3);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 1);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 3);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 5 
                        else if(tetromino.getFirst_grid_x() < 10 && tetromino.getFirst_grid_y() + 3 < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y() + 3
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() + 1 < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y() + 1
                                && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y() + 2 < 19  && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y() + 2
                                && tetromino.getFourth_grid_x() - 2 < 10 && tetromino.getFourth_grid_y() + 3 < 19  && -1 < tetromino.getFourth_grid_x() - 2 && 0 < tetromino.getFourth_grid_y() + 3
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y() + 3) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() - 2, tetromino.getFourth_grid_y() + 3) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 3);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 2);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 3);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        else{
                            break;
                        }
                        break;
//--------------------------------------------------------------------------------------------------------------------------------------------------------------
                    case 2:
                        //Test 1
                        if(tetromino.getFirst_grid_x() - 1 < 10 && tetromino.getFirst_grid_y() + 1 < 19 && -1 < tetromino.getFirst_grid_x() - 1 && 0 < tetromino.getFirst_grid_y() + 1
                                && tetromino.getSecond_grid_x() + 1 < 10 && tetromino.getSecond_grid_y() + 1 < 19  && -1 < tetromino.getSecond_grid_x() + 1 && 0 < tetromino.getSecond_grid_y() + 1
                                && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() < 19  && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() - 1 < 10 && tetromino.getFourth_grid_y() - 1 < 19  && -1 < tetromino.getFourth_grid_x() - 1 && 0 < tetromino.getFourth_grid_y() - 1
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 1, tetromino.getFirst_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() + 1, tetromino.getSecond_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() - 1, tetromino.getFourth_grid_y() - 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 1);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 1);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 2
                        else if(tetromino.getFirst_grid_x() < 10 && tetromino.getFirst_grid_y() + 1 < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y() + 1
                                && tetromino.getSecond_grid_x() + 2 < 10 && tetromino.getSecond_grid_y() + 1 < 19  && -1 < tetromino.getSecond_grid_x() + 2 && 0 < tetromino.getSecond_grid_y() + 1
                                && tetromino.getThird_grid_x() + 1 < 10 && tetromino.getThird_grid_y() < 19  && -1 < tetromino.getThird_grid_x() + 1 && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() - 1 < 19  && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y() - 1
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() + 2, tetromino.getSecond_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() + 1, tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y() - 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 1);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 2);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 3
                        else if(tetromino.getFirst_grid_x() < 10 && tetromino.getFirst_grid_y() + 2 < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y() + 2
                                && tetromino.getSecond_grid_x() + 2 < 10 && tetromino.getSecond_grid_y() + 2 < 19  && -1 < tetromino.getSecond_grid_x() + 2 && 0 < tetromino.getSecond_grid_y() + 2
                                && tetromino.getThird_grid_x() + 1 < 10 && tetromino.getThird_grid_y() + 1 < 19  && -1 < tetromino.getThird_grid_x() + 1 && 0 < tetromino.getThird_grid_y() + 1
                                && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() < 19  && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y()
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() + 2, tetromino.getSecond_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() + 1, tetromino.getThird_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y()) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 2);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 2);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 2);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 1);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y());

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 4
                        else if(tetromino.getFirst_grid_x() - 1 < 10 && tetromino.getFirst_grid_y() - 1 < 19 && -1 < tetromino.getFirst_grid_x() - 1 && 0 < tetromino.getFirst_grid_y() - 1
                                && tetromino.getSecond_grid_x() + 1 < 10 && tetromino.getSecond_grid_y() - 1 < 19  && -1 < tetromino.getSecond_grid_x() + 1 && 0 < tetromino.getSecond_grid_y() - 1
                                && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() - 2 < 19  && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y() - 2
                                && tetromino.getFourth_grid_x() - 1 < 10 && tetromino.getFourth_grid_y() - 3 < 19  && -1 < tetromino.getFourth_grid_x() - 1 && 0 < tetromino.getFourth_grid_y() - 3
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 1, tetromino.getFirst_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() + 1, tetromino.getSecond_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() - 1, tetromino.getFourth_grid_y() - 3) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 1);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() - 1);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 3);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 5
                        else if(tetromino.getFirst_grid_x() < 10 && tetromino.getFirst_grid_y() - 1 < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y() - 1
                                && tetromino.getSecond_grid_x() + 2 < 10 && tetromino.getSecond_grid_y() - 1 < 19  && -1 < tetromino.getSecond_grid_x() + 2 && 0 < tetromino.getSecond_grid_y() - 1
                                && tetromino.getThird_grid_x() + 1 < 10 && tetromino.getThird_grid_y() - 2 < 19  && -1 < tetromino.getThird_grid_x() + 1 && 0 < tetromino.getThird_grid_y() - 2
                                && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() - 3 < 19  && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y() - 3
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() + 2, tetromino.getSecond_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() + 1, tetromino.getThird_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y() - 3) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 1);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() + 2);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 3);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        else{
                            break;
                        }
                        break;
//--------------------------------------------------------------------------------------------------------------------------------------------------------------
                    case 3:
                        //Test 1
                        if(tetromino.getFirst_grid_x() - 1 < 10 && tetromino.getFirst_grid_y() - 1 < 19 && -1 < tetromino.getFirst_grid_x() - 1 && 0 < tetromino.getFirst_grid_y() - 1
                                && tetromino.getSecond_grid_x() - 1 < 10 && tetromino.getSecond_grid_y() + 1 < 19  && -1 < tetromino.getSecond_grid_x()- 1 && 0 < tetromino.getSecond_grid_y() + 1
                                && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() < 19  && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() + 1 < 10 && tetromino.getFourth_grid_y() - 1 < 19  && -1 < tetromino.getFourth_grid_x() + 1 && 0 < tetromino.getFourth_grid_y() - 1
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 1, tetromino.getFirst_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() - 1, tetromino.getSecond_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() + 1, tetromino.getFourth_grid_y() - 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 1);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 1);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 2
                        else if(tetromino.getFirst_grid_x() < 10 && tetromino.getFirst_grid_y() - 1 < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y() - 1
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() + 1 < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y() + 1
                                && tetromino.getThird_grid_x() + 1 < 10 && tetromino.getThird_grid_y() < 19  && -1 < tetromino.getThird_grid_x() + 1 && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() + 2 < 10 && tetromino.getFourth_grid_y() - 1 < 19  && -1 < tetromino.getFourth_grid_x() + 2 && 0 < tetromino.getFourth_grid_y() - 1
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() + 1, tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() + 2, tetromino.getFourth_grid_y() - 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 1);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 2);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 3
                        else if(tetromino.getFirst_grid_x() < 10 && tetromino.getFirst_grid_y() - 2 < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y() - 2
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y() 
                                && tetromino.getThird_grid_x() + 1 < 10 && tetromino.getThird_grid_y() - 1 < 19  && -1 < tetromino.getThird_grid_x() + 1 && 0 < tetromino.getThird_grid_y() - 1
                                && tetromino.getFourth_grid_x() + 2 < 10 && tetromino.getFourth_grid_y() - 2 < 19  && -1 < tetromino.getFourth_grid_x() + 2 && 0 < tetromino.getFourth_grid_y() - 2
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() + 1, tetromino.getThird_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() + 2, tetromino.getFourth_grid_y() - 2) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 2);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y());
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 1);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 2);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 2);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 4
                        else if(tetromino.getFirst_grid_x() - 1 < 10 && tetromino.getFirst_grid_y() + 1 < 19 && -1 < tetromino.getFirst_grid_x() - 1 && 0 < tetromino.getFirst_grid_y() + 1
                               && tetromino.getSecond_grid_x() - 1 < 10 && tetromino.getSecond_grid_y() + 3 < 19  && -1 < tetromino.getSecond_grid_x() - 1 && 0 < tetromino.getSecond_grid_y() + 3
                                && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() + 2 < 19  && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y() + 2
                                && tetromino.getFourth_grid_x() + 1 < 10 && tetromino.getFourth_grid_y() + 1 < 19  && -1 < tetromino.getFourth_grid_x() + 1 && 0 < tetromino.getFourth_grid_y() + 1
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() - 1, tetromino.getFirst_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() - 1, tetromino.getSecond_grid_y() + 3) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() + 1, tetromino.getFourth_grid_y() + 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() - 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 1);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 3);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 1);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 5
                        else if(tetromino.getFirst_grid_x() < 10 && tetromino.getFirst_grid_y() + 1 < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y() + 1
                                && tetromino.getSecond_grid_x() < 10 && tetromino.getSecond_grid_y() + 3 < 19  && -1 < tetromino.getSecond_grid_x() && 0 < tetromino.getSecond_grid_y() + 3
                                && tetromino.getThird_grid_x() + 1 < 10 && tetromino.getThird_grid_y() + 2 < 19  && -1 < tetromino.getThird_grid_x() + 1 && 0 < tetromino.getThird_grid_y() + 2
                                && tetromino.getFourth_grid_x() + 2 < 10 && tetromino.getFourth_grid_y() + 1 < 19  && -1 < tetromino.getFourth_grid_x() + 2 && 0 < tetromino.getFourth_grid_y() + 1
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y() + 3) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() + 1, tetromino.getThird_grid_y() + 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() + 2, tetromino.getFourth_grid_y() + 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() + 1);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x());
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() + 3);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() + 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 2);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        else{
                            break;
                        }
                        break;
//--------------------------------------------------------------------------------------------------------------------------------------------------------------
                    case 4:
                        //Test 1
                        if(tetromino.getFirst_grid_x() + 1 < 10 && tetromino.getFirst_grid_y() - 1 < 19 && -1 < tetromino.getFirst_grid_x() + 1 && 0 < tetromino.getFirst_grid_y() - 1
                                && tetromino.getSecond_grid_x() - 1 < 10 && tetromino.getSecond_grid_y() - 1 < 19  && -1 < tetromino.getSecond_grid_x() - 1 && 0 < tetromino.getSecond_grid_y() - 1
                                && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() < 19  && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() + 1 < 10 && tetromino.getFourth_grid_y() + 1 < 19  && -1 < tetromino.getFourth_grid_x() + 1 && 0 < tetromino.getFourth_grid_y() + 1
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 1, tetromino.getFirst_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() - 1, tetromino.getSecond_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() + 1, tetromino.getFourth_grid_y() + 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 1);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 1);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 2
                        else if(tetromino.getFirst_grid_x() < 10 && tetromino.getFirst_grid_y() - 1 < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y() - 1
                                && tetromino.getSecond_grid_x() - 2 < 10 && tetromino.getSecond_grid_y() - 1 < 19  && -1 < tetromino.getSecond_grid_x() - 2 && 0 < tetromino.getSecond_grid_y() - 1
                                && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y() < 19  && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y()
                                && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() + 1 < 19  && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y() + 1
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() - 2, tetromino.getSecond_grid_y() - 1) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y() + 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 1);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 2);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 1);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y());
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 3
                        else if(tetromino.getFirst_grid_x() < 10 && tetromino.getFirst_grid_y() < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y()
                                && tetromino.getSecond_grid_x() - 2 < 10 && tetromino.getSecond_grid_y() < 19  && -1 < tetromino.getSecond_grid_x() - 2 && 0 < tetromino.getSecond_grid_y()
                                && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y() + 1 < 19  && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y() + 1
                                && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() + 2 < 19  && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y() + 2
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() - 2, tetromino.getSecond_grid_y()) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y() + 1) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y() + 2) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y());
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 2);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y());
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() + 1);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 2);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 4
                        else if(tetromino.getFirst_grid_x() + 1 < 10 && tetromino.getFirst_grid_y() - 3 < 19 && -1 < tetromino.getFirst_grid_x() + 1 && 0 < tetromino.getFirst_grid_y() - 3
                                && tetromino.getSecond_grid_x() - 1 < 10 && tetromino.getSecond_grid_y() - 3 < 19  && -1 < tetromino.getSecond_grid_x() - 1 && 0 < tetromino.getSecond_grid_y() - 3
                                && tetromino.getThird_grid_x() < 10 && tetromino.getThird_grid_y() - 2 < 19  && -1 < tetromino.getThird_grid_x() && 0 < tetromino.getThird_grid_y() - 2
                                && tetromino.getFourth_grid_x() + 1 < 10 && tetromino.getFourth_grid_y() + 1 < 19  && -1 < tetromino.getFourth_grid_x() + 1 && 0 < tetromino.getFourth_grid_y() + 1
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x() + 1, tetromino.getFirst_grid_y() - 3) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() - 1, tetromino.getSecond_grid_y() - 3) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x(), tetromino.getThird_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x() + 1, tetromino.getFourth_grid_y() + 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x() + 1);
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 3);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 1);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 3);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x());
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x() + 1);
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() + 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        //Test 5
                        else if(tetromino.getFirst_grid_x() < 10 && tetromino.getFirst_grid_y() - 3 < 19 && -1 < tetromino.getFirst_grid_x() && 0 < tetromino.getFirst_grid_y() - 3
                                && tetromino.getSecond_grid_x() - 2 < 10 && tetromino.getSecond_grid_y() - 3 < 19  && -1 < tetromino.getSecond_grid_x() - 2 && 0 < tetromino.getSecond_grid_y() - 3
                                && tetromino.getThird_grid_x() - 1 < 10 && tetromino.getThird_grid_y() - 2 < 19  && -1 < tetromino.getThird_grid_x() - 1 && 0 < tetromino.getThird_grid_y() - 2
                                && tetromino.getFourth_grid_x() < 10 && tetromino.getFourth_grid_y() - 1 < 19  && -1 < tetromino.getFourth_grid_x() && 0 < tetromino.getFourth_grid_y() - 1
                                
                                && isEmptyGridWithException(tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y() - 3) == true
                                && isEmptyGridWithException(tetromino.getSecond_grid_x() - 2, tetromino.getSecond_grid_y() - 3) == true
                                && isEmptyGridWithException(tetromino.getThird_grid_x() - 1, tetromino.getThird_grid_y() - 2) == true
                                && isEmptyGridWithException(tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y() - 1) == true){
                            
                            tetromino.setFirst_grid_x(tetromino.getFirst_grid_x());
                            tetromino.setFirst_grid_y(tetromino.getFirst_grid_y() - 3);
                            
                            tetromino.setSecond_grid_x(tetromino.getSecond_grid_x() - 2);
                            tetromino.setSecond_grid_y(tetromino.getSecond_grid_y() - 3);
                            
                            tetromino.setThird_grid_x(tetromino.getThird_grid_x() - 1);
                            tetromino.setThird_grid_y(tetromino.getThird_grid_y() - 2);
                            
                            tetromino.setFourth_grid_x(tetromino.getFourth_grid_x());
                            tetromino.setFourth_grid_y(tetromino.getFourth_grid_y() - 1);

                            tetris_grid.getChildren().remove(tetromino.first);
                            tetris_grid.getChildren().remove(tetromino.second);
                            tetris_grid.getChildren().remove(tetromino.third);
                            tetris_grid.getChildren().remove(tetromino.fourth);

                            tetris_grid.add((tetromino.first), tetromino.getFirst_grid_x(), tetromino.getFirst_grid_y());
                            tetris_grid.add((tetromino.second), tetromino.getSecond_grid_x(), tetromino.getSecond_grid_y());
                            tetris_grid.add((tetromino.third), tetromino.getThird_grid_x(), tetromino.getThird_grid_y());
                            tetris_grid.add((tetromino.fourth), tetromino.getFourth_grid_x(), tetromino.getFourth_grid_y());

                            tetromino.RotationOfForm();
                        }
                        else{
                            break;
                        }
                        break;
                }
                break;
            }
        }
    }
    
    public boolean isOldTetrominoes(int x, int y){
        boolean isOld = false;
        for(int i = 0; i < oldTetrominoes.size(); i++){
            if(getNodeFromGridPane(x,y) == oldTetrominoes.get(i).first || getNodeFromGridPane(x,y) == oldTetrominoes.get(i).second
                    || getNodeFromGridPane(x,y) == oldTetrominoes.get(i).third || getNodeFromGridPane(x,y) == oldTetrominoes.get(i).fourth){
                isOld = true;
                break;
            }
        }
        return isOld;
    }
    
    public boolean isEmptyGrid(int x, int y){
        boolean isEmpty = true;
        if(getNodeFromGridPane(x, y) != null && isOldTetrominoes(x, y)){
            isEmpty = false;
        }
        return isEmpty;
    }
    
    public boolean isEmptyGridWithException(int x, int y){
        boolean isEmpty = true;
        if((getNodeFromGridPane(x,y) != null && getNodeFromGridPane(x,y) == tetromino.first && getNodeFromGridPane(x,y) == tetromino.second
                && getNodeFromGridPane(x,y) == tetromino.third && getNodeFromGridPane(x,y) == tetromino.fourth) || isOldTetrominoes(x, y)){
            isEmpty = false;
        }
        return isEmpty;
    }
    
    public void spawning(){
        //Random Tetromino
        int min = 1;
        int max = 7;
        int r = (int)(Math.random() * max) + min;
        
        switch (r) {
            case 1:
                new_tetromino = new Tetrominos_Shape("O");
                
                //checking if game is over stage 1               
                if(isEmptyGrid(4,1) == true && isEmptyGrid(5,1) == true && isEmptyGrid(4,2) == true && isEmptyGrid(5,2) == true){
                    new_tetromino.setFirst_grid_x(4);
                    new_tetromino.setFirst_grid_y(1);
                    new_tetromino.setSecond_grid_x(5);
                    new_tetromino.setSecond_grid_y(1);
                    new_tetromino.setThird_grid_x(4);
                    new_tetromino.setThird_grid_y(2);
                    new_tetromino.setFourth_grid_x(5);
                    new_tetromino.setFourth_grid_y(2);
                    tetris_grid.add((new_tetromino.first), new_tetromino.getFirst_grid_x(), new_tetromino.getFirst_grid_y());
                    tetris_grid.add((new_tetromino.second), new_tetromino.getSecond_grid_x(), new_tetromino.getSecond_grid_y());
                    tetris_grid.add((new_tetromino.third), new_tetromino.getThird_grid_x(), new_tetromino.getThird_grid_y());
                    tetris_grid.add((new_tetromino.fourth), new_tetromino.getFourth_grid_x(), new_tetromino.getFourth_grid_y());
                }
                
                else if(isEmptyGrid(4,0) == true && isEmptyGrid(5,0) == true && isEmptyGrid(4,1) == true && isEmptyGrid(5,1) == true){
                    new_tetromino.setFirst_grid_x(4);
                    new_tetromino.setFirst_grid_y(0);
                    new_tetromino.setSecond_grid_x(5);
                    new_tetromino.setSecond_grid_y(0);
                    new_tetromino.setThird_grid_x(4);
                    new_tetromino.setThird_grid_y(1);
                    new_tetromino.setFourth_grid_x(5);
                    new_tetromino.setFourth_grid_y(1);
                    tetris_grid.add((new_tetromino.first), new_tetromino.getFirst_grid_x(), new_tetromino.getFirst_grid_y());
                    tetris_grid.add((new_tetromino.second), new_tetromino.getSecond_grid_x(), new_tetromino.getSecond_grid_y());
                    tetris_grid.add((new_tetromino.third), new_tetromino.getThird_grid_x(), new_tetromino.getThird_grid_y());
                    tetris_grid.add((new_tetromino.fourth), new_tetromino.getFourth_grid_x(), new_tetromino.getFourth_grid_y());
                }
                
                else{
                    setIsGameOver(true);
                }
                
                break;
            case 2:
                new_tetromino = new Tetrominos_Shape("I");
                
                //checking if game is over stage 1               
                if(isEmptyGrid(3,2) == true && isEmptyGrid(4,2) == true && isEmptyGrid(5,2) == true && isEmptyGrid(6,2) == true){
                    new_tetromino.setFirst_grid_x(3);
                    new_tetromino.setFirst_grid_y(2);
                    new_tetromino.setSecond_grid_x(4);
                    new_tetromino.setSecond_grid_y(2);
                    new_tetromino.setThird_grid_x(5);
                    new_tetromino.setThird_grid_y(2);
                    new_tetromino.setFourth_grid_x(6);
                    new_tetromino.setFourth_grid_y(2);
                    tetris_grid.add((new_tetromino.first), new_tetromino.getFirst_grid_x(), new_tetromino.getFirst_grid_y());
                    tetris_grid.add((new_tetromino.second), new_tetromino.getSecond_grid_x(), new_tetromino.getSecond_grid_y());
                    tetris_grid.add((new_tetromino.third), new_tetromino.getThird_grid_x(), new_tetromino.getThird_grid_y());
                    tetris_grid.add((new_tetromino.fourth), new_tetromino.getFourth_grid_x(), new_tetromino.getFourth_grid_y());
                }
                
                else if(isEmptyGrid(3,1) == true && isEmptyGrid(4,1) == true && isEmptyGrid(5,1) == true && isEmptyGrid(6,1) == true){
                    new_tetromino.setFirst_grid_x(3);
                    new_tetromino.setFirst_grid_y(1);
                    new_tetromino.setSecond_grid_x(4);
                    new_tetromino.setSecond_grid_y(1);
                    new_tetromino.setThird_grid_x(5);
                    new_tetromino.setThird_grid_y(1);
                    new_tetromino.setFourth_grid_x(6);
                    new_tetromino.setFourth_grid_y(1);
                    tetris_grid.add((new_tetromino.first), new_tetromino.getFirst_grid_x(), new_tetromino.getFirst_grid_y());
                    tetris_grid.add((new_tetromino.second), new_tetromino.getSecond_grid_x(), new_tetromino.getSecond_grid_y());
                    tetris_grid.add((new_tetromino.third), new_tetromino.getThird_grid_x(), new_tetromino.getThird_grid_y());
                    tetris_grid.add((new_tetromino.fourth), new_tetromino.getFourth_grid_x(), new_tetromino.getFourth_grid_y());
                }
                
                else{
                    setIsGameOver(true);
                }
                
                break;
            case 3:
                new_tetromino = new Tetrominos_Shape("S");
                
                //checking if game is over stage 1               
                if(isEmptyGrid(5,1) == true && isEmptyGrid(4,1) == true && isEmptyGrid(4,2) == true && isEmptyGrid(3,2) == true){
                    new_tetromino.setFirst_grid_x(5);
                    new_tetromino.setFirst_grid_y(1);
                    new_tetromino.setSecond_grid_x(4);
                    new_tetromino.setSecond_grid_y(1);
                    new_tetromino.setThird_grid_x(4);
                    new_tetromino.setThird_grid_y(2);
                    new_tetromino.setFourth_grid_x(3);
                    new_tetromino.setFourth_grid_y(2);
                    tetris_grid.add((new_tetromino.first), new_tetromino.getFirst_grid_x(), new_tetromino.getFirst_grid_y());
                    tetris_grid.add((new_tetromino.second), new_tetromino.getSecond_grid_x(), new_tetromino.getSecond_grid_y());
                    tetris_grid.add((new_tetromino.third), new_tetromino.getThird_grid_x(), new_tetromino.getThird_grid_y());
                    tetris_grid.add((new_tetromino.fourth), new_tetromino.getFourth_grid_x(), new_tetromino.getFourth_grid_y());
                }
                
                else if(isEmptyGrid(5,0) == true && isEmptyGrid(4,0) == true && isEmptyGrid(4,1) == true && isEmptyGrid(3,1) == true){
                    new_tetromino.setFirst_grid_x(5);
                    new_tetromino.setFirst_grid_y(0);
                    new_tetromino.setSecond_grid_x(4);
                    new_tetromino.setSecond_grid_y(0);
                    new_tetromino.setThird_grid_x(4);
                    new_tetromino.setThird_grid_y(1);
                    new_tetromino.setFourth_grid_x(3);
                    new_tetromino.setFourth_grid_y(1);
                    tetris_grid.add((new_tetromino.first), new_tetromino.getFirst_grid_x(), new_tetromino.getFirst_grid_y());
                    tetris_grid.add((new_tetromino.second), new_tetromino.getSecond_grid_x(), new_tetromino.getSecond_grid_y());
                    tetris_grid.add((new_tetromino.third), new_tetromino.getThird_grid_x(), new_tetromino.getThird_grid_y());
                    tetris_grid.add((new_tetromino.fourth), new_tetromino.getFourth_grid_x(), new_tetromino.getFourth_grid_y());
                }
                
                else{
                    setIsGameOver(true);
                }
                
                break;
            case 4:
                new_tetromino = new Tetrominos_Shape("Z");
                
                //checking if game is over stage 1               
                if(isEmptyGrid(3,1) == true && isEmptyGrid(4,1) == true && isEmptyGrid(4,2) == true && isEmptyGrid(5,2) == true){
                    new_tetromino.setFirst_grid_x(3);
                    new_tetromino.setFirst_grid_y(1);
                    new_tetromino.setSecond_grid_x(4);
                    new_tetromino.setSecond_grid_y(1);
                    new_tetromino.setThird_grid_x(4);
                    new_tetromino.setThird_grid_y(2);
                    new_tetromino.setFourth_grid_x(5);
                    new_tetromino.setFourth_grid_y(2);
                    tetris_grid.add((new_tetromino.first), new_tetromino.getFirst_grid_x(), new_tetromino.getFirst_grid_y());
                    tetris_grid.add((new_tetromino.second), new_tetromino.getSecond_grid_x(), new_tetromino.getSecond_grid_y());
                    tetris_grid.add((new_tetromino.third), new_tetromino.getThird_grid_x(), new_tetromino.getThird_grid_y());
                    tetris_grid.add((new_tetromino.fourth), new_tetromino.getFourth_grid_x(), new_tetromino.getFourth_grid_y());
                }
                
                else if(isEmptyGrid(3,0) == true && isEmptyGrid(4,0) == true && isEmptyGrid(4,1) == true && isEmptyGrid(5,1) == true){
                    new_tetromino.setFirst_grid_x(3);
                    new_tetromino.setFirst_grid_y(0);
                    new_tetromino.setSecond_grid_x(4);
                    new_tetromino.setSecond_grid_y(0);
                    new_tetromino.setThird_grid_x(4);
                    new_tetromino.setThird_grid_y(1);
                    new_tetromino.setFourth_grid_x(5);
                    new_tetromino.setFourth_grid_y(1);
                    tetris_grid.add((new_tetromino.first), new_tetromino.getFirst_grid_x(), new_tetromino.getFirst_grid_y());
                    tetris_grid.add((new_tetromino.second), new_tetromino.getSecond_grid_x(), new_tetromino.getSecond_grid_y());
                    tetris_grid.add((new_tetromino.third), new_tetromino.getThird_grid_x(), new_tetromino.getThird_grid_y());
                    tetris_grid.add((new_tetromino.fourth), new_tetromino.getFourth_grid_x(), new_tetromino.getFourth_grid_y());
                }
                
                else{
                    setIsGameOver(true);
                }
                
                break;
            case 5:
                new_tetromino = new Tetrominos_Shape("L");
                
                //checking if game is over stage 1               
                if(isEmptyGrid(5,1) == true && isEmptyGrid(5,2) == true && isEmptyGrid(4,2) == true && isEmptyGrid(3,2) == true){
                    new_tetromino.setFirst_grid_x(5);
                    new_tetromino.setFirst_grid_y(1);
                    new_tetromino.setSecond_grid_x(5);
                    new_tetromino.setSecond_grid_y(2);
                    new_tetromino.setThird_grid_x(4);
                    new_tetromino.setThird_grid_y(2);
                    new_tetromino.setFourth_grid_x(3);
                    new_tetromino.setFourth_grid_y(2);
                    tetris_grid.add((new_tetromino.first), new_tetromino.getFirst_grid_x(), new_tetromino.getFirst_grid_y());
                    tetris_grid.add((new_tetromino.second), new_tetromino.getSecond_grid_x(), new_tetromino.getSecond_grid_y());
                    tetris_grid.add((new_tetromino.third), new_tetromino.getThird_grid_x(), new_tetromino.getThird_grid_y());
                    tetris_grid.add((new_tetromino.fourth), new_tetromino.getFourth_grid_x(), new_tetromino.getFourth_grid_y());
                }
                
                else if(isEmptyGrid(5,0) == true && isEmptyGrid(5,1) == true && isEmptyGrid(4,1) == true && isEmptyGrid(3,1) == true){
                    new_tetromino.setFirst_grid_x(5);
                    new_tetromino.setFirst_grid_y(0);
                    new_tetromino.setSecond_grid_x(5);
                    new_tetromino.setSecond_grid_y(1);
                    new_tetromino.setThird_grid_x(4);
                    new_tetromino.setThird_grid_y(1);
                    new_tetromino.setFourth_grid_x(3);
                    new_tetromino.setFourth_grid_y(1);
                    tetris_grid.add((new_tetromino.first), new_tetromino.getFirst_grid_x(), new_tetromino.getFirst_grid_y());
                    tetris_grid.add((new_tetromino.second), new_tetromino.getSecond_grid_x(), new_tetromino.getSecond_grid_y());
                    tetris_grid.add((new_tetromino.third), new_tetromino.getThird_grid_x(), new_tetromino.getThird_grid_y());
                    tetris_grid.add((new_tetromino.fourth), new_tetromino.getFourth_grid_x(), new_tetromino.getFourth_grid_y());
                }
                
                else{
                    setIsGameOver(true);
                }
                
                break;
            case 6:
                new_tetromino = new Tetrominos_Shape("J");
                
                //checking if game is over stage 1               
                if(isEmptyGrid(3,1) == true && isEmptyGrid(3,2) == true && isEmptyGrid(4,2) == true && isEmptyGrid(5,2) == true){
                    new_tetromino.setFirst_grid_x(3);
                    new_tetromino.setFirst_grid_y(1);
                    new_tetromino.setSecond_grid_x(3);
                    new_tetromino.setSecond_grid_y(2);
                    new_tetromino.setThird_grid_x(4);
                    new_tetromino.setThird_grid_y(2);
                    new_tetromino.setFourth_grid_x(5);
                    new_tetromino.setFourth_grid_y(2);
                    tetris_grid.add((new_tetromino.first), new_tetromino.getFirst_grid_x(), new_tetromino.getFirst_grid_y());
                    tetris_grid.add((new_tetromino.second), new_tetromino.getSecond_grid_x(), new_tetromino.getSecond_grid_y());
                    tetris_grid.add((new_tetromino.third), new_tetromino.getThird_grid_x(), new_tetromino.getThird_grid_y());
                    tetris_grid.add((new_tetromino.fourth), new_tetromino.getFourth_grid_x(), new_tetromino.getFourth_grid_y());
                }
                
                else if(isEmptyGrid(3,0) == true && isEmptyGrid(3,1) == true && isEmptyGrid(4,1) == true && isEmptyGrid(5,1) == true){
                    new_tetromino.setFirst_grid_x(3);
                    new_tetromino.setFirst_grid_y(0);
                    new_tetromino.setSecond_grid_x(3);
                    new_tetromino.setSecond_grid_y(1);
                    new_tetromino.setThird_grid_x(4);
                    new_tetromino.setThird_grid_y(1);
                    new_tetromino.setFourth_grid_x(5);
                    new_tetromino.setFourth_grid_y(1);
                    tetris_grid.add((new_tetromino.first), new_tetromino.getFirst_grid_x(), new_tetromino.getFirst_grid_y());
                    tetris_grid.add((new_tetromino.second), new_tetromino.getSecond_grid_x(), new_tetromino.getSecond_grid_y());
                    tetris_grid.add((new_tetromino.third), new_tetromino.getThird_grid_x(), new_tetromino.getThird_grid_y());
                    tetris_grid.add((new_tetromino.fourth), new_tetromino.getFourth_grid_x(), new_tetromino.getFourth_grid_y());
                }
                
                else{
                    setIsGameOver(true);
                }
                
                break;
            case 7:
                new_tetromino = new Tetrominos_Shape("T");
                
                //checking if game is over stage 1               
                if(isEmptyGrid(4,1) == true && isEmptyGrid(3,2) == true && isEmptyGrid(4,2) == true && isEmptyGrid(5,2) == true){
                    new_tetromino.setFirst_grid_x(4);
                    new_tetromino.setFirst_grid_y(1);
                    new_tetromino.setSecond_grid_x(3);
                    new_tetromino.setSecond_grid_y(2);
                    new_tetromino.setThird_grid_x(4);
                    new_tetromino.setThird_grid_y(2);
                    new_tetromino.setFourth_grid_x(5);
                    new_tetromino.setFourth_grid_y(2);
                    tetris_grid.add((new_tetromino.first), new_tetromino.getFirst_grid_x(), new_tetromino.getFirst_grid_y());
                    tetris_grid.add((new_tetromino.second), new_tetromino.getSecond_grid_x(), new_tetromino.getSecond_grid_y());
                    tetris_grid.add((new_tetromino.third), new_tetromino.getThird_grid_x(), new_tetromino.getThird_grid_y());
                    tetris_grid.add((new_tetromino.fourth), new_tetromino.getFourth_grid_x(), new_tetromino.getFourth_grid_y());
                }
                
                else if(isEmptyGrid(4,0) == true && isEmptyGrid(3,1) == true && isEmptyGrid(4,1) == true && isEmptyGrid(5,1) == true){
                    new_tetromino.setFirst_grid_x(4);
                    new_tetromino.setFirst_grid_y(0);
                    new_tetromino.setSecond_grid_x(3);
                    new_tetromino.setSecond_grid_y(1);
                    new_tetromino.setThird_grid_x(4);
                    new_tetromino.setThird_grid_y(1);
                    new_tetromino.setFourth_grid_x(5);
                    new_tetromino.setFourth_grid_y(1);
                    tetris_grid.add((new_tetromino.first), new_tetromino.getFirst_grid_x(), new_tetromino.getFirst_grid_y());
                    tetris_grid.add((new_tetromino.second), new_tetromino.getSecond_grid_x(), new_tetromino.getSecond_grid_y());
                    tetris_grid.add((new_tetromino.third), new_tetromino.getThird_grid_x(), new_tetromino.getThird_grid_y());
                    tetris_grid.add((new_tetromino.fourth), new_tetromino.getFourth_grid_x(), new_tetromino.getFourth_grid_y());
                }
                
                else{
                    setIsGameOver(true);
                }
                
                break;
        }
        if(getIsGameOver() == false){
            setTetromino(getNew_tetromino());
        }
    }

    public void cleaner(){
        int howMuchClean = 0;
        for(int row = 0; row < 19; row++){
            boolean isCleaning = false;
            int notEmptyCounter = 0;
            for(int col = 0; col < 10; col++){
                if(isEmptyGridWithException(col, row) == false){
                    notEmptyCounter++;
                }
            }
        if(notEmptyCounter == 10){
            howMuchClean++;
            for(int col = 0; col < 10; col++){
                removeFromGridPane(col, row);
            }
            int d_row = row;
            for(; d_row > 2; d_row--){
                for(int col = 0; col < 10; col++){
                    try{
                    tetris_grid.add((getNodeFromGridPane(col, d_row-1)), col, d_row);
                    }
                    catch(java.lang.NullPointerException | java.lang.IllegalArgumentException e){
                    }
                }
            }
        }
        }
        int new_score;
        int new_level;
        switch(howMuchClean){
            case 1:
                new_score = Integer.parseInt(score.getText().substring(7)) + (Integer.parseInt(level.getText().substring(7)) + 1) * (100);
                score.setText("SCORE: " + Integer.toString(new_score));
                break;
            case 2:
                new_score = Integer.parseInt(score.getText().substring(7)) + (Integer.parseInt(level.getText().substring(7)) + 1) * (300);
                score.setText("SCORE: " + Integer.toString(new_score));
                break;
            case 3:
                new_score = Integer.parseInt(score.getText().substring(7)) + (Integer.parseInt(level.getText().substring(7)) + 1) * (500);
                score.setText("SCORE: " + Integer.toString(new_score));
                break;
            case 4:
                new_score = Integer.parseInt(score.getText().substring(7)) + (Integer.parseInt(level.getText().substring(7)) + 1) * (800);
                score.setText("SCORE: " + Integer.toString(new_score));
                break;
        }
        
        if(howMuchClean > 0){
            cleaned_lines = cleaned_lines + howMuchClean;
            if(cleaned_lines != 0 && Math.floorDiv(cleaned_lines, 10) > Integer.parseInt(level.getText().substring(7))){
                new_level = Math.floorDiv(cleaned_lines, 10);
                level.setText("LEVEL: " + Integer.toString(new_level));
            }
        }
    }
    
    TimerTask spawner = new TimerTask(){
        @Override
        public void run() {
            Platform.runLater(() -> {
                if(getIsPaused() == false){
                    if(getIsGameOver() == false){
                        move_down(tetromino);
                        //scoring
                    }
                    else{
                        setIsGameOver(true);
                        tetris_mediaplayer.stop();
                        ghostground.setImage(gameover_image);
                        score_gameover.setText(score.getText().substring(7));
                        level_gameover.setText(level.getText().substring(7));
                        ghostground.setMouseTransparent(false);
                        score_gameover.setMouseTransparent(false);
                        level_gameover.setMouseTransparent(false);
                        exit.setVisible(true);
                        go_to_mainmenu_gameover.setVisible(true);
                        exit.setDisable(false);
                        go_to_mainmenu_gameover.setDisable(false);
                        drop.cancel();
                    }
                }
                });
        }
        
    };

    private void Pause(){
        boolean temp = pause_clicker;
        pause_clicker = getIsPaused();
        setIsPaused(temp);
        if(getIsPaused()){
            tetris_mediaplayer.pause();
            ghostground.setImage(paused_image);
            ghostground.setMouseTransparent(false);
            continue_game.setVisible(true);
            continue_game.setDisable(false);
        }
        else{
            if(IsMplayer == true){
                tetris_mediaplayer.play();
            }
            else{
                tetris_mediaplayer.pause();
            }
            ghostground.setImage(null);
            ghostground.setMouseTransparent(true);
            continue_game.setVisible(false);
            continue_game.setDisable(true);
        }
    }
    
    public boolean getIsPaused() {
        return isPaused;
    }

    public void setIsPaused(boolean isPaused) {
        this.isPaused = isPaused;
    }
    
    public boolean getIsGameOver() {
        return isGameOver;
    }

    public void setIsGameOver(boolean isGameOver) {
        this.isGameOver = isGameOver;
    }

    public Tetrominos_Shape getTetromino() {
        return tetromino;
    }

    public void setTetromino(Tetrominos_Shape tetromino) {
        this.tetromino = tetromino;
    }

    public Tetrominos_Shape getNew_tetromino() {
        return new_tetromino;
    }

    public void setNew_tetromino(Tetrominos_Shape new_tetromino) {
        this.new_tetromino = new_tetromino;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        background.setImage(background_image_default);
        
        music.setImage(music_on_image);
        
        tetris_mediaplayer.setAutoPlay(IsMplayer);
        tetris_mediaplayer.setCycleCount(MediaPlayer.INDEFINITE);
        
        score.setText("SCORE: " + Integer.toString(0));
        level.setText("LEVEL: " + Integer.toString(0));
        spawning();
        drop.schedule(spawner, 300, 900);
        
    }    
    
}
