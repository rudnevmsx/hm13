package ru.rudnev.test.java;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Server {
    // Определение порта, на котором будет работать сервер
    private static final int PORT = 8000;
    // ConcurrentHashMap для хранения соединений с клиентами. Ключ - сокет, значение - поток вывода.
    private static ConcurrentHashMap<Socket, PrintWriter> clients = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Сервер запущен на порту " + PORT);

        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                clients.put(clientSocket, out);

                new Thread(new Handler(clientSocket)).start();
            }
        } finally {
            // Закрытие серверного сокета
            serverSocket.close();
        }
    }
    // Внутренний класс для обработки соединений с клиентами
    private static class Handler implements Runnable {
        private Socket socket;
        private BufferedReader in;

        public Handler(Socket socket) throws IOException {
            this.socket = socket;
            // Инициализация потока ввода для сокета
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.println("Доступные операции: +, -, *, /");
                while (true){
                    String input = in.readLine();
                    if (input == null){
                        break;
                    }
                    String [] values = input.split(" ");
                    if (values.length != 3){
                        out.println("Неверный формат запроса");
                        continue;
                    }
                    double num1, num2, result;
                    try {
                        num1 = Double.parseDouble(values[0]);
                        num2 = Double.parseDouble(values[2]);
                    } catch (NumberFormatException e){
                        out.println("Неверный формат чисел");
                        continue;
                    }
                    switch (values[1]) {
                        case "+":
                            result = num1 + num2;
                            break;
                        case "-":
                            result = num1 - num2;
                            break;
                        case "*":
                            result = num1 * num2;
                            break;
                        case "/":
                            if (num2 == 0) {
                                out.println("Деление на ноль");
                                continue;
                            }
                            result = num1 / num2;
                            break;
                        default:
                            out.println("Неверная операция");
                            continue;
                    }
                    out.println(result);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
