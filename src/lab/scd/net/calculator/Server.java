package lab.scd.net.calculator;

import java.io.*;
import java.net.*;

public class Server {

    public static final int PORT = 1900;

    public static void main(String[] args) throws IOException {
        ServerSocket s = new ServerSocket(PORT);
        System.out.println("Server PORT: " + PORT);

        try {
            while (true) {
                Socket socket = s.accept();
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                    String line = in.readLine();
                    if (line != null) {
                        System.out.println("Command: " + line);
                        String[] parts = line.trim().split("\\s+");

                        if (parts.length == 3) {
                            try {
                                double a = Double.parseDouble(parts[0]);
                                double b = Double.parseDouble(parts[1]);
                                String op = parts[2];
                                double res = 0;

                                switch (op) {
                                    case "+":
                                        res = a + b;
                                        out.println(String.valueOf(res));
                                        break;
                                    case "-":
                                        res = a - b;
                                        out.println(String.valueOf(res));
                                        break;
                                    case "*":
                                        res = a * b;
                                        out.println(String.valueOf(res));
                                        break;
                                    case "/":
                                        if (b == 0) {
                                            out.println("Error: Impossible to divide by zero!");
                                        } else {
                                            res = a / b;
                                            out.println(String.valueOf(res));
                                        }
                                        break;
                                    default:
                                        out.println("Invalid error!");
                                }
                            } catch (NumberFormatException e) {
                                out.println("Enter valid numbers!");
                            }
                        }
                    }
                } finally {
                    socket.close();
                }
            }
        } finally {
            s.close();
        }
    }
}
