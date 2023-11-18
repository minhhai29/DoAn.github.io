package DoAn;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;

public class Login extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JPasswordField passwordField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
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
	public Login() {
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
				HomePage homeFrame = new HomePage();
                homeFrame.setVisible(true);
                dispose();
			}
		});
		btnNewButton.setBounds(103, 169, 109, 23);
		contentPane.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Đăng ký");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SignUp signupFrame = new SignUp(); // Tạo thể hiện của JFrame Signup
                signupFrame.setVisible(true); // Hiển thị JFrame Signup
                dispose(); // Đóng cửa sổ của JFrame Login sau khi chuyển qua JFrame Signup
			}
		});
		btnNewButton_1.setBounds(240, 169, 100, 23);
		contentPane.add(btnNewButton_1);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(156, 114, 184, 20);
		contentPane.add(passwordField);
	}
}
