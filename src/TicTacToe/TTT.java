/**
 * ES234317-Algorithm and Data Structures
 * Semester Ganjil, 2024/2025
 * Group Capstone Project
 * Group #12
 * 1 - 5026231082 - Naufal Zaky Nugraha
 * 2 - 5026231035 - Aldani Prasetyo
 * 3 - 5026231183 - Astrid Meilendra
 */

package TicTacToe;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.sound.sampled.*;
import java.io.File;

/**
 * Tic-Tac-Toe: Two-player Graphic version with better OO design.
 * The Board and Cell classes are separated in their own classes.
 */
public class TTT extends JPanel {
    private static final long serialVersionUID = 1L; // to prevent serializable warning

    // Define named constants for the drawing graphics
    public static final String TITLE = "Tic Tac Toe";
    // Light mode colors
    public static final Color COLOR_BG_LIGHT = Color.WHITE;
    public static final Color COLOR_BG_STATUS_LIGHT = new Color(216, 216, 216);
    // Dark mode colors
    public static final Color COLOR_BG_DARK = new Color(43, 43, 43);
    public static final Color COLOR_BG_STATUS_DARK = new Color(66, 66, 66);
    public static final Color COLOR_CROSS = new Color(239, 105, 80);  // Red #EF6950
    public static final Color COLOR_NOUGHT = new Color(64, 154, 225); // Blue #409AE1
    public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);

    // Define game objects
    private Board board;         // the game board
    private State currentState;  // the current state of the game
    private Seed currentPlayer;  // the current player
    private JLabel statusBar;    // for displaying status message
    private AIPlayer aiPlayer;   // AI player
    private boolean againstAI;   // flag for playing against AI
    private JFrame frame;        // store frame reference
    private JButton switchModeButton; // button for switching modes
    private boolean isDarkMode = false; // flag for dark mode
    private JToggleButton darkModeToggle; // toggle button for dark mode

    /** Constructor to setup the UI and game components */
    public TTT(boolean playAgainstAI) {
        // Set the game mode
        this.againstAI = playAgainstAI;

        // Initialize sound effects
        SoundEffect.initGame();
        // Start background music
        SoundEffect.BACKGROUND.playLoop();

        // Create dark mode toggle button
        darkModeToggle = new JToggleButton("Dark Mode");
        darkModeToggle.setFont(FONT_STATUS);
        darkModeToggle.addActionListener(e -> {
            isDarkMode = darkModeToggle.isSelected();
            updateTheme();
            repaint();
        });

        // Create switch mode button
        switchModeButton = new JButton(againstAI ? "Switch to PVP Mode" : "Switch to VS AI Mode");
        switchModeButton.setFont(FONT_STATUS);
        switchModeButton.addActionListener(e -> switchGameMode());

        // Setup the status bar (JLabel) to display status message
        statusBar = new JLabel();
        statusBar.setFont(FONT_STATUS);
        statusBar.setBackground(COLOR_BG_STATUS_LIGHT);
        statusBar.setOpaque(true);
        statusBar.setPreferredSize(new Dimension(200, 30));
        statusBar.setHorizontalAlignment(JLabel.LEFT);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));

        // Create top panel for buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.add(darkModeToggle);
        topPanel.add(switchModeButton);
        topPanel.setBackground(COLOR_BG_STATUS_LIGHT);

        // Create bottom panel for status
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(statusBar, BorderLayout.CENTER);
        bottomPanel.setBackground(COLOR_BG_STATUS_LIGHT);

        // Create game board panel
        JPanel gameBoardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(isDarkMode ? COLOR_BG_DARK : COLOR_BG_LIGHT);
                board.paint(g);
            }
        };
        gameBoardPanel.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT));

        super.setLayout(new BorderLayout());
        super.add(topPanel, BorderLayout.PAGE_START);
        super.add(gameBoardPanel, BorderLayout.CENTER);
        super.add(bottomPanel, BorderLayout.PAGE_END);
        super.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 80)); // Added extra height for top and bottom panels

        // This JPanel fires MouseEvent
        gameBoardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {  // mouse-clicked handler
                int mouseX = e.getX();
                int mouseY = e.getY();
                // Get the row and column clicked
                int row = mouseY / Cell.SIZE;
                int col = mouseX / Cell.SIZE;

                if (currentState == State.PLAYING) {
                    if (row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS
                            && board.cells[row][col].content == Seed.NO_SEED) {
                        // Update cells[][] and return the new game state after the move
                        currentState = board.stepGame(currentPlayer, row, col);

                        // Switch player
                        currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;

                        // If it's AI's turn and game is still playing
                        if (againstAI && currentPlayer == Seed.NOUGHT && currentState == State.PLAYING) {
                            // Get AI's move
                            int[] aiMove = aiPlayer.move();
                            // Make the move
                            currentState = board.stepGame(currentPlayer, aiMove[0], aiMove[1]);
                            // Switch back to human player
                            currentPlayer = Seed.CROSS;
                        }
                    }
                } else {        // game over
                    newGame();  // restart the game
                }
                // Refresh the drawing canvas
                gameBoardPanel.repaint();  // Callback paintComponent().
                updateStatusBar();
            }
        });

        // Setup game objects
        // Create a new instance of Board
        board = new Board();  // Create a new instance of Board

        // Initialize AI player if playing against AI
        if (againstAI) {
            aiPlayer = new AIPlayerMinimax(board);
            aiPlayer.setSeed(Seed.NOUGHT);  // AI plays as O
        }

        // Initialize the game
        initGame();
        newGame();

        // Initial theme update
        updateTheme();
    }

    /** Initialize the game (run once) */
    public void initGame() {
        board = new Board();  // allocate array
        // Initialize the game board
        for (int row = 0; row < Board.ROWS; ++row) {
            for (int col = 0; col < Board.COLS; ++col) {
                board.cells[row][col] = new Cell(row, col);
            }
        }

        // Initialize AI if playing against AI
        if (againstAI) {
            aiPlayer = new AIPlayerMinimax(board);
            aiPlayer.setSeed(Seed.NOUGHT);  // AI plays as O
        }
    }

    /** Reset the game-board contents */
    public void newGame() {
        for (int row = 0; row < Board.ROWS; ++row) {
            for (int col = 0; col < Board.COLS; ++col) {
                board.cells[row][col].content = Seed.NO_SEED;  // all cells empty
            }
        }
        currentState = State.PLAYING;  // ready to play
        currentPlayer = Seed.CROSS;    // cross plays first
    }

    /** Update the theme based on dark mode state */
    private void updateTheme() {
        Color bgColor = isDarkMode ? COLOR_BG_DARK : COLOR_BG_LIGHT;
        Color statusBgColor = isDarkMode ? COLOR_BG_STATUS_DARK : COLOR_BG_STATUS_LIGHT;
        Color textColor = isDarkMode ? Color.WHITE : Color.BLACK;

        setBackground(bgColor);
        statusBar.setBackground(statusBgColor);
        statusBar.setForeground(textColor);
        darkModeToggle.setBackground(statusBgColor);
        darkModeToggle.setForeground(textColor);
        switchModeButton.setBackground(statusBgColor);
        switchModeButton.setForeground(textColor);

        // Update all panels
        for (Component comp : getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                if (panel == getComponent(1)) { // Game board panel
                    panel.setBackground(bgColor);
                } else {
                    panel.setBackground(statusBgColor);
                    // Update child components
                    for (Component child : panel.getComponents()) {
                        if (child instanceof JComponent) {
                            JComponent jc = (JComponent) child;
                            jc.setBackground(statusBgColor);
                            jc.setForeground(textColor);
                        }
                    }
                }
            }
        }

        // Update board dark mode state
        board.setDarkMode(isDarkMode);
        repaint();
    }

    /** Custom painting codes on this JPanel */
    @Override
    public void paintComponent(Graphics g) {  // Callback via repaint()
        super.paintComponent(g);
        setBackground(isDarkMode ? COLOR_BG_DARK : COLOR_BG_LIGHT);
    }

    /** Update the status bar at the bottom of the frame */
    private void updateStatusBar() {
        if (currentState == State.PLAYING) {
            statusBar.setForeground(isDarkMode ? Color.WHITE : Color.BLACK);
            statusBar.setText((currentPlayer == Seed.CROSS) ? "X's Turn" : "O's Turn");
        } else if (currentState == State.DRAW) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("It's a Draw! Click to play again.");
        } else if (currentState == State.CROSS_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'X' Won! Click to play again.");
            SoundEffect.PLAYER_WIN.play();
        } else if (currentState == State.NOUGHT_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'O' Won! Click to play again.");
            if (againstAI) {
                SoundEffect.AI_WIN.play();
            } else {
                SoundEffect.PLAYER_WIN.play();
            }
        }
    }

    /** Switch between PVP and AI mode */
    private void switchGameMode() {
        againstAI = !againstAI;
        // Update button text
        switchModeButton.setText(againstAI ? "Switch to PVP Mode" : "Switch to VS AI Mode");

        if (againstAI) {
            aiPlayer = new AIPlayerMinimax(board);
            aiPlayer.setSeed(Seed.NOUGHT);
            JOptionPane.showMessageDialog(this, "Switched to Player vs AI mode");
        } else {
            aiPlayer = null;
            JOptionPane.showMessageDialog(this, "Switched to Player vs Player mode");
        }
        // If it's AI's turn when switching to AI mode, make AI move
        if (againstAI && currentPlayer == Seed.NOUGHT && currentState == State.PLAYING) {
            int[] aiMove = aiPlayer.move();
            currentState = board.stepGame(currentPlayer, aiMove[0], aiMove[1]);
            currentPlayer = Seed.CROSS;
            repaint();
        }
    }

    /** The entry "main" method */
    public static void main(String[] args) {
        // Run GUI construction codes in Event-Dispatching thread for thread safety
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(TITLE);
                // Set the content-pane of the JFrame to an instance of main JPanel
                frame.setContentPane(new TTT(true));
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null); // center the application window
                frame.setVisible(true);            // show it
            }
        });
    }

    public void play() {
        frame = new JFrame(TITLE);
        // Set the content-pane of the JFrame to this instance
        frame.setContentPane(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null); // center the application window
        frame.setVisible(true);            // show it
    }
}