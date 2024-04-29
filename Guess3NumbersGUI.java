import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

public class Guess3NumbersGUI extends JFrame {
    private Random random = new Random();
    private int[] numbersToGuess = new int[3];
    private ArrayList<Integer> guessedNumbers = new ArrayList<>();
    private int attempts = 1;
    private boolean gameOver = false;
    private final int maxAttempts = 30;
    private final int tolerance = 3;

    private JLabel messageLabel;
    private JLabel numbersLabel1;
    private JLabel numbersLabel2;
    private JLabel numbersLabel3;
    private JLabel attemptsLabel;
    private JTextField guessField;
    private JButton submitButton;
    private JButton restartButton;
    private JButton startButton;
    private JLabel welcomeLabel;
    private JButton outroRestartButton;

    // MySQL kapcsolat változói
    private static final String DB_URL = "jdbc:mysql://localhost:3306/guess3numbers";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private Connection conn;

    public Guess3NumbersGUI() {
        setTitle("Guess 3 Numbers");
        setSize(400, 300);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.BLUE);

        // MySQL kapcsolat létrehozása
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JPanel welcomePanel = new JPanel();
        welcomePanel.setBackground(Color.BLUE);
        welcomeLabel = new JLabel("Welcome to guess three numbers! Let's play a game");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        welcomeLabel.setForeground(Color.YELLOW);
        welcomePanel.add(welcomeLabel);

        startButton = new JButton("Play!");
        startButton.setPreferredSize(new Dimension(100, 30));

        setLayout(new BorderLayout());
        add(welcomePanel, BorderLayout.CENTER);
        add(startButton, BorderLayout.SOUTH);

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
    }

    private void startGame() {
        getContentPane().removeAll();
        getContentPane().invalidate();
        getContentPane().revalidate();
        getContentPane().repaint();

        getContentPane().setBackground(Color.BLACK);

        setTitle("Guess 3 Numbers");
        setSize(400, 300);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        generateNumbersToGuess();

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());
        inputPanel.setBackground(Color.BLACK);

        messageLabel = new JLabel("Enter your guess:");
        messageLabel.setForeground(Color.YELLOW);
        guessField = new JTextField(10);
        guessField.setBackground(Color.YELLOW);
        guessField.setForeground(Color.BLACK);

        submitButton = new JButton("Submit");
        restartButton = new JButton("Restart");

        numbersLabel1 = new JLabel(" ");
        numbersLabel2 = new JLabel(" ");
        numbersLabel3 = new JLabel(" ");
        numbersLabel1.setForeground(Color.YELLOW);
        numbersLabel2.setForeground(Color.YELLOW);
        numbersLabel3.setForeground(Color.YELLOW);

        attemptsLabel = new JLabel("Attempt " + attempts + "/" + maxAttempts);
        attemptsLabel.setForeground(Color.YELLOW);

        inputPanel.add(messageLabel);
        inputPanel.add(guessField);
        inputPanel.add(submitButton);
        inputPanel.add(restartButton);
        inputPanel.add(numbersLabel1);
        inputPanel.add(numbersLabel2);
        inputPanel.add(numbersLabel3);
        inputPanel.add(attemptsLabel);

        add(inputPanel, BorderLayout.CENTER);

        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!gameOver) {
                    checkGuess();
                }
            }
        });

        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });

        restartButton.setVisible(false);
    }

    private void showOutro() {
        getContentPane().removeAll();
        getContentPane().invalidate();
        getContentPane().revalidate();
        getContentPane().repaint();

        getContentPane().setBackground(Color.BLUE);

        JPanel outroPanel = new JPanel();
        outroPanel.setLayout(new BorderLayout());
        outroPanel.setBackground(Color.BLUE);

        JLabel outroLabel = new JLabel("Congratulations! You guessed all three numbers.");
        outroLabel.setFont(new Font("Arial", Font.BOLD, 16));
        outroLabel.setForeground(Color.YELLOW);
        outroLabel.setHorizontalAlignment(SwingConstants.CENTER);
        outroPanel.add(outroLabel, BorderLayout.NORTH);

        // Create a table to display the scores
        JTable scoresTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(scoresTable);
        scrollPane.setPreferredSize(new Dimension(300, 150));

        try {
            // Fetch data from the scores table
            String query = "SELECT * FROM scores";
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery(query);

            // Populate the table model
            DefaultTableModel model = new DefaultTableModel();
            scoresTable.setModel(model);

            // Add columns to the table
            java.sql.ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(metaData.getColumnLabel(i));
            }

            // Add rows to the table
            while (rs.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = rs.getObject(i);
                }
                model.addRow(rowData);
            }

            // Close statement and result set
            stmt.close();
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        outroPanel.add(scrollPane, BorderLayout.CENTER);

        outroRestartButton = new JButton("Restart");
        outroRestartButton.setPreferredSize(new Dimension(100, 30));
        outroRestartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });

        outroPanel.add(outroRestartButton, BorderLayout.SOUTH);

        add(outroPanel, BorderLayout.CENTER);
    }

    private void generateNumbersToGuess() {
        for (int i = 0; i < 3; i++) {
            numbersToGuess[i] = random.nextInt(100) + 1;
        }
    }

    private void checkGuess() {
        int userGuess;
        try {
            userGuess = Integer.parseInt(guessField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        guessedNumbers.add(userGuess);

        StringBuilder data = new StringBuilder();

        for (int i = 0; i < 3; i++) {
            int numberToGuess = numbersToGuess[i];
            int difference = Math.abs(userGuess - numberToGuess);

            if (guessedNumbers.contains(numberToGuess)) {
                data.append("Number ").append(i + 1).append(": ").append(numberToGuess).append(" ✓\n");
            } else if (difference <= tolerance) {
                data.append("Number ").append(i + 1).append(": You are close!\n");
            } else if (userGuess < numberToGuess) {
                data.append("Number ").append(i + 1).append(": Too low.\n");
            } else {
                data.append("Number ").append(i + 1).append(": Too high.\n");
            }
        }

        JOptionPane.showMessageDialog(this, data.toString(), "Guess Feedback", JOptionPane.INFORMATION_MESSAGE);

        if (guessedNumbers.contains(numbersToGuess[0]) && guessedNumbers.contains(numbersToGuess[1]) && guessedNumbers.contains(numbersToGuess[2])) {
            showOutro();
            gameOver = true;
            submitButton.setEnabled(false);
            restartButton.setVisible(false);
            guessField.setVisible(false);
        }

        attempts++;
        attemptsLabel.setText("Attempt " + attempts + "/" + maxAttempts);

        if (attempts >= maxAttempts) {
            gameOver = true;
            messageLabel.setText("Game Over. You reached the maximum number of attempts.");
            submitButton.setEnabled(false);
            restartButton.setVisible(true);
            guessField.setVisible(false);
        }

        guessField.setText("");
        guessField.requestFocus();
    }

    private void restartGame() {
        getContentPane().removeAll();
        getContentPane().invalidate();
        getContentPane().revalidate();
        getContentPane().repaint();

        getContentPane().setBackground(Color.BLUE);

        JPanel welcomePanel = new JPanel();
        welcomePanel.setBackground(Color.BLUE);
        welcomeLabel = new JLabel("Welcome to guess three numbers! Let's play a game");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        welcomeLabel.setForeground(Color.YELLOW);
        welcomePanel.add(welcomeLabel);

        startButton = new JButton("PLAY!");
        startButton.setPreferredSize(new Dimension(100, 30));

        setLayout(new BorderLayout());
        add(welcomePanel, BorderLayout.CENTER);
        add(startButton, BorderLayout.SOUTH);

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });

        generateNumbersToGuess();

        guessedNumbers.clear();

        attempts = 1;
        gameOver = false;
    }

    private void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Guess3NumbersGUI frame = new Guess3NumbersGUI();
                frame.setVisible(true);
            }
        });
    }
}
