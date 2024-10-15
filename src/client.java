import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
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
        } catch (IOException e) {
            closeClient(clientSocket, bufferedReader, bufferedWriter);
        }
    }

    public static void closeClient(Socket socket, BufferedReader bf, BufferedWriter bw){
        System.out.println("Conexao Perdida");
        try {
            if (bf != null) {
                bf.close();
            }
            if (bw != null) {
                bw.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 4002);
        Scanner scanner = new Scanner(System.in);
        client client = new client(socket);

        if (client.clientSocket.isConnected()) {
            System.out.println("Conectado ao servidor de votacoes.");
        }else{
            System.out.println("Erro ao se conectar ao servidor. Verifique se o servidor esta sendo executado.");
        }

        while (client.clientSocket.isConnected()) {
            String message = scanner.nextLine();
            client.bufferedWriter.write(message);
            client.bufferedWriter.newLine();
            client.bufferedWriter.flush();

            StringBuilder response = new StringBuilder();
            char[] buffer = new char[1024]; //temporary buffer
            
            //awaits for the response
            try {
                int readChars;
                while ((readChars = client.bufferedReader.read(buffer, 0, buffer.length)) != -1) {
                    response.append(buffer, 0, readChars); // adds characters to the full response
                    if (readChars < buffer.length) { // Finish reading from buffer
                        break;
                    }
                }
                System.out.println(response.toString()); 
            } catch (IOException e) {
                closeClient(client.clientSocket, client.bufferedReader, client.bufferedWriter);
            }
            
        } 
        scanner.close();
    }
}