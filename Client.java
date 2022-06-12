// client socket to run on controller

import java.util.Scanner;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.IOException;
import java.net.UnknownHostException;

public class Client {
    static Thread sent;
    static Thread receive;
    static Socket socket;

    public static void main(String args[]){
            try {
                socket = new Socket("localhost",65432);
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            sent = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        PrintWriter socketOut = new PrintWriter(socket.getOutputStream(), true);
                        Scanner consoleIn = new Scanner(System.in);
                        while(true){
                            String input = new String(consoleIn.nextLine());
                            if (input.equals("")) {
                                break;
                            }
                            socketOut.print(input);
                            socketOut.flush(); // force print throughsocket
                        }
                        consoleIn.close();

                    }
                    catch (NullPointerException e) { // socket does not exist
                        System.out.println("Cannot connect");
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            });
            
        sent.start();
        try {
            sent.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}