import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class client {
    private Socket clientSocket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public client(Socket socket){
        try {
            this.clientSocket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Hello, client 2!");
        Socket socket = new Socket("localhost", 4002);
        Scanner scanner = new Scanner(System.in);
        client client = new client(socket);

        while (client.clientSocket.isConnected()) {
            String message = scanner.nextLine();
            client.bufferedWriter.write(message);
            client.bufferedWriter.newLine();
            client.bufferedWriter.flush();

            StringBuilder response = new StringBuilder();
            char[] buffer = new char[1024]; // Buffer de leitura temporário
            
            //awaits for the response
            try {
                int readChars;
                while ((readChars = client.bufferedReader.read(buffer, 0, buffer.length)) != -1) {
                    response.append(buffer, 0, readChars); // Adiciona os caracteres lidos à resposta
                    if (readChars < buffer.length) { // Se o buffer está menor que o tamanho lido, é o fim dos dados
                        break;
                    }
                }
                System.out.println(response.toString()); // Imprime a resposta completa
            } catch (Exception e) {
                // TODO: handle exception
            }
            
        } 
        scanner.close();
    }
}