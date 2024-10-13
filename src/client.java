import java.net.Socket;

public class client {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, client!");
        Socket socket = new Socket("localhost", 4002);
    }
}