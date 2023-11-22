package DoAn;

import java.net.*;
import java.io.*;
import javax.swing.SwingUtilities;

public class Client {
    public static void main(String[] args) {
    	Socket socket = null;

        try {
        	socket = new Socket("localhost", 12346);

            // Receive the welcome message from the server
            InputStream inputStream = socket.getInputStream();
            byte[] buffer = new byte[1024];
            int bytesRead = inputStream.read(buffer);
            String welcomeMessage = new String(buffer, 0, bytesRead);
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