package PClient;

import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.awt.EventQueue;
import java.io.*;
import org.jsoup.Connection;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.SwingUtilities;
import javax.crypto.SecretKey;
import org.jsoup.Jsoup; // Import for Jsoup
import org.jsoup.nodes.Document; // Import for Jsoup
import org.json.JSONObject; // Import for JSONObject
import PServer.Login;

public class Client {
	private static Socket socket;
	private static String host;
	private static Cipher aesEncryptCipher;
	public static void main(String[] args) {
        new Thread(() -> runClient()).start();
    }

	private static void runClient() {
		
        try {
        	host = "192.168.233.240";
            socket = new Socket(host, 2911);

            // Receive the AES key from the server
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            SecretKey aesKey = (SecretKey) objectInputStream.readObject();

            // Mã hóa và giải mã dữ liệu với key AES
            try {
                aesEncryptCipher = Cipher.getInstance("AES");
                aesEncryptCipher.init(Cipher.ENCRYPT_MODE, aesKey);

                // Gửi dữ liệu cho server
                String dataToSend = "Hello from client!";
                byte[] encryptedData = aesEncryptCipher.doFinal(dataToSend.getBytes());
                socket.getOutputStream().write(encryptedData);
                
                SwingUtilities.invokeLater(() -> {
                    Login loginFrame = new Login(socket); // Tạo thể hiện của JFrame Login
                    loginFrame.setVisible(true); // Hiển thị JFrame
                });
                byte[] encryptedBye = aesEncryptCipher.doFinal("bye".getBytes());
                socket.getOutputStream().write(encryptedBye);
            // Xử lý phản hồi từ server và giải mã nếu cần thiết

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        } } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}