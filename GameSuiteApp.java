import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class GameSuiteApp
{
 // Game turn tracker and remaining tries for both players
    int turn = 1, tries1 = 10, tries2 = 10;

    // Secret number for Cows and Bulls, player names
    String secret, player1Name, player2Name;

    // GUI components
    JFrame frame;
    JEditorPane log;
    StringBuilder logContent = new StringBuilder();
    JTextField input;
    JLabel status;
    JPanel topPanel;
    public static void main(String[] args)
    {
        // Start the GUI application
        SwingUtilities.invokeLater(GameSuiteApp::new);
    }
    public GameSuiteApp()
    {
        // Prompt users for their names at the beginning
        getNames();
    }
    void getNames()
    {
 // Fields for name input
        JTextField player1Field = new JTextField(10);
        JTextField player2Field = new JTextField(10);
// Layout for the input dialog
        JPanel namePanel = new JPanel(new GridLayout(2, 2));
        namePanel.add(new JLabel("Enter Player 1's Name:"));
        namePanel.add(player1Field);
        namePanel.add(new JLabel("Enter Player 2's Name:"));
        namePanel.add(player2Field);
        int option = JOptionPane.showConfirmDialog(null, namePanel, "Enter Names",                       JOptionPane.OK_CANCEL_OPTION);
if (option == JOptionPane.OK_OPTION)
        {
            player1Name = player1Field.getText();
            player2Name = player2Field.getText();
if (player1Name.isEmpty() || player2Name.isEmpty())
            {
                JOptionPane.showMessageDialog(null, "Please enter both player names!");
                return;
            }

            // Start with Tic Tac Toe
            new TicTacToe();
        }
        else
        {
            System.exit(0);
        }
    }

    void startCowsBulls()
    {
        // Generate a new secret number
        secret = genSecret();

        frame = new JFrame("Cows and Bulls");
        input = new JPasswordField(10);
        input.setFont(new Font("Arial", Font.BOLD, 20));

        log = new JEditorPane("text/plain", "");
        log.setEditable(false);
        logContent.setLength(0);

        // Status label displaying turn and tries
        status = new JLabel(player1Name + "'s Turn | " + player1Name + ": " + tries1 + ", " + player2Name + ": " + tries2);
        status.setFont(new Font("Arial", Font.PLAIN, 16));

        // Buttons
        JButton submit = new JButton("Guess");
        JButton reveal = new JButton("Reveal");
        JButton reset = new JButton("Reset");

        submit.addActionListener(e -> guess());
        reveal.addActionListener(e -> revealAnswer());
        reset.addActionListener(e -> resetGame());

        // Top input panel
        topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.add(new JLabel("Guess:"));
        topPanel.add(input);
        topPanel.add(submit);
        topPanel.add(reveal);
        topPanel.add(reset);

        // Main layout
        frame.setLayout(new BorderLayout());
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(log), BorderLayout.CENTER);
        frame.add(status, BorderLayout.SOUTH);

        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Handle dynamic resizing
        frame.addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {
                int newSize = Math.max(14, Math.min(frame.getWidth() / 30, 36));

                Font resizedFont = new Font("Arial", Font.BOLD, newSize);
                input.setFont(resizedFont);
                log.setFont(resizedFont);
                status.setFont(new Font("Arial", Font.PLAIN, newSize - 2));

                for (Component c : topPanel.getComponents())
                {
                    if (c instanceof JButton)
                    {
                        c.setFont(new Font("Arial", Font.BOLD, newSize - 2));
                    }
                    else if (c instanceof JLabel)
                    {
                        c.setFont(new Font("Arial", Font.PLAIN, newSize - 2));
                    }
                }
            }
        });

        frame.setVisible(true);
    }

    void guess()
    {
        String g = new String(((JPasswordField) input).getPassword()).trim();

        // Validate guess format
        if (g.length() != 4 || !g.matches("\\d+")) return;

        int bulls = 0, cows = 0;
        boolean[] bullFlags = new boolean[4];
        boolean[] secretUsed = new boolean[4];

        // Count bulls (correct digit and position)
        for (int i = 0; i < 4; i++)
        {
            if (g.charAt(i) == secret.charAt(i))
            {
                bulls++;
                bullFlags[i] = true;
                secretUsed[i] = true;
            }
        }

        // Count cows (correct digit, wrong position)
        for (int i = 0; i < 4; i++)
        {
            if (!bullFlags[i])
            {
                for (int j = 0; j < 4; j++)
                {
                    if (!secretUsed[j] && g.charAt(i) == secret.charAt(j))
                    {
                        cows++;
                        secretUsed[j] = true;
                        break;
                    }
                }
            }
        }

        if (turn == 1) tries1--;
        else tries2--;

        logContent.append((turn == 1 ? player1Name : player2Name) + "'s Guess: ** → " + bulls + " Bulls, " + cows + " Cows\n");
        log.setText(logContent.toString());

        // Check for win or game over
        if (bulls == 4)
        {
            JOptionPane.showMessageDialog(frame, "Congratulations, " + (turn == 1 ? player1Name : player2Name) + " wins!");
            endGame();
        }
        else if (tries1 == 0 && tries2 == 0)
        {
            JOptionPane.showMessageDialog(frame, "No attempts left! Game Over.");
            endGame();
        }
        else
        {
            // Switch turn and update status
            turn = 3 - turn;
            status.setText((turn == 1 ? player1Name : player2Name) + "'s Turn | " + player1Name + ": " + tries1 + ", " + player2Name + ": " + tries2);
            input.setText("");
        }
    }

    void revealAnswer()
    {
        // Show the correct secret number
        JOptionPane.showMessageDialog(frame, "Answer: " + secret);
        endGame();
    }

    void endGame()
    {
        // Reset tries and dispose of game window
        tries1 = tries2 = 10;
        frame.dispose();

        // Show options after game ends
        int choice = JOptionPane.showOptionDialog(null, "Game Over! What would you like to do?", "Game Over",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                new String[]{"Restart Cows and Bulls", "Go to Tic Tac Toe", "Exit"}, "Restart Cows and Bulls");

        if (choice == JOptionPane.YES_OPTION)
        {
            restartCowsBulls();
        }
        else if (choice == JOptionPane.NO_OPTION)
        {
            new TicTacToe();
        }
        else
        {
            System.exit(0);
        }
    }

    void resetGame()
    {
        // Reset game state and restart the Cows and Bulls game
        frame.dispose();
        tries1 = tries2 = 10;
        turn = 1;
        startCowsBulls();
    }

    void restartCowsBulls()
    {
        // Generate new secret and reset everything
        tries1 = tries2 = 10;
        turn = 1;
        secret = genSecret();
        if (frame != null) frame.dispose();
        logContent.setLength(0);
        startCowsBulls();
    }

    String genSecret()
    {
        // Generate a unique 4-digit number
        Set<Integer> digits = new LinkedHashSet<>();
        Random r = new Random();
        while (digits.size() < 4)
        {
            digits.add(r.nextInt(10));
        }
        return digits.stream().map(String::valueOf).reduce("", String::concat);
    }

    class TicTacToe
    {
        JButton[] cells = new JButton[9];
        int current = 1; // 1 for Player1 (X), 2 for Player2 (O)

        TicTacToe()
        {
            JFrame f = new JFrame("Tic Tac Toe");
            f.getContentPane().setBackground(Color.BLACK);
            f.setLayout(new GridLayout(3, 3));

            // Create grid buttons
            for (int i = 0; i < 9; i++)
            {
                cells[i] = new JButton();
                cells[i].setFont(new Font("Arial", Font.BOLD, 40));
                cells[i].setBackground(Color.BLACK);
                cells[i].setOpaque(true);
                cells[i].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));

                int idx = i;
                cells[i].addActionListener(e -> move(idx, f));
                f.add(cells[i]);
            }

            f.setSize(300, 300);
            f.setVisible(true);
        }

        void move(int i, JFrame f)
        {
            // If already clicked, do nothing
            if (!cells[i].getText().isEmpty()) return;

            // Set X or O and update color
            if (current == 1)
            {
                cells[i].setText("X");
                cells[i].setForeground(new Color(255, 215, 0));
            }
            else
            {
                cells[i].setText("O");
                cells[i].setForeground(new Color(192, 192, 192));
            }

            // Check win or draw
            if (check())
            {
                int choice = JOptionPane.showOptionDialog(f, (current == 1 ? player1Name : player2Name) + " wins!", "Victory!",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                        new String[]{"Restart Tic Tac Toe", "Go to Cows and Bulls"}, "Go to Cows and Bulls");

                tries1 = tries2 = 10;
                f.dispose();

                if (choice == JOptionPane.YES_OPTION) new TicTacToe();
                else startCowsBulls();
                return;
            }

            if (Arrays.stream(cells).allMatch(b -> !b.getText().isEmpty()))
            {
                int choice = JOptionPane.showOptionDialog(f, "It's a draw!", "Draw!",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                        new String[]{"Restart Tic Tac Toe", "Go to Cows and Bulls"}, "Go to Cows and Bulls");

                f.dispose();
                if (choice == JOptionPane.YES_OPTION) new TicTacToe();
                else startCowsBulls();
                return;
            }

            current = 3 - current;
        }

        boolean check()
        {
            // All winning combinations
            int[][] w = {
                    {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
                    {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
                    {0, 4, 8}, {2, 4, 6}
            };

            for (int[] a : w)
            {
                String s1 = cells[a[0]].getText();
                String s2 = cells[a[1]].getText();
                String s3 = cells[a[2]].getText();

                if (!s1.isEmpty() && s1.equals(s2) && s2.equals(s3)) return true;
            }
            return false;
        }
    }
}
