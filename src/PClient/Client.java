package PClient;

import java.net.*;
import java.io.*;
import javax.swing.SwingUtilities;
import PServer.Login;

public class Client {
    public static void main(String[] args) {
    	Socket socket = null;

        try {
        	socket = new Socket("192.168.254.166", 2911);

            // Receive the welcome message from the server
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            byte[] buffer = new byte[1024];
            int bytesRead = inputStream.read(buffer);
            String welcomeMessage = bytesRead != -1 ? new String(buffer, 0, bytesRead) : "Hello";
            System.out.println("Server says: " + welcomeMessage);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    Login loginFrame = new Login(); // Tạo thể hiện của JFrame Login
                    loginFrame.setVisible(true); // Hiển thị JFrame
                }
                

            });
        } catch (IOException e) {
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
