import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

  public static Socket clientSocket;
  public static BufferedReader in;
  public static PrintWriter out;
  public static Scanner keyboard = new Scanner(System.in);
  public static String username;

  public static void main(String[] args) {
    
    try {
      System.out.print("Enter your username: ");  
      username = keyboard.nextLine();
      clientSocket = new Socket("127.0.0.1", 8080);
      out = new PrintWriter(clientSocket.getOutputStream());
      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      out.println(username + " has entered the chat.");
      out.flush();

      Thread sender = new Thread(new Runnable() {
        String msg;
        @Override
        public void run() {
          while (clientSocket != null) {
            msg = username + ": " + keyboard.nextLine();
            if(msg.equals(username + ": exit"))
              exitMessage();
            out.println(msg);
            out.flush();
          }
        }
      });
      sender.start();

      Thread receiver = new Thread(new Runnable() {
        String msg;
        @Override
        public void run() {
          try {
            while (clientSocket != null) {
              msg = in.readLine();
              System.out.println(msg);
              if(msg.equals("Server is shutting down."))
                serverClosed();
            }

            out.close();
            clientSocket.close();
            
          } catch (IOException e) {
            exitMessage();
          }
        }
      });
      receiver.start();

    } catch (IOException e) {
      System.out.println("Error 6: Attempt to establish conneciton failed.");
      e.printStackTrace();
    }
  }

  public static void exitMessage() {
    try {
      
      System.out.println("You are leaving the chat");
      System.out.println("Press enter to exit to command prompt.");
      out.println(username + " has left the chat.");
      out.flush();

      in.close();
      out.close();
      clientSocket.close();
      keyboard.close();

      System.exit(0);

    } catch(IOException e) {
      System.out.println("Error 7: Shut down procedure failed.");
      e.printStackTrace();
    }
  }

  public static void serverClosed() {
    try {

      in.close();
      out.close();
      clientSocket.close();

    } catch(IOException e) {
      System.out.println("Error 8: Server shutdown procedure failed.");
      e.printStackTrace();
    }
  }
}
