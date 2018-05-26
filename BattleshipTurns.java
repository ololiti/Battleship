/*
 * Aditi Talati - 9th period T/Th - due May 28, 2018
 * Final Project - make a game of battleship that can be played over 
                   multiple devices
 */
package battleshipproject;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Aditi
 */
public class BattleshipTurns implements Runnable{
    private Socket socket;
    private int port = 7778;
    private MainScreen main;
    private Thread instreamHandler;
    private Thread outstreamHandler;
    private GameOutputStreamHandler out;
    private GameInputStreamHandler in;
    private static boolean server = false;
    public BattleshipTurns(MainScreen m){
        main = m;
    }
    public void run() {
        System.out.println("starting game client");
        System.out.println("Trying to start client");
        startClient();
        if (socket == null) {
            System.out.println("BattleshipTurns: starting game server");
            System.out.println("Client Failed -- Starting Server");
            main.setInstructions("Waiting for opponenent...");
            System.out.println("set instructions");
            startServer();
        }
    
    }
    private void startClient() {

        try {
            // Create a connection to the server socket 
            Scanner s = new Scanner(System.in);
            System.out.print("What is the IP address you are trying to connect to? " );
            InetAddress host = InetAddress.getByName(s.next());
            socket = new Socket(host.getHostName(), port);
            System.out.println("Client connected to Server");
            //Create input and output threads
            System.out.println("Creating Chat Input and OutputStream");
            out = new GameOutputStreamHandler(socket, false, main);
            outstreamHandler = new Thread(out);
            instreamHandler = new Thread(
               in = new GameInputStreamHandler(socket, false, main, out, this));

            //Srart input and output threads.
            instreamHandler.start();
            outstreamHandler.start();

        } catch (UnknownHostException e) {
            System.out.println("Unknown Host");
            System.out.println(e);
        } catch (IOException e) {
            System.out.println("No Server Found");
            System.out.println(e);
        }
    }
    void startServer() {

        try {
            //Starting server thread and waiting for client
            ServerSocket server = new ServerSocket(port);
            System.out.println("Waiting for client message...");
            socket = server.accept();
            System.out.println("Connected to Client");
            out = new GameOutputStreamHandler(socket, true, main);
            outstreamHandler = new Thread(out);
            instreamHandler = new Thread(
                in = new GameInputStreamHandler(socket, true, main, out, this));
            outstreamHandler.start();
            instreamHandler.start();

        } catch (IOException e) {
            System.out.println("Error starting server");
            System.out.println(e);;
        }
    }
    public GameOutputStreamHandler getOutput(){
        return out;
    }
    public void attack(Coordinate c){
        out.attack(c);
    }
    public void gameOver(){
        out.gameOver = true;
        in.gameOver = true;
    }
    class GameOutputStreamHandler implements Runnable {

        private ObjectOutputStream outstream;
        Socket socket;
        private boolean myTurn;
        private MainScreen main;
        private Coordinate attackTile;
        private Coordinate recieved;
        private boolean newRecieved;
        private boolean understood = false;
        private boolean gameOver = false;

        public GameOutputStreamHandler(Socket socket, boolean turn, 
                                                      MainScreen m) {

            System.out.println("Creating OutputStreamHandler");
            this.socket = socket;
            main = m;
            setTurn(turn);
        }
        public void setTurn(boolean turn){
            myTurn = turn;
            if (myTurn) main.setInstructions("Select a tile on your opponent's "
                                           + "grid to attack.");
            else main.setInstructions("Opponent's turn!");
        }

        /*
         * Thread run method
         */
        public void run() {
            System.out.println("started output");
            try{
                outstream = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException e){
                System.out.println(e);
            }  
            while (!gameOver){
                try {
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
                if (myTurn){
                    if (attackTile != null) {
                        try{
                            System.out.println("sending tile");
                            outstream.writeObject(attackTile);
                            main.hit(attackTile);
                            setTurn(false);
                            attackTile = null;
                        } catch (IOException e){
                            System.out.println(e);
                        }
                    }
                } else if (newRecieved){
                    try{
                        System.out.println("sending boolean");
                        outstream.writeBoolean(main.attacked(recieved));
                        newRecieved = false;
                        setTurn(true);
                    } catch (IOException e){
                        System.out.println(e);
                    }
                }
            }
            
        }
        public void attack(Coordinate c){
            if (myTurn) attackTile = c;
        }
        public void defend(Coordinate c){
            System.out.println("entered defend");
            recieved = c;
            newRecieved = true;
        }
    }
    class GameInputStreamHandler implements Runnable {

        ObjectInputStream instream;
        Socket socket;
        final boolean goesFirst;
        MainScreen main;
        GameOutputStreamHandler out;
        BattleshipTurns turns;
        private boolean gameOver = false;

        public GameInputStreamHandler(Socket socket, boolean first, 
                                      MainScreen m,
                                      GameOutputStreamHandler out,
                                      BattleshipTurns b) {
            System.out.println("Creating InputStreamHandler");
            this.socket = socket;
            goesFirst = first;
            main = m;
            this.out = out;
            turns = b;
            System.out.println("input stream created -  first: " + goesFirst);
        }


        /*
         * Thread run method
         */    
        public void run() {
            boolean stopRequested = false;
            try {
                //Open input object stream
                System.out.println("Starting input stream");
                instream = new ObjectInputStream(socket.getInputStream());

                //Keep running until user enters "Bye"
                while (!gameOver) {
                    if (goesFirst){
                        main.hitOrMiss(instream.readBoolean());
                        out.defend((Coordinate)instream.readObject());
                    } else {
                        out.defend((Coordinate)instream.readObject());
                        main.hitOrMiss(instream.readBoolean());
                    }
                } 
            } catch (IOException e) {
                System.out.println(e);
            } catch (ClassNotFoundException e){
                System.out.println(e);
            }      
        }
    }
}

