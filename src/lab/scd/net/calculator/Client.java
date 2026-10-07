package lab.scd.net.calculator;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Client extends JFrame {

    private final JTextField txtNum1;
    private final JTextField txtNum2;
    private final JTextField txtResult;

    // 172.20.10.2
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 1900;

    public Client() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        inputPanel.add(new JLabel("First number:"));
        txtNum1 = new JTextField();
        inputPanel.add(txtNum1);

        inputPanel.add(new JLabel("Second number:"));
        txtNum2 = new JTextField();
        inputPanel.add(txtNum2);

        inputPanel.add(new JLabel("Result:"));
        txtResult = new JTextField();
        txtResult.setEditable(false);
        inputPanel.add(txtResult);

        add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 8, 8));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        String[] ops = {"+", "-", "*", "/"};
        for (String op : ops) {
            JButton btn = new JButton(op);
            btn.setFont(new Font("Arial", Font.BOLD, 16));
            btn.addActionListener((ActionEvent e) -> sendOperation(op));
            buttonPanel.add(btn);
        }

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void sendOperation(String op) {
        String num1 = txtNum1.getText().trim();
        String num2 = txtNum2.getText().trim();

        if (num1.isEmpty() || num2.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter the two numbers!", "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Socket socket = null;
        try {
            InetAddress address = InetAddress.getByName(SERVER_IP);
            socket = new Socket(address, SERVER_PORT);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            out.println(num1 + " " + num2 + " " + op);

            String response = in.readLine();
            txtResult.setText(response);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error to connect " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Client().setVisible(true);
        });
    }
}