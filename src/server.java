import java.net.ServerSocket;
import java.net.Socket;

public class server {

    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(4002);
        System.out.println("Server criado");
        while (true) {
            try {
                Socket socketClient = server.accept();
                AppClient appClient = new AppClient(socketClient);
                appClient.start();
            } catch (Exception e) {
                server.close();
                break;
            }
        }
        System.out.println("Servidor encerrado");
    }
}
