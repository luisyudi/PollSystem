import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class AppClient extends Thread {
    private static ArrayList<Poll> polls = new ArrayList<>();
    private static int lastUser = 1;
    private static String validCommands[] = {"criarvotacao", "listarvotacoes", "vervotacao", "votar", "encerrar"};
    private Socket clientSocket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private int user;
    
    public AppClient(Socket socket){
        try {
            this.user = lastUser;
            lastUser++;
            
            this.clientSocket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //readline
        } catch (IOException e) {
            // TODO: handle exception
        }
        
    }

    public String readCommand(String command){
        int spaceIndex = command.indexOf(" ");
        String commandType;
        
        if(spaceIndex != -1){
            commandType = command.substring(0, spaceIndex);
        }else{
            return "error";
        }

        for (int i = 0; i < validCommands.length; i++) {
            if (commandType.equals(validCommands[i])) {
                return commandType;
            }
        }

        return "error";
    }

    @Override
    public void run(){
        String command;
        while (clientSocket.isConnected()) {
            try {
                command = bufferedReader.readLine();
                String commandType = readCommand(command);
                if(commandType == "error"){
                    bufferedWriter.write("Comando nao encontrado. Digite help para ver os comandos disponiveis");
                    bufferedWriter
                }else{

                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
}