package DoAn;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JDialog;

public class HomePage extends JFrame {

	private JPanel contentPane;

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
		btnNewButton.setBounds(61, 243, 115, 45);
		
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
		contentPane.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Tìm trận");
		btnNewButton_1.setBounds(289, 243, 115, 45);
		btnNewButton_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		contentPane.add(btnNewButton_1);
		
		JButton btnNewButton_2 = new JButton("Đăng xuất");
		btnNewButton_2.setBounds(321, 18, 115, 59);
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Login LoginFrame = new Login();
                LoginFrame.setVisible(true);
                dispose();
			}
		});
		contentPane.add(btnNewButton_2);
	}
}
