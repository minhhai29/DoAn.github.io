package PServer;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import PServer.SignUp.PasswordHasher;
import database.JDBCUtil;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;

public class Login extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JPasswordField passwordField;
	private static Socket socket;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login(socket);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Login(Socket socket) {
		this.socket = socket;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Tên đăng nhập");
		lblNewLabel.setBounds(34, 77, 87, 26);
		contentPane.add(lblNewLabel);
		
		textField = new JTextField();
		textField.setBounds(156, 80, 184, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Mật khẩu");
		lblNewLabel_1.setBounds(34, 114, 74, 20);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Học viện IQ");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setBounds(132, 11, 184, 46);
		contentPane.add(lblNewLabel_2);
		
		JButton btnNewButton = new JButton("Đăng nhập");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isInputValid()) {
			        Connection connection = JDBCUtil.getConnection();
			        PreparedStatement preparedStatement = null;
			        ResultSet resultSet = null;

			        try {

			            // Tiếp tục với việc kiểm tra thông tin đăng nhập
			            String sql = "SELECT * FROM nameid WHERE email=? AND password=?";
			            preparedStatement = connection.prepareStatement(sql);
			            
			            String email = textField.getText();
			            String password = passwordField.getText();
			            String hashedPassword = PasswordHasher.hashPassword(password);

			            preparedStatement.setString(1, email);
			            preparedStatement.setString(2, hashedPassword);

			            resultSet = preparedStatement.executeQuery();

			            if (resultSet.next()) {
			            	int userId = resultSet.getInt("id");
			            	String playerName = resultSet.getString("username");
			            	int isol = resultSet.getInt("isonline");
			                updateOnlineStatus(userId, 1); // 1 là trạng thái đang online
			                // Đăng nhập thành công, mở giao diện HomePage
			                SwingUtilities.invokeLater(() -> {
				                HomePage homePage = new HomePage(playerName,socket);
				                homePage.setVisible(true);
				                dispose();
			                });
		 // Đóng frame đăng ký sau khi đăng nhập thành công
			            } else {
			                JOptionPane.showMessageDialog(null, "Tên người dùng hoặc mật khẩu không đúng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
			            }
			        } catch (SQLException | NoSuchAlgorithmException ex) {
			            ex.printStackTrace();
			        } finally {
			            JDBCUtil.closeResultSet(resultSet);
			            JDBCUtil.closeStatement(preparedStatement);
			            JDBCUtil.closeConnection(connection);
			        }
			    }
			}

		});
		btnNewButton.setBounds(103, 169, 109, 23);
		contentPane.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Đăng ký");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(() -> {
					SignUp signupFrame = new SignUp(socket); // Tạo thể hiện của JFrame Signup
	                signupFrame.setVisible(true); // Hiển thị JFrame Signup
	                dispose(); // Đóng cửa sổ của JFrame Login sau khi chuyển qua JFrame Signup
                });
				
			}
		});
		btnNewButton_1.setBounds(240, 169, 100, 23);
		contentPane.add(btnNewButton_1);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(156, 114, 184, 20);
		contentPane.add(passwordField);
	}
	private boolean isInputValid() {
	    String username = textField.getText();
	    String password = new String(passwordField.getPassword());

	    // Kiểm tra xem có bất kỳ trường nào bị trống không
	    if (password.isEmpty() || username.isEmpty()) {
	        JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
	        return false;
	    }

	    return true;
	}
	public static boolean authenticateUser(String username, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            // Chuẩn bị câu truy vấn
            String sql = "SELECT * FROM nameid WHERE username = ? AND password = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            // Thực hiện truy vấn
            resultSet = preparedStatement.executeQuery();

            // Nếu có bản ghi khớp, trả về true
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Đóng tất cả các resource
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        // Trả về false nếu có lỗi hoặc không có bản ghi khớp
        return false;
    }
	private static void updateOnlineStatus(int userId, int status) {
	    Connection connection = null;
	    PreparedStatement preparedStatement = null;

	    try {
	        String sql = "UPDATE nameid SET isonline = ? WHERE id = ?";
	        connection = JDBCUtil.getConnection();
	        preparedStatement = connection.prepareStatement(sql);
	        preparedStatement.setInt(1, status);
	        preparedStatement.setInt(2, userId);
	        preparedStatement.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        // Đóng tất cả các resource
	        JDBCUtil.closeStatement(preparedStatement);
	        JDBCUtil.closeConnection(connection);
	    }
	}
}
