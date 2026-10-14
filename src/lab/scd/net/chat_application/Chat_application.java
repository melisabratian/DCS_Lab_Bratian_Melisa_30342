package lab.scd.net.chat_application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Chat_application extends JFrame {

    private JTextArea chatHistory;
    private JTextField messageInput;
    private JButton sendButton;

    private DatagramSocket socket;
    private String remoteAddress;
    private int localPort;
    private int remotePort;

    public Chat_application(String title, int localPort, String remoteAddress, int remotePort) {
        this.localPort = localPort;
        this.remoteAddress = remoteAddress;
        this.remotePort = remotePort;

        try {
            this.socket = new DatagramSocket(localPort);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Eroare la deschiderea socket-ului pe portul " + localPort);
            e.printStackTrace();
            System.exit(1);
        }
        setTitle(title + " (Port Local: " + localPort + " -> Destinatie: " + remoteAddress + ":" + remotePort + ")");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        chatHistory = new JTextArea();
        chatHistory.setEditable(false);
        chatHistory.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(chatHistory);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        messageInput = new JTextField();
        sendButton = new JButton("Send");

        bottomPanel.add(messageInput, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        messageInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        new ReceiverThread().start();
        setVisible(true);
    }

    private void sendMessage() {
        String msg = messageInput.getText().trim();
        if (msg.isEmpty()) return;

        try {
            byte[] buffer = msg.getBytes();
            InetAddress address = InetAddress.getByName(remoteAddress);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, remotePort);

            socket.send(packet);

            chatHistory.append("Eu: " + msg + "\n");
            messageInput.setText("");
        } catch (Exception ex) {
            chatHistory.append("[System Error] Nu s-a putut trimite mesajul.\n");
            ex.printStackTrace();
        }
    }

    private class ReceiverThread extends Thread {
        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            while (!socket.isClosed()) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                    socket.receive(packet);

                    String receivedMsg = new String(packet.getData(), 0, packet.getLength());

                    SwingUtilities.invokeLater(() -> {
                        chatHistory.append("Partener: " + receivedMsg + "\n");
                    });

                } catch (Exception e) {
                    if (socket.isClosed()) break;
                    e.printStackTrace();
                }
            }
        }
    }
}
