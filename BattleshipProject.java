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
import java.net.InetAddress;
import java.net.UnknownHostException;
public class BattleshipProject {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        WelcomeScreen welcome = new WelcomeScreen();
        welcome.setVisible(true);
    }
    
}
