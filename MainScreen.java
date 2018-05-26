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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class MainScreen extends JFrame{
    public static final int WIDTH = 950;
    public static final int HEIGHT = 650;
    public static final Color PURPLE = new Color(93,46,140);
    public static final Font TEXT_FONT= new Font("Courier", Font.BOLD, 12);
    public static final Color YELLOW = new Color(241,232,184);
    public static final Font LABEL_FONT = new Font("Luminari", Font.BOLD, 20);
    public static final Color BLUE = new Color(46,196,182);
    public static final int GRID = 20;
    public static final Color RED = new Color(255,102,102);
    public static final Color GREEN = new Color(204,255,102);
    
    private Coordinate currentTile;
    private JLabel instructions;
    private Socket socket;
    private int port = 7777;
    private JTextField enterText;
    private JTextArea chatWindow;
    private JButton send;
    private JPanel[][] opponentTiles;
    private JPanel[][] myTiles;
    private int numHit = 0;
    private int hitMe = 0;
    private BattleshipTurns turns;
    
    
    public MainScreen(JPanel[][] welcomeScreenTileArray){
        //create JFrame
        super("BATTLESHIP!");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setBackground(PURPLE);
        
        //create bottom panel
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new FlowLayout());
        chatPanel.setBackground(PURPLE);
        //create submit text button
        enterText = new JTextField(50);
        enterText.setFont(TEXT_FONT);
        enterText.setBackground(YELLOW);
        enterText.setForeground(PURPLE);
        chatPanel.add(enterText);
        
        send = new JButton("send");
        send.setFont(LABEL_FONT);
        send.setBackground(BLUE);
        send.setForeground(YELLOW);
        send.setBorderPainted(false);
        send.setOpaque(true);
        chatPanel.add(send);
        
        
        //create text display
        chatWindow = new JTextArea(15,40);
        chatWindow.setFont(TEXT_FONT);
        chatWindow.setBackground(YELLOW);
        chatWindow.setForeground(PURPLE);
        chatWindow.setEditable(false);
        chatWindow.setLineWrap(true);
        JScrollPane chatScroll = new JScrollPane(chatWindow);
        chatScroll.setVerticalScrollBarPolicy
                                    (JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatPanel.add(chatScroll);
        
        add(chatPanel, BorderLayout.SOUTH);
        //make the lines alternate colors depending on who's speaking
        //make display scrollable?
        
        //create main panel
        JPanel gamePanel = new JPanel();
        gamePanel.setBackground(PURPLE);
        gamePanel.setLayout(new GridLayout());
        //create opponent grid
        JPanel opponentSide = new JPanel();
        opponentSide.setBackground(PURPLE);
        opponentSide.setLayout(new BorderLayout());
        
        JPanel opponentGrid = new JPanel();
        opponentGrid.setBackground(PURPLE);
        opponentGrid.setLayout(new GridLayout(GRID, GRID));
        opponentTiles = new JPanel[GRID][GRID];
        for(int i = 0; i < GRID; i++){
            for (int j = 0; j < GRID; j++){
                opponentTiles[i][j] = new JPanel();
                opponentTiles[i][j].setBackground(BLUE);
                opponentTiles[i][j].addMouseListener(new OpponentTileListener
                                                    (new Coordinate(i,j)));
            }
        }
        for(JPanel[] arr: opponentTiles){
            for (JPanel j: arr){
                opponentGrid.add(j);
            }
        }
        opponentSide.add(opponentGrid,BorderLayout.CENTER);
        //create label
        JLabel opponentLabel = new JLabel("OPPONENT GRID");
        opponentLabel.setForeground(BLUE);
        opponentLabel.setFont(LABEL_FONT);
        opponentSide.add(opponentLabel, BorderLayout.NORTH);
        
        gamePanel.add(opponentSide);
        
        //create my grid
        JPanel mySide = new JPanel();
        mySide.setBackground(PURPLE);
        mySide.setLayout(new BorderLayout());
        
        JPanel myGrid = new JPanel();
        myGrid.setBackground(PURPLE);
        myGrid.setLayout(new GridLayout(GRID, GRID));
        myTiles = welcomeScreenTileArray;
        for(JPanel[] j: myTiles)
            for (JPanel k: j)
                myGrid.add(k);
        mySide.add(myGrid, BorderLayout.CENTER);
        
        //create label
        JPanel myLabelPanel = new JPanel();
        myLabelPanel.setBackground(PURPLE);
        myLabelPanel.setLayout(new BorderLayout());
        JLabel myLabel = new JLabel("YOUR GRID");
        myLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        myLabel.setForeground(BLUE);
        myLabel.setFont(LABEL_FONT);
        myLabelPanel.add(myLabel, BorderLayout.NORTH);
        instructions = new JLabel("");
        instructions.setForeground(BLUE);
        instructions.setFont(LABEL_FONT);
        myLabelPanel.add(instructions, BorderLayout.SOUTH);
        mySide.add(myLabelPanel, BorderLayout.NORTH);
        
        gamePanel.add(mySide);
        
        add(gamePanel,BorderLayout.CENTER);
        start();
    }
    
    public void setInstructions(String newInstructions){
        instructions.setText(newInstructions);
    }
    
    /**
     * First try as a client, if it fails then start as a client; Then create
     * two separate threads one for input and another for output
     */
    private void start() {

        System.out.println("Trying to start client");
        startClient(port);
        if (socket == null) {
            System.out.println("MainScreen: Client Failed -- Starting Server");
            startServer(port);
        }

        //Create input and output threads
        System.out.println("Creating Chat Input and OutputStream");
        Thread instreamHandler = new Thread(
                new ChatInputStreamHandler(socket, chatWindow));
        ChatOutputStreamHandler output = 
                new ChatOutputStreamHandler(socket, enterText, chatWindow);
        Thread outstreamHandler = new Thread(output);
        send.addActionListener(output);
        
        //Srart input and output threads.
        instreamHandler.start();
        outstreamHandler.start();
    
    }
    public boolean attacked(Coordinate c){
        c.hit = true;
        boolean hit = false;
        for (Coordinate d: TileListener.ships){
            if (c.equals(d)){
                d.hit = true;
                hit = true;
                hitMe ++;
                if (hitMe == 33){
                    setInstructions("Game over! You have lost.");
                    turns.gameOver();
                }
                myTiles[d.x][d.y].setBackground(RED);
                setInstructions("You hit the opponent's ship!");
                break;
            }
        }
        if (!hit) myTiles[c.x][c.y].setBackground(GREEN);
        return hit;
    }
    public void hit(Coordinate c){
        System.out.println("set tile to attack");
        currentTile = c;
        opponentTiles[currentTile.x][currentTile.y].setBackground(YELLOW);
    }
    public void hitOrMiss(boolean hit){
        System.out.println("entered hit or miss");
        if (hit){
            numHit ++;
            if (numHit == 33){
                setInstructions("Congrats! You have won the game.");
                turns.gameOver();
            }
            opponentTiles[currentTile.x][currentTile.y].setBackground(RED);
        }
        else opponentTiles[currentTile.x][currentTile.y].setBackground(PURPLE);
    }
    public void setTurns(BattleshipTurns t){
        turns = t;
    }

    /**
     * Start the cline on local host and port 7777
     */
    private void startClient(int port) {

        try {
            // Create a connection to the server socket 
            Scanner s = new Scanner(System.in);
            System.out.print("What is the IP address you are trying to connect to? " );
            InetAddress host = InetAddress.getByName(s.next());
            s.close();
            socket = new Socket(host.getHostName(), port);
            System.out.println("Client connected to Server");

        } catch (UnknownHostException e) {
            System.out.println("Unknown Host");
            System.out.println(e);
        } catch (IOException e) {
            System.out.println("No Server Found");
            System.out.println(e);
        }
    }

    /**
     * Start the server on port 7777
     */
    void startServer(int port) {

        try {
            //Starting servr and waiting for client
            ServerSocket server = new ServerSocket(port);
            System.out.println("Waiting for client message...");
            socket = server.accept();
            System.out.println("Connected to Client");

        } catch (IOException e) {
            System.out.println("Error starting server");
            System.out.println(e);
        }
    }
    
}
/**
 * A class that creates a thread to handle output on the socket
 *
 * @author Thananjeyan
 */
class ChatOutputStreamHandler implements Runnable, ActionListener {

    private ObjectOutputStream outstream;
    Socket socket;
    private String message;
    private boolean newMessage = false;
    private JTextField text;
    private JTextArea output;

    public ChatOutputStreamHandler(Socket socket, JTextField text,
                                   JTextArea output) {

        System.out.println("Creating OutputStreamHandler");
        this.socket = socket;
        this.text = text;
        this.output = output;
    }

    
    /*
     * Thread run method
     */
    public void run() {

        boolean stopRequested = false;
        try {

            // Open output object stream
            System.out.println("Starting output streams");
            outstream = new ObjectOutputStream(socket.getOutputStream());

            
            while (!stopRequested) {
                //System.out.print("");
            
                try {
                    Thread.sleep(10); // yield for some time
                } catch (InterruptedException ignore) {
                }
                if (newMessage){
                    //System.out.println("message being sent");
                    newMessage = false;
                    outstream.writeObject(message);
                    output.append("\nYou: " + message);
                }
            }
        } catch (IOException e) {
            System.out.println(e);;
        }
    }
    public void actionPerformed(ActionEvent e){
        message = text.getText();
        text.setText("");
        newMessage = true;
        //System.out.println("actionPerformed "+newMessage);
    }
}
/**
 * A class that creates a thread to handle input on the socket
 *
 * @author Thananjeyan
 */
class ChatInputStreamHandler implements Runnable {

    ObjectInputStream instream;
    Socket socket;
    JTextArea output;

    public ChatInputStreamHandler(Socket socket, JTextArea output) {

        System.out.println("Creating InputStreamHandler");
        this.socket = socket;
        this.output = output;
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
            while (!stopRequested) {
                String message = (String) instream.readObject();
                //System.out.println("message recieved");
                output.append("\nOpponent: " + message);
                if (message.equals("Bye")) {
                    stopRequested = true;
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        }        
    }
}
