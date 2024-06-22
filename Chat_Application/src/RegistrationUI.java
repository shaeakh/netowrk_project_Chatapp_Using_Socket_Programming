// RegistrationUI.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class RegistrationUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public RegistrationUI() {
        setTitle("Registration");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();

        setLocationRelativeTo(null);
    }

    private void initComponents() {
        // Create the main panel with a GridBagLayout
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = new JLabel("Username:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(usernameLabel, constraints);

        usernameField = new JTextField(20); // Increased size
        constraints.gridx = 1;
        constraints.gridy = 0;
        panel.add(usernameField, constraints);

        JLabel passwordLabel = new JLabel("Password:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(passwordLabel, constraints);

        passwordField = new JPasswordField(20); // Increased size
        constraints.gridx = 1;
        constraints.gridy = 1;
        panel.add(passwordField, constraints);

        JButton registerButton = new JButton("Register");
        registerButton.setBackground(Color.decode("#5CE1E6"));
        registerButton.setForeground(Color.decode("#00253B"));
        registerButton.setOpaque(true);
        registerButton.setBorderPainted(false);
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(registerButton, constraints);

        // Center everything
        add(panel, BorderLayout.CENTER);
    }

    private void registerUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in both fields");
            return;
        }

        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            try {
                String query = "INSERT INTO user (UserName, Password) VALUES (?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.executeUpdate();

                JOptionPane.showMessageDialog(this, "Registration successful");
                dispose();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Registration failed");
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RegistrationUI().setVisible(true);
        });
    }
}
