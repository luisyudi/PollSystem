import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class server {

    public static void main(String[] args) throws Exception {
        
        ServerSocket server = new ServerSocket(4002);
        System.out.println("Server criado");
        int count = 1;
        while (true) {
            try {
                Socket socketClient = server.accept();
                AppClient appClient = new AppClient(socketClient);

                System.out.println("Novo Cliente conectado: " + count);
                appClient.start();
                count++;
            } catch (Exception e) {
                // TODO: closeServer();
            }
        }
    }
}
