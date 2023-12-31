package PServer;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

//import PServer.GUIServer.MatchmakingServer;
import database.JDBCUtil;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import javax.swing.JDialog;
public class HomePage extends JFrame {
	private static Socket socket;
	private JPanel contentPane;
	private int secondsPassed = 0;
	private Timer timer;
    private JLabel lblNewLabel_5;
    private JLabel lblNewLabel_1; 
    private JLabel lblNewLabel_2; 
    private JLabel lblNewLabel_3; 
    //private MatchmakingServer matchmakingServer;
    private String playerName; // Biến instance để lưu trữ playerName

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HomePage frame = new HomePage("", socket);
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
	public HomePage(String playerName, Socket socket) {
		this.playerName = playerName;
		this.socket = socket;
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
		
		lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setText("Tên người chơi: " + playerName);
        lblNewLabel_1.setBounds(35, 68, 200, 23);
        lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 12));
        contentPane.add(lblNewLabel_1);
		
        lblNewLabel_2 = new JLabel("");
        lblNewLabel_2.setBounds(35, 102, 104, 23);
        lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 12));
        contentPane.add(lblNewLabel_2);
		displayRanking(playerName);
		
		lblNewLabel_3 = new JLabel("");
		lblNewLabel_3.setBounds(35, 136, 104, 23);
		lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 12));
		contentPane.add(lblNewLabel_3);
		displayIQPoints(playerName);
		
		JButton btnNewButton = new JButton("Bài test IQ");
		btnNewButton.setBounds(60, 236, 115, 45);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int iqPoints = getIqPoints(playerName);
				if (iqPoints == 0) {
		            // Mở giao diện ImageViewerFrame
					SwingUtilities.invokeLater(() -> {
						ImageViewerFrame ima = new ImageViewerFrame(playerName,socket);
			            ima.setVisible(true);
	                });
		            
		        } else {
		            // Hiển thị thông báo
		            JOptionPane.showMessageDialog(HomePage.this, "Bạn đã kiểm tra IQ trước đó. Không thể thử lại.");
		        }
            }
		});
		
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
				ResultSet resultSet = null;
				int userId = -1;
				Connection connection = JDBCUtil.getConnection();
		        PreparedStatement preparedStatement = null;
		        try {
		            connection = JDBCUtil.getConnection();
		            String sql = "SELECT id FROM nameid WHERE username=?";
		            preparedStatement = connection.prepareStatement(sql);
		            preparedStatement.setString(1, playerName);

		            resultSet = preparedStatement.executeQuery();

		            // Nếu có kết quả, di chuyển con trỏ đến dòng đầu tiên
		            if (resultSet.next()) {
		                // Lấy giá trị trường "id" từ ResultSet
		                userId = resultSet.getInt("id");
		                updateOnlineStatus(userId, 0);
					    Login loginFrame = new Login(socket);
					    loginFrame.setVisible(true);
					    dispose();
		            }
		        } catch (SQLException e3) {
		            e3.printStackTrace();
		        } finally {
		            // Đảm bảo đóng tất cả các resource (ResultSet, PreparedStatement, Connection)
		            JDBCUtil.closeResultSet(resultSet);
		            JDBCUtil.closeStatement(preparedStatement);
		            JDBCUtil.closeConnection(connection);
		        }
				
				
			}
		});
		contentPane.add(btnNewButton_2);
		
		JLabel lblNewLabel_4 = new JLabel("Nhấn");
		lblNewLabel_4.setBounds(265, 211, 46, 14);
		contentPane.add(lblNewLabel_4);
		
	}
	private void startTimer() {
		if (lblNewLabel_5 == null) {
			
	        lblNewLabel_5 = new JLabel("Thời gian bắt đầu 60 giây");
	        lblNewLabel_5.setFont(new Font("Tahoma", Font.BOLD, 12));
	        lblNewLabel_5.setBounds(265, 292, 171, 14);
	        contentPane.add(lblNewLabel_5);
	    }
		final int totalTime = 60;
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                secondsPassed++;
                int remainingTime = totalTime - secondsPassed;
                lblNewLabel_5.setText("Thời gian bắt đầu " + remainingTime + " giây");
                //sendMatchNotification(playerName, opponent);
                //matchmakingServer.startMatchmaking(playerName);
                if (remainingTime <= 0) {
                    timer.stop();
                    resetTimer();
                } 
            }
        });
        if (!timer.isRunning()) {
            timer.start();
        }
    }
	private void resetTimer() {
	    if (timer != null && timer.isRunning()) {
	        timer.stop();
	    }
	    secondsPassed = 0;
	    lblNewLabel_5.setText("Thời gian bắt đầu 60 giây");
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
	private void displayRanking(String playerName) {
	    Connection connection = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;

	    try {
	        connection = JDBCUtil.getConnection();
	        String sql = "SELECT username, point FROM nameid ORDER BY point DESC";
	        preparedStatement = connection.prepareStatement(sql);
	        resultSet = preparedStatement.executeQuery();

	        int rank = 1;

	        while (resultSet.next()) {
	            String username = resultSet.getString("username");
	            int points = resultSet.getInt("point");

	            if (username.equals(playerName)) {
	            	lblNewLabel_2.setText("Xếp hạng: " + rank);
	        		
	                
	                break;
	            }

	            rank++;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        JDBCUtil.closeResultSet(resultSet);
	        JDBCUtil.closeStatement(preparedStatement);
	        JDBCUtil.closeConnection(connection);
	    }
	}
	private int getIqPoints(String playerName) {
	    Connection connection = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    int iqPoints = 0;

	    try {
	        connection = JDBCUtil.getConnection();
	        String sql = "SELECT pointiq FROM nameid WHERE username=?";
	        preparedStatement = connection.prepareStatement(sql);
	        preparedStatement.setString(1, playerName);

	        resultSet = preparedStatement.executeQuery();

	        if (resultSet.next()) {
	            iqPoints = resultSet.getInt("pointiq");
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        JDBCUtil.closeResultSet(resultSet);
	        JDBCUtil.closeStatement(preparedStatement);
	        JDBCUtil.closeConnection(connection);
	    }

	    return iqPoints;
	}
	private void displayIQPoints(String playerName) {
	    int iqPoints = getIqPoints(playerName);
	    lblNewLabel_3.setText("Điểm IQ: " + iqPoints);
	}

}
