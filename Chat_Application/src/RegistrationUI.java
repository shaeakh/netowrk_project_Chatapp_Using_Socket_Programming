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
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();

        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel()); // Placeholder for grid alignment
        panel.add(registerButton);

        add(panel);
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
