import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.ArrayList;
import java.lang.Thread;

public class Server {

    public static ServerSocket serverSocket;
    public static Socket clientSocket;
    public static ArrayList<Socket> clients = new ArrayList<>();
    public static BufferedReader in;
    public static ArrayList<BufferedReader> readers = new ArrayList<>();
    public static PrintWriter out;
    public static ArrayList<PrintWriter> senders = new ArrayList<>();
    public static Scanner keyboard = new Scanner(System.in);

  public static void main(String[] args) {

    try {
      serverSocket = new ServerSocket(8080);
      System.out.println("The server is ready.");  

      Thread connector = new Thread(new Runnable() {
          @Override
          public void run() {
            while (serverSocket != null) {
              try {
                clientSocket = serverSocket.accept();
                clients.add(clientSocket);
                out = new PrintWriter(clientSocket.getOutputStream());
                senders.add(out);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                readers.add(in);
              } catch (IOException e) {
                System.out.println("Error 0: A new connection attempted to establish and failed.");
                e.printStackTrace();
              }
            }
          }
        });
        connector.start();

        Thread sender = new Thread(new Runnable() {
          String msg;

          @Override
          public void run() {
            while (serverSocket != null) {
              msg = "Server: " + keyboard.nextLine();

              if(msg.equals("Server: exit"))
                  exitMessage();

              for(int i = 0; i < senders.size(); i++) {
                out = senders.get(i);
                out.println(msg);
                out.flush();            
              }
            }
          }
        });
        sender.start();

        Thread receive = new Thread(new Runnable() {
          String msg;

          @Override
          public void run() {

            try {
              while(readers.size() == 0)
                java.lang.Thread.sleep(500);
            } catch (Exception e) {
              System.out.println("Error 1: Attempt to sleep failed.");
              System.out.println(e);
            }

            while(serverSocket != null) {
              for(int i = 0; i < readers.size(); i++) {
                try {
                  in = readers.get(i);
                  if(in.ready()) {
                    msg = in.readLine();
                    System.out.println(msg);

                    for(int j = 0; j < senders.size(); j++) {
                      if(i != j) {
                        out = senders.get(j);
                        out.println(msg);
                        out.flush();
                      }
                
                    }
                  }  
                } catch (IOException e) {
                  exitMessage();
                }
              }
            }
          }              
        });
        receive.start();
      
    } catch (IOException e) {
      System.out.println("Error 4: Attempt to establish a connection or run any threads failed.");
      e.printStackTrace();
    }
  }

  public static void exitMessage() {
    try {
      
      for(int i = 0; i < clients.size(); i++) {
        out = senders.get(i);
        out.println("Server is shutting down.");
        out.flush();
        senders.get(i).close();
        readers.get(i).close();
        clients.get(i).close();
      }

      in.close();
      out.close();
      clientSocket.close();
      serverSocket.close();
      keyboard.close();
      System.exit(0);

    } catch(IOException e) {
      System.out.println("Error 3: Shut down procedure failed.");
      e.printStackTrace();
    }
  }

    
}
