package ru.rudnev.test.java;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.1.0.1", 8000);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Подключено к серверу.");
        Scanner scanner = new Scanner(System.in);

        new Thread(() -> {
            try {
                String serverResponse;
                while ((serverResponse = in.readLine()) != null) {
                    System.out.println(serverResponse);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        String userInput;
        while ((userInput = stdIn.readLine()) != null) {
            out.println(userInput);
            if (userInput.equalsIgnoreCase("Stop")){
                break;
            }
        }
        socket.close();
    }
}