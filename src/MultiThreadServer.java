import java.io.*;
import java.net.*;
import java.util.logging.Logger;

class MultiThreadServer {
    static Logger logger = Logger.getLogger("serverLogger");

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket socket = null;

        if (args.length != 1) {
            System.err.println("Usage: java MultiThreadServer <port>");
            System.exit(1);
        }
        
        System.out.println("Server starting...");

        try {
            int port = Integer.parseInt(args[0]);

            serverSocket = new ServerSocket(port);
            System.out.println(String.format("Server running on port %s (CTRL + C to stop)", port));

            while (true) {
                socket = serverSocket.accept();
                logger.info(String.format("Client '%s' connected.", socket.getInetAddress().getHostAddress()));

                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();

                logger.info("Thread started: " + thread.getName());

            }
        } catch (NumberFormatException e) {
            System.err.println("<port> must be an integer");
            System.exit(1);
        } catch (EOFException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    static Logger logger = Logger.getLogger("serverLogger");

    private Socket socketToClient;

    public ClientHandler(Socket socketToClient) {
        this.socketToClient = socketToClient;
    }

    @Override
    public void run() {
        try {
            String userInput;
            ObjectInputStream objectInputStream = new ObjectInputStream(socketToClient.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socketToClient.getOutputStream());

            while (true) {
                try {
                    userInput = (String) objectInputStream.readObject();

                if (userInput.equalsIgnoreCase("exit")) {
                    break;  // while loop will terminate here
                }

                String messageToClient = String.format("Hello %s, the server has received your message '%s'.", socketToClient.getInetAddress().getHostAddress(), userInput);

                objectOutputStream.writeObject(messageToClient);
                objectOutputStream.flush();

                logger.info(String.format("Response sent to client '%s'.", socketToClient.getInetAddress().getHostAddress()));

                } catch (EOFException e) {
                    logger.info(String.format("Connection to client '%s' lost.", socketToClient.getInetAddress().getHostAddress()));
                    break;  // while loop will terminate here
                }
            }

            socketToClient.close();
            logger.info(String.format("Client '%s' disconnected.", socketToClient.getInetAddress().getHostAddress()));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        } 
    }
}