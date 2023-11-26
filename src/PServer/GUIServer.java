package PServer;

import java.awt.EventQueue;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.net.InetAddress;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import database.JDBCUtil;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
public class GUIServer extends JFrame {

	private JPanel contentPane;
	private List<String> onlineUsers = new ArrayList<>();
	private final List<ClientHandler> clients = new ArrayList<>();
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    GUIServer frame = new GUIServer();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
	public GUIServer() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 358, 327);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Xin chào Server");
		lblNewLabel.setBounds(25, 11, 126, 30);
		contentPane.add(lblNewLabel);
		
		JButton btnNewButton = new JButton("Thêm câu hỏi vào CSDL");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addquestion addQtDialog = new addquestion(); // Tạo thể hiện của JFrame Signup
				addQtDialog.setVisible(true); // Hiển thị JFrame Signup
			}
		});
		btnNewButton.setBounds(21, 52, 187, 41);
		contentPane.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Tổng số người chơi");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<String> allUsers = getAllUsersFromDatabase();
		        List<String> onlineUsers = getOnlineUsersFromDatabase();

		        // Tạo một đối tượng Online và truyền danh sách tất cả người dùng và người dùng online
		        Online onlineDialog = new Online(allUsers, onlineUsers);
		        onlineDialog.setVisible(true);
			}
		});
		btnNewButton_1.setBounds(21, 104, 187, 38);
		contentPane.add(btnNewButton_1);
		
		JButton btnNewButton_2 = new JButton("Cài đặt trò chơi");
		btnNewButton_2.setBounds(21, 153, 149, 23);
		contentPane.add(btnNewButton_2);
		
		JLabel lblNewLabel_1 = new JLabel("Người tham gia nhiều nhất");
		lblNewLabel_1.setBounds(25, 188, 135, 14);
		contentPane.add(lblNewLabel_1);
		
		JTextPane textPane = new JTextPane();
		textPane.setBounds(170, 188, 149, 20);
		contentPane.add(textPane);
		
		JLabel lblNewLabel_2 = new JLabel("Người thắng nhiều nhất");
		lblNewLabel_2.setBounds(25, 225, 135, 14);
		contentPane.add(lblNewLabel_2);
		
		JTextPane textPane_1 = new JTextPane();
		textPane_1.setBounds(170, 219, 149, 20);
		contentPane.add(textPane_1);
		
		JLabel lblNewLabel_3 = new JLabel("Chuỗi thắng dài nhất");
		lblNewLabel_3.setBounds(25, 256, 126, 14);
		contentPane.add(lblNewLabel_3);
		
		JTextPane textPane_2 = new JTextPane();
		textPane_2.setBounds(170, 250, 149, 20);
		contentPane.add(textPane_2);
		// Khởi động máy chủ trong một luồng riêng biệt
		Thread serverThread = new Thread(() -> {
		    try (ServerSocket serverSocket = new ServerSocket(2911)) {
		        System.out.println("Server is running...");

		        while (true) {
		            Socket clientSocket = serverSocket.accept();
		            System.out.println("Client connected from: " + clientSocket.getInetAddress().getHostAddress());

		            // Tạo một ClientHandler mới cho mỗi client và thêm vào danh sách
		            ClientHandler clientHandler = new ClientHandler(clientSocket);
		            clients.add(clientHandler);

		            // Bắt đầu một luồng riêng biệt để xử lý client
		            new Thread(clientHandler).start();
		        }
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		});

        serverThread.start(); // Khởi động máy chủ trong luồng riêng biệt
		
	}


	private class ClientHandler implements Runnable {
	    private Socket clientSocket;
	    private InputStream inputStream;
	    private OutputStream outputStream;

	    public ClientHandler(Socket clientSocket) {
	        this.clientSocket = clientSocket;
	    }

	    @Override
	    public void run() {
	        try {
	            // Mở luồng vào/ra cho client
	            inputStream = clientSocket.getInputStream();
	            outputStream = clientSocket.getOutputStream();

	            // Xử lý tương tác với client trong suốt quá trình chơi game
	            // Ví dụ: đọc dữ liệu từ client và gửi phản hồi

	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            // Khi client thoát, loại bỏ khỏi danh sách và đóng kết nối
	            clients.remove(this);
	            closeConnection();
	        }
	    }

	    private void closeConnection() {
	        try {
	            if (inputStream != null) {
	                inputStream.close();
	                
	            }
	            if (outputStream != null) {
	                outputStream.close();
	            }
	            if (clientSocket != null && !clientSocket.isClosed()) {
	                clientSocket.close();
	                System.out.println("Client disconnected: " + clientSocket.getInetAddress().getHostAddress());
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            clients.remove(this);
	        }
	    }
	}
	private List<String> getOnlineUsersFromDatabase() {
	    List<String> onlineUsers = new ArrayList<>();
	    Connection connection = JDBCUtil.getConnection();
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;

	    try {
	        String sql = "SELECT username FROM nameid WHERE isonline = 1";
	        preparedStatement = connection.prepareStatement(sql);
	        resultSet = preparedStatement.executeQuery();

	        while (resultSet.next()) {
	            String username = resultSet.getString("username");
	            onlineUsers.add(username);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        JDBCUtil.closeResultSet(resultSet);
	        JDBCUtil.closeStatement(preparedStatement);
	        JDBCUtil.closeConnection(connection);
	    }

	    return onlineUsers;
	}
	private List<String> getAllUsersFromDatabase() {
	    List<String> allUsers = new ArrayList<>();
	    Connection connection = JDBCUtil.getConnection();
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;

	    try {
	        String sql = "SELECT username FROM nameid";
	        preparedStatement = connection.prepareStatement(sql);
	        resultSet = preparedStatement.executeQuery();

	        while (resultSet.next()) {
	            String username = resultSet.getString("username");
	            allUsers.add(username);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        JDBCUtil.closeResultSet(resultSet);
	        JDBCUtil.closeStatement(preparedStatement);
	        JDBCUtil.closeConnection(connection);
	    }

	    return allUsers;
	}
	
	// Trong Server (phần ghép cặp thành công)
	// Trong Server
	public class MatchmakingServer {
	    private Map<String, String> waitingPlayers = new HashMap<>();

	    public void startMatchmaking(String username) {
	        waitingPlayers.put(username, username);

	        if (waitingPlayers.size() >= 2) {
	            // Ghép cặp hai người chơi
	            Iterator<String> iterator = waitingPlayers.keySet().iterator();
	            String player1 = iterator.next();
	            String player2 = iterator.next();

	            // Gửi thông báo ghép cặp đến cả hai người chơi
	            sendMatchmakingSuccess(player1, player2);

	            // Xóa người chơi khỏi danh sách đang chờ
	            iterator.remove();
	            iterator.remove();
	        }
	    }

	    private void sendMatchmakingSuccess(String player1, String player2) {
	    	openVoGame(player1);
	        openVoGame(player2);
	    }
	}

	
	private void openVoGame(String username) {
        // Mở giao diện VoGame trong luồng giao diện người dùng (Swing UI thread)
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                VoGame voGame = new VoGame();
                voGame.setVisible(true);
            }
        });
    }

}


	