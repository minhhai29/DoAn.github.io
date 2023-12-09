package PServer;
import javax.swing.*;

import java.util.Random;

import database.JDBCUtil;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.EventQueue;
import java.net.Socket;
import javax.swing.border.EmptyBorder;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class SignUp extends JFrame {
		private static Socket socket;
	    private Worker worker;
	 	private JPanel contentPane;
	    private JTextField textField;
	    private JTextField textField_2;
	    private final JRadioButton rdbtnNewRadioButton = new JRadioButton("Nam");
	    private final JRadioButton rdbtnNewRadioButton_1 = new JRadioButton("Nữ");
	    private final JRadioButton rdbtnNewRadioButton_2 = new JRadioButton("Other");
	    private JPasswordField passwordField;
	    	private ButtonGroup genderButtonGroup = new ButtonGroup();
	    /**
	     * Launch the application.
	     */
	    public static void main(String[] args) {
	        EventQueue.invokeLater(new Runnable() {
	            public void run() {
	                try {
	                    SignUp frame = new SignUp(socket);
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
	    public SignUp(Socket socket) {
	    	this.socket = socket;
	        this.worker = new Worker();
	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        setBounds(100, 100, 450, 300);
	        contentPane = new JPanel();
	        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	        setContentPane(contentPane);
	        contentPane.setLayout(null);
	        
		JLabel lblNewLabel = new JLabel("Đăng ký");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNewLabel.setBounds(166, 23, 103, 33);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Gmail đăng nhập");
		lblNewLabel_1.setBounds(45, 75, 121, 14);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Mật khẩu");
		lblNewLabel_2.setBounds(45, 107, 121, 14);
		contentPane.add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("Tên người dùng");
		lblNewLabel_3.setBounds(45, 140, 121, 14);
		contentPane.add(lblNewLabel_3);
		
		JLabel lblNewLabel_4 = new JLabel("Giới tính");
		lblNewLabel_4.setBounds(45, 174, 46, 14);
		contentPane.add(lblNewLabel_4);
		
		textField = new JTextField();
		textField.setBounds(176, 72, 182, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		textField_2 = new JTextField();
		textField_2.setBounds(176, 137, 182, 20);
		contentPane.add(textField_2);
		textField_2.setColumns(10);
		
		JButton btnNewButton = new JButton("Đăng ký");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(isInputValid()) {
					generatedOTP = generateOTP();
					if (worker.isEmailExists(textField.getText())) {
					    JOptionPane.showMessageDialog(null, "Email đã tồn tại. Vui lòng sử dụng email khác.", "Lỗi", JOptionPane.ERROR_MESSAGE);
					    return; // Không tiếp tục nếu email đã tồn tại
					}
					if (worker.isUsernameExists(textField_2.getText())) {
					    JOptionPane.showMessageDialog(null, "Tên người dùng đã tồn tại. Vui lòng chọn tên khác.", "Lỗi", JOptionPane.ERROR_MESSAGE);
					    return; // Không tiếp tục nếu tên người dùng đã tồn tại
					}
					    
					// Chuẩn bị câu truy vấn SQL cho bảng câu hỏi
//			        String sql = "INSERT INTO nameid (username,password,email,isonline,sex,point,winstreak,totalmatch,pointiq) VALUES (?,?,?,0,?,0,0,0,0)";
					// Tạo một PreparedStatement cho bảng câu hỏi
//			        preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

					// Lấy giá trị từ form và thiết lập cho các tham số trong câu truy vấn câu hỏi
					String username = textField_2.getText();
					String password = passwordField.getText();
					String hashedPassword = null;
					// Hash mật khẩu
					try {
					    hashedPassword = PasswordHasher.hashPassword(password);
					} catch (NoSuchAlgorithmException ex) {
					    ex.printStackTrace();
					}

					String email = textField.getText();
					String gender = "";
					if (rdbtnNewRadioButton.isSelected()) {
						gender = "M";
					} else if (rdbtnNewRadioButton_1.isSelected()) {
						gender = "F";
					} else if (rdbtnNewRadioButton_2.isSelected()) {
						gender = "O";
					}
					
					// Gọi phương thức đăng ký của Worker
					int result = worker.signUpUser(username, hashedPassword, email, gender);

					
					EmailSender.sendEmail(email, generatedOTP);
					String enteredOTP = JOptionPane.showInputDialog("Nhập mã OTP đã gửi đến email của bạn:");
					if (enteredOTP != null && enteredOTP.equals(generatedOTP) && result != -1) {
					    // Nếu đúng, tiến hành đăng ký
					    JOptionPane.showMessageDialog(null, "Đăng ký thành công!");
					    int rowsAffected = worker.signUpUser(username, hashedPassword, email, gender);
					    if (rowsAffected > 0) {
					    	SwingUtilities.invokeLater(() -> {
					    		Login loginFrame = new Login(socket);
					            loginFrame.setVisible(true);
					            dispose();
					        });
//					            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
//					            if (generatedKeys.next()) {
//					                int nameId = generatedKeys.getInt(1);				               
//					            } 
					    }
					} else {
					    // Nếu sai, hiển thị thông báo lỗi
					    JOptionPane.showMessageDialog(null, "Xác nhận không thành công. Vui lòng thử lại.");
					}
				}
			}
		});
        btnNewButton.setBounds(285, 212, 89, 23);
        contentPane.add(btnNewButton);
		btnNewButton.setBounds(285, 212, 89, 23);
		contentPane.add(btnNewButton);
		
        rdbtnNewRadioButton.setBounds(117, 170, 54, 23);
        contentPane.add(rdbtnNewRadioButton);
        genderButtonGroup.add(rdbtnNewRadioButton);

        rdbtnNewRadioButton_1.setBounds(181, 168, 54, 27);
        contentPane.add(rdbtnNewRadioButton_1);
        genderButtonGroup.add(rdbtnNewRadioButton_1);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(176, 104, 182, 20);
		contentPane.add(passwordField);
		

        rdbtnNewRadioButton_2.setBounds(238, 170, 109, 23);
        contentPane.add(rdbtnNewRadioButton_2);
        genderButtonGroup.add(rdbtnNewRadioButton_2);
		
		JButton btnNewButton_1 = new JButton("Trở lại");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Login loginFrame = new Login(socket); // Tạo thể hiện của JFrame Signup
                loginFrame.setVisible(true); // Hiển thị JFrame Signup
                dispose();
			}
		});
		btnNewButton_1.setBounds(180, 212, 89, 23);
		contentPane.add(btnNewButton_1);
	}
	    
	    private boolean isInputValid() {
		    String email = textField.getText();
		    String password = new String(passwordField.getPassword());
		    String username = textField_2.getText();

		    // Kiểm tra xem có bất kỳ trường nào bị trống không
		    if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
		        JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
		        return false;
		    }
		    if (!email.matches(".+@gmail\\.com")) {
		        JOptionPane.showMessageDialog(null, "Email phải có định dạng @gmail.com.", "Lỗi", JOptionPane.ERROR_MESSAGE);
		        return false;
		    }

		    // Kiểm tra độ dài mật khẩu (tối thiểu 6 kí tự)
		    if (password.length() < 6) {
		        JOptionPane.showMessageDialog(null, "Mật khẩu phải có ít nhất 6 kí tự.", "Lỗi", JOptionPane.ERROR_MESSAGE);
		        return false;
		    }

		    // Kiểm tra xem mật khẩu có ít nhất một chữ cái và một số không
		    if (!password.matches(".*[a-zA-Z].*") || !password.matches(".*\\d.*")) {
		        JOptionPane.showMessageDialog(null, "Mật khẩu phải chứa ít nhất một chữ cái và một số.", "Lỗi", JOptionPane.ERROR_MESSAGE);
		        return false;
		    }

		    // Kiểm tra định dạng tên người dùng (viết hoa chữ cái đầu)
		    return true;
		}
	    public class PasswordHasher {

	    	public static String hashPassword(String password) throws NoSuchAlgorithmException {
	            MessageDigest digest = MessageDigest.getInstance("SHA-256");
	            byte[] encodedHash = digest.digest(password.getBytes());

	            StringBuilder hexString = new StringBuilder();
	            for (byte b : encodedHash) {
	                String hex = Integer.toHexString(0xff & b);
	                if (hex.length() == 1) hexString.append('0');
	                hexString.append(hex);
	            }
	            return hexString.toString();
	        }

	    }
	    private String generateOTP() {
	        int otpLength = 6;
	        StringBuilder otp = new StringBuilder();

	        // Sử dụng Random để sinh ngẫu nhiên từ 0-9
	        Random random = new Random();
	        for (int i = 0; i < otpLength; i++) {
	            otp.append(random.nextInt(10));
	        }

	        return otp.toString();
	    }
	    // Thêm biến để lưu trữ OTP
	    private String generatedOTP;
}
