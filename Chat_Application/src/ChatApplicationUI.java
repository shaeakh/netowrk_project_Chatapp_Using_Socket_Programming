// ChatApplicationUI.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatApplicationUI extends JFrame {
    private String currentUser;
    private JList<String> userList;
    private JLabel chatPartnerLabel;
    private JTextArea chatHistory;
    private JTextField messageField;
    private JButton sendButton;
    private String selectedUser;
    private PrintWriter out;
    private BufferedReader in;

    public ChatApplicationUI(String currentUser) {
        this.currentUser = currentUser;

        setTitle("Chat Application - " + currentUser);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();
        fetchUserList();

        setLocationRelativeTo(null);

        connectToServer();
        new MessageReceiver().execute();
    }

    private void initComponents() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        userList = new JList<>();
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedUser = userList.getSelectedValue();
                chatPartnerLabel.setText("Chatting with: " + selectedUser);
                fetchChatHistory(currentUser, selectedUser);
            }
        });
        leftPanel.add(new JScrollPane(userList), BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        chatPartnerLabel = new JLabel("Chatting with: ");
        rightPanel.add(chatPartnerLabel, BorderLayout.NORTH);

        chatHistory = new JTextArea();
        chatHistory.setEditable(false);
        rightPanel.add(new JScrollPane(chatHistory), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        bottomPanel.add(messageField, BorderLayout.CENTER);
        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        bottomPanel.add(sendButton, BorderLayout.EAST);
        rightPanel.add(bottomPanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(200);
        splitPane.setDividerSize(10);

        add(splitPane);
    }

    private void fetchUserList() {
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            try {
                String query = "SELECT UserName FROM user WHERE UserName != ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, currentUser);
                ResultSet resultSet = preparedStatement.executeQuery();

                List<String> users = new ArrayList<>();
                while (resultSet.next()) {
                    users.add(resultSet.getString("UserName"));
                }

                userList.setListData(users.toArray(new String[0]));

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

    private void fetchChatHistory(String user1, String user2) {
        Connection connection = DatabaseConnection.getConnection();

        if (connection != null) {
            try {
                String query = "SELECT * FROM messages " +
                        "WHERE (Sender = ? AND Receiver = ?) OR (Sender = ? AND Receiver = ?) " +
                        "ORDER BY Timestamp ASC";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, user1);
                preparedStatement.setString(2, user2);
                preparedStatement.setString(3, user2);
                preparedStatement.setString(4, user1);
                ResultSet resultSet = preparedStatement.executeQuery();


                StringBuilder history = new StringBuilder();
                while (resultSet.next()) {
                    String sender = resultSet.getString("Sender");
                    String message = resultSet.getString("Message");
                    LocalDateTime timestamp = resultSet.getTimestamp("Timestamp").toLocalDateTime();

                    history.append("[").append(timestamp).append("] ")
                            .append(sender).append(": ")
                            .append(message).append("\n");
                }

                chatHistory.setText(history.toString());

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

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (message.isEmpty() || selectedUser == null) {
            return;
        }

        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            try {
                String query = "INSERT INTO messages (Sender, Receiver, Message, Timestamp) VALUES (?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, currentUser);
                preparedStatement.setString(2, selectedUser);
                preparedStatement.setString(3, message);
                preparedStatement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

                preparedStatement.executeUpdate();

                // Send message to the server
                out.println(currentUser + ":" + selectedUser + ":" + message);

                // Clear message input field
                messageField.setText("");

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

    private void connectToServer() {
        try {
            Socket socket = new Socket("localhost", 12345);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class MessageReceiver extends SwingWorker<Void, String> {
        @Override
        protected Void doInBackground() throws Exception {
            String message;
            while ((message = in.readLine()) != null) {
                publish(message);
            }
            return null;
        }

        @Override
        protected void process(List<String> messages) {
            for (String message : messages) {
                String[] parts = message.split(":", 3);
                String sender = parts[0];
                String receiver = parts[1];
                String msg = parts[2];

                if ((currentUser.equals(sender) && selectedUser.equals(receiver)) ||
                        (currentUser.equals(receiver) && selectedUser.equals(sender))) {
                    chatHistory.append(sender + ": " + msg + "\n");
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ChatApplicationUI("currentUser").setVisible(true);
        });
    }
}
