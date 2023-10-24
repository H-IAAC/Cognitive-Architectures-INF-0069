/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package structures;

/**
 *
 * @author bruno
 */
public class Position {
    
    private int x;
    private int y;
    public boolean occupied;
    
    public Position(){
        this.x = 0;
        this.y = 0;
        this.occupied = false;
    }
    
    public Position(int x, int y){
        this.x = x;
        this.y = y;
        this.occupied = false;
    }
    
    public void setPos(Position pos){
        this.x = pos.getX();
        this.y = pos.getY();
    }
    
    public int getX(){
        return this.x;
    }
    
    public int getY(){
        return this.y;
    }
    
    public void setX(int x){
        this.x = x;
    }
    
    public void setY(int y){
        this.y = y;
    }
    
    public String coord(){
        return "X: " + x + " - Y: " + y;
    }
    
    public static int distBetween(Position pos1, Position pos2){
        return Math.abs(pos1.getX() - pos2.getX()) + Math.abs(pos1.getY() - pos2.getY());
    }
    
}
