package DoAn;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import DoAn.SignUp.PasswordHasher;
import database.JDBCUtil;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import javax.swing.JDialog;

public class HomePage extends JFrame {

	private JPanel contentPane;
	private int secondsPassed = 0;
	private Timer timer;
    private JLabel lblNewLabel_5;
    private int userId;
    private String playerName;
    private JLabel lblNewLabel_1;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HomePage frame = new HomePage();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public void setUserId(int userId, String playerName) {
	    this.userId = userId;
	    this.playerName = playerName;
	    setPlayerName(playerName);
	}
	/**
	 * Create the frame.
	 */
	public HomePage() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 483, 378);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Học viện IQ");
		lblNewLabel.setBounds(10, 11, 129, 33);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 16));
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Tên người chơi :");
		lblNewLabel_1.setBounds(35, 68, 104, 23);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Xếp hạng :");
		lblNewLabel_2.setBounds(35, 102, 104, 23);
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 12));
		contentPane.add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("Điểm IQ :");
		lblNewLabel_3.setBounds(35, 136, 104, 23);
		lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 12));
		contentPane.add(lblNewLabel_3);
		
		JButton btnNewButton = new JButton("Bài test IQ");
		btnNewButton.setBounds(60, 236, 115, 45);
		
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
		contentPane.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Tìm trận");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
                startTimer();
            }
		});
		btnNewButton_1.setBounds(265, 236, 115, 45);
		btnNewButton_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		contentPane.add(btnNewButton_1);
		
		JButton btnNewButton_2 = new JButton("Đăng xuất");
		btnNewButton_2.setBounds(321, 18, 115, 59);
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateOnlineStatus(userId, 0);
			    Login loginFrame = new Login();
			    loginFrame.setVisible(true);
			    dispose();
				
			}
		});
		contentPane.add(btnNewButton_2);
		
		JLabel lblNewLabel_4 = new JLabel("Nhấn");
		lblNewLabel_4.setBounds(265, 211, 46, 14);
		contentPane.add(lblNewLabel_4);
	}
	private void startTimer() {
		if (lblNewLabel_5 == null) {
	        lblNewLabel_5 = new JLabel("Thời gian bắt đầu 0 giây");
	        lblNewLabel_5.setBounds(265, 292, 171, 14);
	        contentPane.add(lblNewLabel_5);
	    }
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                secondsPassed++;
                lblNewLabel_5.setText("Thời gian bắt đầu " + secondsPassed + " giây");
            }
        });
        if (!timer.isRunning()) {
            timer.start();
        }
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

	public void setPlayerName(String playerName) {
        lblNewLabel_1.setText("Tên người chơi: " + playerName);
    }
}
