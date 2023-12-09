package PServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import database.JDBCUtil;
import encryptions.AESEncryption;
public class Worker implements Runnable {
    private static final int TIMEOUT_MINUTES = 10;
    private static final List<ClientHandler> clients = new ArrayList<>();
	Map<String, Integer> highestPointPlayer = getPlayerWithHighestPoint();
	Map<String, Integer> highestWinStreakPlayer = getPlayerWithHighestWinStreak();
	Map<String, Integer> highestTotalMatchPlayer = getPlayerWithHighestTotalMatch();
	private List<String> onlineUsers = new ArrayList<>();
	private List<String> allUsers = new ArrayList<>();
	private ScheduledExecutorService executorService;
	private static Worker instance;

	public void run() {
		try (ServerSocket serverSocket = new ServerSocket(2911)) {
            executorService = Executors.newScheduledThreadPool(1);
            executorService.scheduleAtFixedRate(() -> checkAndClose(serverSocket), 0, 1, TimeUnit.MINUTES);

            System.out.println("Server is waiting for connections...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected from: " + clientSocket.getInetAddress().getHostAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);

                new Thread(clientHandler).start();
            }
		} catch (SocketTimeoutException e) {
            System.out.println("No connection within " + TIMEOUT_MINUTES + " minutes. Closing the server.");
            closeServerGracefully();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (executorService != null && !executorService.isShutdown()) {
                executorService.shutdown();
            }
        }
    }
	public static Worker getInstance() {
        if (instance == null) {
            instance = new Worker();
        }
        return instance;
    }
	private void closeServerGracefully() {
        try {
            for (ClientHandler client : clients) {
                client.closeConnection();
            }

            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }
    private class ClientHandler implements Runnable {
	    private Socket clientSocket;
	    private InputStream inputStream;
	    private OutputStream outputStream;
	    private Cipher aesEncryptCipher;
        private Cipher aesDecryptCipher;
	    public ClientHandler(Socket clientSocket) {
	        this.clientSocket = clientSocket;
	        try {
	            aesEncryptCipher = Cipher.getInstance("AES");
	            aesDecryptCipher = Cipher.getInstance("AES");
	        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
	            e.printStackTrace();
	        }
	    
	    
	    }
	    
	    @Override
	    public void run() {
            try {
                // Mở luồng vào/ra cho client
                inputStream = clientSocket.getInputStream();
                outputStream = clientSocket.getOutputStream();

                // Gửi key AES cho client
                SecretKey aesKey = null; // Khai báo ngoài khối try-catch để có thể truy cập bên ngoài

                    try {
						aesKey = AESEncryption.generateKey();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(aesKey);

                // Xử lý tương tác với client trong suốt quá trình chơi game
                // Ví dụ: đọc dữ liệu từ client và gửi phản hồi

                // Mã hóa và giải mã dữ liệu với key AES
                aesEncryptCipher.init(Cipher.ENCRYPT_MODE, aesKey);
                aesDecryptCipher.init(Cipher.DECRYPT_MODE, aesKey);

                // Đọc dữ liệu từ client
                byte[] encryptedData = new byte[1024];
                int bytesRead = inputStream.read(encryptedData);
                byte[] decryptedData = aesDecryptCipher.doFinal(encryptedData, 0, bytesRead);
                if (bytesRead == -1) {
                    // Client đã đóng kết nối
                    System.out.println("Client disconnected: " + clientSocket.getInetAddress().getHostAddress());
                    closeConnection();
                    return;
                }
                String clientMessage = new String(decryptedData);
                System.out.println("Received from client: " + clientMessage);


            } catch (IOException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                e.printStackTrace();
            } finally {
                // Khi client thoát, loại bỏ khỏi danh sách và đóng kết nối
                clients.remove(this);
                closeConnection();
            }
        }
	    private void sendEncryptedData(String data) throws IOException, IllegalBlockSizeException, BadPaddingException {
	        byte[] encryptedData = aesEncryptCipher.doFinal(data.getBytes());
	        outputStream.write(encryptedData);
	    }
	    private byte[] decryptDataFromClient(byte[] encryptedData, int bytesRead) throws IllegalBlockSizeException, BadPaddingException {
	        return aesDecryptCipher.doFinal(encryptedData, 0, bytesRead);
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

	public Worker() {
	        // Khởi tạo dữ liệu và đối tượng ở đây
	        highestPointPlayer = getPlayerWithHighestPoint();
	        highestWinStreakPlayer = getPlayerWithHighestWinStreak();
	        highestTotalMatchPlayer = getPlayerWithHighestTotalMatch();
	    }
	

    public List<String> getOnlineUsers() {
        return onlineUsers;
    }
    public List<String> getAllUsers() {
        return allUsers;
    }
    public void updateOnlineUsers() {
        // Thực hiện logic để cập nhật người dùng trực tuyến
        onlineUsers = getOnlineUsersFromDatabase();
    }
    public void updateAllUsers() {
        // Thực hiện logic để cập nhật người dùng trực tuyến
        allUsers = getAllUsersFromDatabase();
    }
    private static void checkAndClose(ServerSocket serverSocket) {
        try {
            serverSocket.setSoTimeout(TIMEOUT_MINUTES * 60 * 1000);
            serverSocket.accept();
        } catch (SocketTimeoutException e) {
            System.out.println("No connection within " + TIMEOUT_MINUTES + " minutes. Closing the server.");
            Worker.getInstance().closeServerGracefully();
        } catch (SocketException e) {
            System.out.println("Socket has closed. The server may be shutting down.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    

    public boolean isEmailExists(String email) {
        Connection connection = null;
        try {
            connection = JDBCUtil.getConnection();
            String query = "SELECT COUNT(*) FROM nameid WHERE email=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, email);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        return count > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Đóng kết nối
            closeConnection(connection);
        }
        return false;
    }

    public boolean isUsernameExists(String username) {
        Connection connection = null;
        try {
            connection = JDBCUtil.getConnection();
            String query = "SELECT COUNT(*) FROM nameid WHERE username=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        return count > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Đóng kết nối
            closeConnection(connection);
        }
        return false;
    }

    // Hàm để đóng kết nối
    private void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public int signUpUser(String username, String hashedPassword, String email, String gender) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        int result = -1;
        try {
            // Tạo một PreparedStatement cho bảng nameid
            connection = JDBCUtil.getConnection();
            // Chuẩn bị câu truy vấn SQL cho bảng nameid
            String sql = "INSERT INTO nameid (username, password, email, isonline, sex, point, winstreak, totalmatch, pointiq) VALUES (?, ?, ?, 0, ?, 0, 0, 0, 0)";
            // Tạo 1 PreparedStatement cho bảng nameid
            preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            // Lấy giá trị từ form và thiết lập cho các tham số trong câu truy vấn
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, gender);

            // Thi hành câu truy vấn chèn và kiểm tra số lượng dòng bị ảnh hưởng
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
            	int generatedKey = getGeneratedKey(preparedStatement);
                return generatedKey;
            } else {
                return -1; // Trường hợp thất bại
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return -1; // Trường hợp lỗi
        } finally {
            JDBCUtil.closeStatement(preparedStatement);
            JDBCUtil.closeConnection(connection);
        }
    }

    private int getGeneratedKey(PreparedStatement preparedStatement) throws SQLException {
    	int generatedKey = -1;
    	// Lấy giá trị khóa chính được tạo tự động (nếu có)
        try (var generatedKeys = preparedStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                generatedKey = generatedKeys.getInt(1);
            }
        }
        return generatedKey;
    }
}