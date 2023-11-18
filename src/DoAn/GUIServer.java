package DoAn;

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
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GUIServer extends JFrame {

	private JPanel contentPane;
	/**
	 * Launch the application.
	 */
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
                dispose();
			}
		});
		btnNewButton.setBounds(21, 52, 187, 41);
		contentPane.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Tổng số người chơi");
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

		
	}


	public static void main(String[] args) {
	    // Khởi động máy chủ trong một luồng riêng biệt
		
	    Thread serverThread = new Thread(new Runnable() {
	        public void run() {
	        	Connection connection = JDBCUtil.getConnection();
	        	ServerSocket serverSocket = null;
	            try {
	            	serverSocket = new ServerSocket(12346);
	                System.out.println("Server is running...");

	                while (true) {
	                	Socket clientSocket = serverSocket.accept();
	                    System.out.println("Client connected from: " + clientSocket.getInetAddress().getHostAddress());

	                    // Send a welcome message to the client
	                    OutputStream outputStream = clientSocket.getOutputStream();
	                    String welcomeMessage = "Connection successful! Welcome to the server.";
	                    outputStream.write(welcomeMessage.getBytes());

	                    clientSocket.close();
	                    
	                }
	            } catch (IOException e) {
	                e.printStackTrace();
	            } finally {
	                try {
	                    if (serverSocket != null && !serverSocket.isClosed()) {
	                        serverSocket.close();
	                        JDBCUtil.closeConnection(connection);
	                    }
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	    });

	    serverThread.start(); // Khởi động máy chủ trong luồng riêng biệt

	    // Tạo và hiển thị giao diện
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
	}}


	