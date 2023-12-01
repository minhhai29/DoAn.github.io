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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.io.ObjectOutputStream;
public class GUIServer extends JFrame {
	private static final int TIMEOUT_MINUTES = 1;
	private JPanel contentPane;
	private List<String> onlineUsers = new ArrayList<>();
	private static final List<ClientHandler> clients = new ArrayList<>();
	Map<String, Integer> highestPointPlayer = getPlayerWithHighestPoint();
	Map<String, Integer> highestWinStreakPlayer = getPlayerWithHighestWinStreak();
	Map<String, Integer> highestTotalMatchPlayer = getPlayerWithHighestTotalMatch();
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
		
		for (Map.Entry<String, Integer> entry : highestTotalMatchPlayer.entrySet()) {
		    String username = entry.getKey();
		    int totalMatch = entry.getValue();

		    JLabel lblNewLabel_1 = new JLabel("Tham gia nhiều nhất: " + username + " số trận đã chơi: " + totalMatch );
			lblNewLabel_1.setBounds(25, 188, 300, 14);
			contentPane.add(lblNewLabel_1);
		}
		
		
		for (Map.Entry<String, Integer> entry : highestPointPlayer.entrySet()) {
			String username = entry.getKey();
		    int point = entry.getValue();
			JLabel lblNewLabel_2 = new JLabel("Người điểm cao nhất:   " + username + " với " + point + " điểm");
			lblNewLabel_2.setBounds(25, 220, 300, 14);
			contentPane.add(lblNewLabel_2);
		}
		
		for (Map.Entry<String, Integer> entry : highestWinStreakPlayer.entrySet()) {
			String username = entry.getKey();
		    int winstreak = entry.getValue();
			JLabel lblNewLabel_3 = new JLabel("Chuỗi thắng dài nhất:   " + username + " với chuỗi "+ winstreak);
			lblNewLabel_3.setBounds(25, 256, 300, 14);
			contentPane.add(lblNewLabel_3);
		}
		
		
		// Khởi động máy chủ trong một luồng riêng biệt
		Thread serverThread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(2911)) {
                ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
                executorService.scheduleAtFixedRate(() -> checkAndClose(serverSocket), 0, 1, TimeUnit.MINUTES);

                System.out.println("Server is waiting for connections...");

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected from: " + clientSocket.getInetAddress().getHostAddress());

                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    clients.add(clientHandler);

                    new Thread(clientHandler).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        serverThread.start();
    }

    private static void checkAndClose(ServerSocket serverSocket) {
        try {
            if (serverSocket.isClosed() || serverSocket.accept() == null) {
                System.out.println("No connections in the last " + TIMEOUT_MINUTES + " minutes. Closing server.");
                closeServerGracefully(serverSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void closeServerGracefully(ServerSocket serverSocket) {
        try {
            for (ClientHandler client : clients) {
                client.closeConnection();
            }

            serverSocket.close();

            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
	private Map<String, Integer> getPlayerWithHighestTotalMatch() {
	    Map<String, Integer> result = new HashMap<>();
	    Connection connection = JDBCUtil.getConnection();
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;

	    try {
	        String sql = "SELECT username, totalmatch FROM nameid ORDER BY totalmatch DESC LIMIT 1";
	        preparedStatement = connection.prepareStatement(sql);
	        resultSet = preparedStatement.executeQuery();

	        if (resultSet.next()) {
	            String username = resultSet.getString("username");
	            int totalMatch = resultSet.getInt("totalmatch");
	            result.put(username, totalMatch);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        JDBCUtil.closeResultSet(resultSet);
	        JDBCUtil.closeStatement(preparedStatement);
	        JDBCUtil.closeConnection(connection);
	    }

	    return result;
	}
	private Map<String, Integer> getPlayerWithHighestPoint() {
		Map<String, Integer> result = new HashMap<>();
	    Connection connection = JDBCUtil.getConnection();
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;

	    try {
	        String sql = "SELECT username, point FROM nameid ORDER BY point DESC LIMIT 1";
	        preparedStatement = connection.prepareStatement(sql);
	        resultSet = preparedStatement.executeQuery();

	        if (resultSet.next()) {
	            String username = resultSet.getString("username");
	            int totalMatch = resultSet.getInt("point");
	            result.put(username, totalMatch);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        JDBCUtil.closeResultSet(resultSet);
	        JDBCUtil.closeStatement(preparedStatement);
	        JDBCUtil.closeConnection(connection);
	    }

	    return result;
	}

	private Map<String, Integer> getPlayerWithHighestWinStreak() {
		Map<String, Integer> result = new HashMap<>();
	    Connection connection = JDBCUtil.getConnection();
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;

	    try {
	        String sql = "SELECT username, winstreak FROM nameid ORDER BY winstreak DESC LIMIT 1";
	        preparedStatement = connection.prepareStatement(sql);
	        resultSet = preparedStatement.executeQuery();

	        if (resultSet.next()) {
	            String username = resultSet.getString("username");
	            int totalMatch = resultSet.getInt("winstreak");
	            result.put(username, totalMatch);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        JDBCUtil.closeResultSet(resultSet);
	        JDBCUtil.closeStatement(preparedStatement);
	        JDBCUtil.closeConnection(connection);
	    }

	    return result;
	}
	public class MatchmakingManager {
	    private static List<String> playersInQueue = new ArrayList<>();

	    public static synchronized void addToQueue(String playerName) {
	        playersInQueue.add(playerName);
	        tryMatchmaking();
	    }

	    private static void tryMatchmaking() {
	        // Kiểm tra xem có đủ người chơi để ghép cặp hay không
	        if (playersInQueue.size() >= 2) {
	            String player1 = playersInQueue.remove(0);
	            String player2 = playersInQueue.remove(0);
	            
	            // Gửi thông báo đến cả hai người chơi với thông tin đối thủ
	            notifyMatchedPlayers(player1, player2);
	        }
	    }

	    private static void notifyMatchedPlayers(String player1, String player2) {
	        // TODO: Gửi thông báo đến client để chuyển sang giao diện inGame
	        // Ví dụ:
	        sendMatchNotification(player1, player2);
	        sendMatchNotification(player2, player1);
	    }

	    private static void sendMatchNotification(String playerName, String opponentName) {
	    	try {
	            ServerSocket serverSocket = new ServerSocket(2911); // Chọn một cổng phù hợp
	            Socket socket = serverSocket.accept();

	            // Gửi thông điệp ghép cặp tới client
	            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
	            outputStream.writeObject("MATCH_FOUND:" + opponentName);

	            // Đóng các resource
	            outputStream.close();
	            socket.close();
	            serverSocket.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    }
	}




	