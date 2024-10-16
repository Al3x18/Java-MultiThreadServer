import java.io.*;
import java.net.*;

public class Client {
    
    private static BufferedReader reader = null;
    private static String ask(String prompt) throws IOException {
        System.out.print(prompt + "");
        return reader.readLine();
    }
    public static void main(String[] args) {
        String userInput;
        reader = new BufferedReader(new InputStreamReader(System.in));

        if (args.length != 2) {
            System.err.println("Usage: java Client <host> <port>.\nExample: java Client localhost 8080");
            System.exit(1);
        }

        try {
            String host = args[0];
            int port = Integer.parseInt(args[1]);

            Socket socket = new Socket(host, port);

            System.out.println(String.format("Connected to '%s' on port %d", host, port));

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            while (!(userInput = ask("Type something ('exit' to close connection) >>> ")).equalsIgnoreCase("exit")) {

                objectOutputStream.writeObject(userInput);
                objectOutputStream.flush();

                
                String response = (String) objectInputStream.readObject();
                System.out.println("Server response: " + response);
            }

            // Close connection in case user types "exit"
            objectOutputStream.writeObject("exit");
            objectOutputStream.flush();
            
            System.out.println("Connection closed");
            socket.close();

        } catch (NumberFormatException e) {
            System.err.println("<port> must be an integer");
            System.exit(1);
        } catch (ConnectException e) {
            System.err.println("Connection refused. Is the server running on port " + args[1] + "?");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
