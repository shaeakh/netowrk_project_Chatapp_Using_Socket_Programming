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
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = new JLabel("Username:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(usernameLabel, constraints);

        usernameField = new JTextField(15);
        constraints.gridx = 1;
        constraints.gridy = 0;
        panel.add(usernameField, constraints);

        JLabel passwordLabel = new JLabel("Password:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(passwordLabel, constraints);

        passwordField = new JPasswordField(15);
        constraints.gridx = 1;
        constraints.gridy = 1;
        panel.add(passwordField, constraints);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, constraints);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openRegistrationUI();
            }
        });
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(registerButton, constraints);

        add(panel);
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
