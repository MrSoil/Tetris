/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tetris;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;



public class Tetrominos_Shape {
    // rectangle javafxteki metot
    Rectangle first = new Rectangle(TetrisGame.TETROMINOES_SIZE, TetrisGame.TETROMINOES_SIZE);
    Rectangle second = new Rectangle(TetrisGame.TETROMINOES_SIZE, TetrisGame.TETROMINOES_SIZE);
    Rectangle third = new Rectangle(TetrisGame.TETROMINOES_SIZE, TetrisGame.TETROMINOES_SIZE);
    Rectangle fourth = new Rectangle(TetrisGame.TETROMINOES_SIZE, TetrisGame.TETROMINOES_SIZE);
    //
    Image yellow = new Image(getClass().getResourceAsStream("/tetraminos/yellow_tetramino_block.JPG"));
    Image cyan = new Image(getClass().getResourceAsStream("/tetraminos/cyan_tetramino_block.JPG"));
    Image red = new Image(getClass().getResourceAsStream("/tetraminos/red_tetramino_block.JPG"));
    Image green = new Image(getClass().getResourceAsStream("/tetraminos/green_tetramino_block.JPG"));
    Image orange = new Image(getClass().getResourceAsStream("/tetraminos/orange_tetramino_block.JPG"));
    Image pink = new Image(getClass().getResourceAsStream("/tetraminos/pink_tetramino_block.JPG"));
    Image purple = new Image(getClass().getResourceAsStream("/tetraminos/purple_tetramino_block.JPG"));
    //
    
    private ImagePattern texture;
    
    private String name; 
    
    private int form = 1;
    
    private int first_grid_x;
    
    private int first_grid_y;
    
    private int second_grid_x;
    
    private int second_grid_y;
    
    private int third_grid_x;
    
    private int third_grid_y;
    
    private int fourth_grid_x;
    
    private int fourth_grid_y;
    
    // renkleri belirtirken kullancaz
    
    public Tetrominos_Shape (String name) {
        this.name = name;

        switch(name) {
        case "O":
            texture = new ImagePattern(yellow);
            break;
        case "I":
            texture = new ImagePattern(cyan);
            break;
        case "S":
            texture = new ImagePattern(red);
            break;
        case "Z":
            texture = new ImagePattern(green);
            break;
        case "L":
            texture = new ImagePattern(orange);
            break;
        case "J":
            texture = new ImagePattern(pink);
            break;
        case "T":
            texture = new ImagePattern(purple);
            break;
        }
	
        this.first.setFill(this.texture);
        this.second.setFill(this.texture);
        this.third.setFill(this.texture);
        this.fourth.setFill(this.texture);
    }

    public void RotationOfForm(){
        if(getForm() != 4){
            setForm(getForm() + 1);
        }
        else if(getForm() == 4){
            setForm(1);
        }
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getForm() {
        return form;
    }

    public void setForm(int form) {
        this.form = form;
    }

    public int getFirst_grid_x() {
        return first_grid_x;
    }

    public void setFirst_grid_x(int first_grid_x) {
        this.first_grid_x = first_grid_x;
    }

    public int getFirst_grid_y() {
        return first_grid_y;
    }

    public void setFirst_grid_y(int first_grid_y) {
        this.first_grid_y = first_grid_y;
    }

    public int getSecond_grid_x() {
        return second_grid_x;
    }

    public void setSecond_grid_x(int second_grid_x) {
        this.second_grid_x = second_grid_x;
    }

    public int getSecond_grid_y() {
        return second_grid_y;
    }

    public void setSecond_grid_y(int second_grid_y) {
        this.second_grid_y = second_grid_y;
    }

    public int getThird_grid_x() {
        return third_grid_x;
    }

    public void setThird_grid_x(int third_grid_x) {
        this.third_grid_x = third_grid_x;
    }

    public int getThird_grid_y() {
        return third_grid_y;
    }

    public void setThird_grid_y(int third_grid_y) {
        this.third_grid_y = third_grid_y;
    }

    public int getFourth_grid_x() {
        return fourth_grid_x;
    }

    public void setFourth_grid_x(int fourth_grid_x) {
        this.fourth_grid_x = fourth_grid_x;
    }

    public int getFourth_grid_y() {
        return fourth_grid_y;
    }

    public void setFourth_grid_y(int fourth_grid_y) {
        this.fourth_grid_y = fourth_grid_y;
    }

    
    
}
