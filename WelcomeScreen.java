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
public class WelcomeScreen extends JFrame implements ActionListener{
    public static final int WIDTH = 700;
    public static final int HEIGHT = 300;
    public static final Color BACKGROUND = new Color(229,242,255);
    public static final Color PURPLE = new Color(93,46,140);
    public static final Font LABEL_FONT = new Font("Luminari", Font.BOLD, 20);
    public static final Color BLUE = new Color(46,196,182);
    
    private JPanel[][] myTiles = new JPanel[MainScreen.GRID][MainScreen.GRID];
    private Coordinate[][] tileNumbers = 
                               new Coordinate[MainScreen.GRID][MainScreen.GRID];
    private JLabel instructionLabel;
    private JLabel instructionLabel2;
    private JLabel instructionLabel3;
    private JButton start;
    private boolean horizontal = true;
    public WelcomeScreen(){
        //create JFrame
        super("Welcome to Battleship!");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        
        //create welcome label/instructions
        JPanel welcome = new JPanel();
        welcome.setLayout(new GridLayout(2,1));
        welcome.setBackground(PURPLE);
        JLabel welcomeLabel = new JLabel("Welcome to battleship!");
        JLabel welcomeLabel2 = new JLabel("Please select the locations "
                                        + "of your ships.");
        welcomeLabel.setFont(LABEL_FONT);
        welcomeLabel2.setFont(LABEL_FONT);
        welcomeLabel.setForeground(BLUE);
        welcomeLabel2.setForeground(BLUE);
        welcome.add(welcomeLabel);
        welcome.add(welcomeLabel2);
        add(welcome,BorderLayout.NORTH);
        
        //create grid
        JPanel mySide = new JPanel();
        mySide.setBackground(PURPLE);
        mySide.setLayout(new BorderLayout());
        
        JPanel myGrid = new JPanel();
        myGrid.setBackground(PURPLE);
        myGrid.setLayout(new GridLayout(MainScreen.GRID, MainScreen.GRID));
        myTiles = new JPanel[MainScreen.GRID][MainScreen.GRID];
        for(int i = 0; i < MainScreen.GRID; i++){
            for (int j = 0; j < MainScreen.GRID; j++){
                myTiles[i][j] = new JPanel();
                myTiles[i][j].setBackground(BLUE);
                tileNumbers[i][j] = new Coordinate(i,j);
                myTiles[i][j].addMouseListener(new TileListener(
                                               tileNumbers[i][j],
                                               this));
                myGrid.add(myTiles[i][j]);
            }
        }
        mySide.add(myGrid, BorderLayout.CENTER);
        add(mySide, BorderLayout.CENTER);
        
        //create list of ships (rectangles?)
        JPanel instructions = new JPanel();
        instructions.setLayout(new GridLayout(3,2));
        instructions.setBackground(PURPLE);
        instructionLabel = new JLabel("Currently Selecting:");
        instructionLabel.setFont(LABEL_FONT);
        instructionLabel.setForeground(BLUE);
        instructionLabel2 = new JLabel("aircraft carrier");
        instructionLabel2.setFont(LABEL_FONT);
        instructionLabel2.setForeground(BLUE);
        instructionLabel3 = new JLabel("size: 5 panels");
        instructionLabel3.setFont(LABEL_FONT);
        instructionLabel3.setForeground(BLUE);
        
        JPanel labels = new JPanel();
        labels.setBackground(PURPLE);
        labels.setLayout(new GridLayout(2, 1));
        JLabel buttonLabel = new JLabel("Choose a direction");
        JLabel buttonLabel2 = new JLabel("then select the first tile");
        JLabel buttonLabel3 = new JLabel("of your ship");
        buttonLabel3.setForeground(BLUE);
        buttonLabel3.setFont(MainScreen.TEXT_FONT);
        buttonLabel.setForeground(BLUE);
        buttonLabel.setFont(MainScreen.LABEL_FONT);
        buttonLabel2.setForeground(BLUE);
        buttonLabel2.setFont(MainScreen.TEXT_FONT);
        labels.add(instructionLabel2);
        labels.add(instructionLabel3);
        JButton horizontal = new JButton("horizontal");
        horizontal.addActionListener(this);
        JButton vertical = new JButton("vertical");
        vertical.addActionListener(this);
        JPanel buttons = new JPanel();
        buttons.setBackground(PURPLE);
        buttons.add(horizontal);
        buttons.add(vertical);
        
        instructions.add(instructionLabel);
        instructions.add(labels);
        instructions.add(buttonLabel);
        instructions.add(buttonLabel2);
        instructions.add(buttons);
        instructions.add(buttonLabel3);
        add(instructions,BorderLayout.WEST);
        
        //"done" button
        JPanel submit = new JPanel();
        submit.setBackground(PURPLE);
        start = new JButton("start!");
        start.setVisible(false);
        start.addActionListener(this);
        submit.add(start);
        add(submit, BorderLayout.SOUTH);
    }
    public void setInstructions(String s, String s1, String s2){
        instructionLabel.setText(s);
        instructionLabel2.setText(s1);
        instructionLabel3.setText(s2);
    }
    public void done(){
        start.setVisible(true);
    }
    public void actionPerformed(ActionEvent e){
        String message = e.getActionCommand();
        switch (message){
            case "horizontal": horizontal = true;
                               break;
            case "vertical": System.out.println("vertical selected");
                             horizontal = false;
                             break;
            case "start!":  setInstructions("Waiting for opponent...","","");
                            MainScreen main = new MainScreen(myTiles);
                            main.setVisible(true);
                            BattleshipTurns turns = new BattleshipTurns(main);
                            OpponentTileListener.setTurns(turns);
                            main.setTurns(turns);
                            Thread t = new Thread(turns);
                            t.start();
                            dispose();
        }
    }
    public boolean select(Coordinate c){
        if (horizontal){
            if(TileListener.currentTiles + c.y <= MainScreen.GRID){
                for(int i = c.y; i<TileListener.currentTiles + c.y; i++){
                    if(tileNumbers[c.x][i].selected){
                        return false;
                    }
                }
                for(int i = c.y; i<TileListener.currentTiles + c.y; i++){
                    myTiles[c.x][i].setBackground(MainScreen.YELLOW);
                    TileListener.ships[TileListener.shipsSelected]=
                                                            tileNumbers[c.x][i];
                    TileListener.shipsSelected ++;
                    tileNumbers[c.x][i].selected = true;
                }
                return true;
            }
        } else {
            if(TileListener.currentTiles + c.x <= MainScreen.GRID){
                for(int i = c.x; i<TileListener.currentTiles + c.x; i++){
                    if(tileNumbers[i][c.y].selected){
                        return false;
                    }
                }
                for(int i = c.x; i<TileListener.currentTiles + c.x; i++){
                    myTiles[i][c.y].setBackground(MainScreen.YELLOW);
                    TileListener.ships[TileListener.shipsSelected]=
                                                        tileNumbers[i][c.y];
                    TileListener.shipsSelected ++;
                    tileNumbers[i][c.y].selected = true;
                }
                return true;
            }
        }
        return false;
    }
}
