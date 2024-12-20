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

        // Create status panel with status bar, switch mode button, and dark mode toggle
        JPanel statusPanel = new JPanel(new BorderLayout());
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Setup the status bar (JLabel) to display status message
        statusBar = new JLabel();
        statusBar.setFont(FONT_STATUS);
        statusBar.setBackground(COLOR_BG_STATUS_LIGHT);
        statusBar.setOpaque(true);
        statusBar.setPreferredSize(new Dimension(200, 30));
        statusBar.setHorizontalAlignment(JLabel.LEFT);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));

        // Add buttons to the buttons panel
        buttonsPanel.add(darkModeToggle);
        buttonsPanel.add(switchModeButton);
        buttonsPanel.setBackground(COLOR_BG_STATUS_LIGHT);

        // Add components to the status panel
        statusPanel.add(statusBar, BorderLayout.CENTER);
        statusPanel.add(buttonsPanel, BorderLayout.EAST);
        statusPanel.setBackground(COLOR_BG_STATUS_LIGHT);

        super.setLayout(new BorderLayout());
        super.add(statusPanel, BorderLayout.PAGE_END);
        super.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 30));
        super.setBorder(BorderFactory.createLineBorder(COLOR_BG_STATUS_LIGHT, 2, false));

        // This JPanel fires MouseEvent
        super.addMouseListener(new MouseAdapter() {
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
                repaint();  // Callback paintComponent().
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

        // Update the container of status bar and buttons
        Container parent = (Container) statusBar.getParent();
        if (parent != null) {
            parent.setBackground(statusBgColor);
            // Update the buttons panel background
            Component[] components = parent.getComponents();
            for (Component comp : components) {
                if (comp instanceof JPanel) {
                    comp.setBackground(statusBgColor);
                }
            }
        }

        // Update board dark mode state
        board.setDarkMode(isDarkMode);
    }

    /** Custom painting codes on this JPanel */
    @Override
    public void paintComponent(Graphics g) {  // Callback via repaint()
        super.paintComponent(g);
        setBackground(isDarkMode ? COLOR_BG_DARK : COLOR_BG_LIGHT); // set its background color

        board.paint(g);  // ask the game board to paint itself

        // Print status-bar message
        if (currentState == State.PLAYING) {
            statusBar.setForeground(isDarkMode ? Color.WHITE : Color.BLACK);
            statusBar.setText((currentPlayer == Seed.CROSS) ? "X's Turn" : "O's Turn");
            if (currentPlayer == Seed.CROSS) {
                SoundEffect.EAT_FOOD.play();
            }
            if (currentPlayer == Seed.NOUGHT) {
                SoundEffect.EAT_FOOD.play();
            }
        } else if (currentState == State.DRAW) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("It's a Draw! Click to play again.");
            SoundEffect.DIE.play();
        } else if (currentState == State.CROSS_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'X' Won! Click to play again.");
            SoundEffect.DIE.play();
        } else if (currentState == State.NOUGHT_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'O' Won! Click to play again.");
            SoundEffect.DIE.play();
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