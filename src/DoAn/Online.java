package DoAn;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class Online extends JDialog {
    private final JPanel contentPanel = new JPanel();
    private List<String> onlineUsers;

    public Online(List<String> onlineUsers) {
        this.onlineUsers = onlineUsers;
        initComponents();
    }

    private void initComponents() {
        setBounds(100, 100, 450, 300);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);

        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.BOLD, 14));
        textArea.setText("Những người chơi đang online:");
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(10, 11, 414, 200);
        contentPanel.add(scrollPane);

        for (String username : onlineUsers) {
            textArea.append("\n"+ username);
        }

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("Cancel");
        buttonPane.add(cancelButton);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Xử lý sự kiện Cancel (nếu cần)
                dispose();
            }
        });
    }
}
