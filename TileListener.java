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
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
public class TileListener extends MouseAdapter{
    public static Coordinate[] ships = new Coordinate[33];
    public static int shipsSelected = 0;
    public static boolean allShips = false;
    public static int currentTiles = 5;
    
    private WelcomeScreen main;
    private Coordinate c;
    public TileListener(Coordinate c, WelcomeScreen m){
        this.c = c;
        main = m;
    }
    public void mouseClicked(MouseEvent e){
        if(!allShips){
            if (main.select(c)){
                switch(shipsSelected){
                    case 5: main.setInstructions("Current Ship:",
                                                 "battleship 1",
                                                 "size: 4 tiles");
                            currentTiles = 4;
                            break;
                    case 9: main.setInstructions("Current Ship:",
                                                 "battleship 2",
                                                 "size: 4 tiles");
                            break;
                    case 13:main.setInstructions("Current Ship:",
                                                 "submarine 1",
                                                 "size: 3 tiles");
                            currentTiles = 3;
                            break;
                    case 16:main.setInstructions("Current Ship:",
                                                 "submarine 2",
                                                 "size: 3 tiles");
                            break;
                    case 19:main.setInstructions("Current Ship:",
                                                 "destroyer 1",
                                                 "size: 3 tiles");
                            break;
                    case 22:main.setInstructions("Current Ship",
                                                 "destroyer 2",
                                                 "size: 3 tiles");
                            break;
                    case 25:main.setInstructions("Current Ship",
                                                 "patrol boat 1",
                                                 "size: 2 tiles");
                            currentTiles = 2;
                            break;
                    case 27:main.setInstructions("Current Ship",
                                                 "patrol boat 2",
                                                 "size: 2 tiles");
                            break;
                    case 29:main.setInstructions("Current Ship",
                                                 "patrol boat 3",
                                                 "size: 2 tiles");
                            break;   
                    case 31:main.setInstructions("Current Ship",
                                                 "patrol boat 4",
                                                 "size: 2 tiles");
                            break;
                    case 33:main.setInstructions("You have finished", 
                                                 "selecting" +
                                                 " boats!", "");
                            allShips = true;
                            main.done();
                            break;
                }
            } 
        } 
    }
}
/*allShips = true;
                            BattleshipTurns turns = new BattleshipTurns(main);
                            OpponentTileListener.setTurns(turns);
                            main.setTurns(turns);
                            Thread t = new Thread(turns);
                            t.start();
*/