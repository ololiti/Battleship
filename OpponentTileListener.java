/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battleshipproject;

/**
 *
 * @author Aditi
 */
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
public class OpponentTileListener extends MouseAdapter{
    private Coordinate c;
    private static BattleshipTurns turns;
    public OpponentTileListener(Coordinate my){
        c = my;
    }
    public void mouseClicked(MouseEvent e){
        if (TileListener.allShips){
            turns.attack(c);
        }
    }
    public static void setTurns(BattleshipTurns b){
        turns = b;
    }
}
