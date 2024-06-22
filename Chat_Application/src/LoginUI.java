// LoginUI.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginUI() {
        setTitle("Login");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();

        setLocationRelativeTo(null);
    }

    private void initComponents() {
        // Create the main panel with a BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create a label with an image icon for the left side
        JLabel imageLabel = new JLabel(new ImageIcon("assets/images/login_bg.png"));
        imageLabel.setHorizontalAlignment(JLabel.CENTER);

        // Create the form panel for the right side
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = new JLabel("Username:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        formPanel.add(usernameLabel, constraints);

        usernameField = new JTextField(20); // Increased size
        constraints.gridx = 1;
        constraints.gridy = 0;
        formPanel.add(usernameField, constraints);

        JLabel passwordLabel = new JLabel("Password:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        formPanel.add(passwordLabel, constraints);

        passwordField = new JPasswordField(20); // Increased size
        constraints.gridx = 1;
        constraints.gridy = 1;
        formPanel.add(passwordField, constraints);

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(Color.decode("#5CE1E6"));
        loginButton.setForeground(Color.decode("#00253B"));
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        formPanel.add(loginButton, constraints);

        JButton registerButton = new JButton("Register");
        registerButton.setBackground(Color.decode("#5CE1E6"));
        registerButton.setForeground(Color.decode("#00253B"));
        registerButton.setOpaque(true);
        registerButton.setBorderPainted(false);
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openRegistrationUI();
            }
        });
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        formPanel.add(registerButton, constraints);

        // Add the image label and form panel to a JSplitPane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, imageLabel, formPanel);
        splitPane.setDividerLocation(400); // Set the initial position of the divider
        splitPane.setResizeWeight(0.5); // Adjust the resize weight

        // Add the split pane to the main panel
        mainPanel.add(splitPane, BorderLayout.CENTER);

        // Add the main panel to the frame
        add(mainPanel);
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            try {
                String query = "SELECT * FROM user WHERE UserName = ? AND Password = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    dispose();
                    new ChatApplicationUI(username).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid username or password");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void openRegistrationUI() {
        new RegistrationUI().setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginUI().setVisible(true);
        });
    }
}
