/*
 * Aditi Talati - 9th period T/Th - due May 28, 2018
 * Final Project - make a game of battleship that can be played over 
                   multiple devices
 */
package battleshipproject;

/**
 *
 * @author Aditi
 */
import java.io.Serializable;
public class Coordinate implements Serializable{
    public int x;
    public int y;
    boolean hit;
    boolean selected;
    public Coordinate(int x, int y){
        this.x = x;
        this.y = y;
        hit = false;
        selected = false;
    }
    public boolean equals(Coordinate other){
        if (other.x == x && other.y == y) return true;
        return false;
    }
}
