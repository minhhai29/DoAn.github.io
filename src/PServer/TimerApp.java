package PServer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TimerApp {

    private static int secondsPassed = 0;
    private static Timer timer;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Đếm giây");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel timerLabel = new JLabel("Thời gian: 0 giây");
        frame.getContentPane().add(timerLabel);

        JButton startButton = new JButton("Bắt đầu lại");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startTimer();
            }
        });
        frame.getContentPane().add(startButton);

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                secondsPassed++;
                timerLabel.setText("Thời gian: " + secondsPassed + " giây");
            }
        });

        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.pack();
        frame.setSize(300, 200);
        frame.setVisible(true);
    }

    private static void startTimer() {
        if (!timer.isRunning()) {
            timer.start();
        }
    }
}
