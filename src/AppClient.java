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

    public void writeMessage(String message){
        try {
            this.bufferedWriter.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readCommand(String command){
        int spaceIndex = command.indexOf(" ");
        String commandType = command;
        
        if(spaceIndex != -1){
            commandType = command.substring(0, spaceIndex);
        }

        for (int i = 0; i < validCommands.length; i++) {
            if (commandType.equals(validCommands[i])) {
                return commandType;
            }
        }

        return "error";
    }

    public void runCommand(String type, String command){
        switch (type) {
            case "criarvotacao":
                createPoll(command);
                break;
        
            case "listarvotacoes":
                listPolls();
                break;

            case "vervotacao":
                showPoll(command);
                break;

            case "votar":
                vote(command);
                break;
            
            case "encerrar":
                endPoll(command);
                break;

            case "help":
                help();
                break;
        }
    }

    private void createPoll(String command) {
        int initialIndex = command.indexOf(" ");
        if (initialIndex == -1) {
            writeMessage("Erro na sintaxe: Nome da votaçao nao encontrado.");
            return;
        }

        command = command.substring(initialIndex + 1);
        ArrayList<String> parameters = new ArrayList<>();
        
        // Decodes options
        while (true) {
            int nextIndex = command.indexOf("$");
            if (nextIndex != -1) {
                parameters.add(command.substring(0, nextIndex));
                command = command.substring(nextIndex + 1);
            } else {
                // When options are over
                if (!command.isEmpty()) {
                    parameters.add(command.substring(0, command.indexOf(" ")));
                    command = command.substring(command.indexOf(" ") + 1, command.length());
                }
                break;
            }
        }

        // Minimum of 2 options
        if (parameters.size() < 3) {
            writeMessage("Uma votação deve possuir pelo menos 2 opções.");
            return;
        }

        // Verifies 'verVoto' and 'timer'
        String remainingCommand = command.trim();
        boolean showVotes = false;
        int hours = 0, minutes = 0;

        String[] remainingParts = remainingCommand.split(" ");
        if (remainingParts.length > 0) {
            if (remainingParts[0].equalsIgnoreCase("S")) {
                showVotes = true;
            } else if (!remainingParts[0].equalsIgnoreCase("N")) {
                writeMessage("'verVoto' deve ser 'S' ou 'N'.");
                return;
            }
        }

        // Timer
        if (remainingParts.length > 1) {
            String timer = remainingParts[1];
            String[] timeParts = timer.split("-");
            if (timeParts.length == 2) {
                try {
                    hours = Integer.parseInt(timeParts[0]);
                    minutes = Integer.parseInt(timeParts[1]);
                    // Format
                    if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59) {
                        writeMessage("Valor para timer invalido.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    writeMessage("Erro na sintaxe: Timer deve estar no formato hh-mm.");
                    return;
                }
            }
        }
        long currentTimeMillis = System.currentTimeMillis();

        // Converts to milliseconds
        long addedTimeMillis = (hours * 60 * 60 * 1000) + (minutes * 60 * 1000);
        long timer = currentTimeMillis + addedTimeMillis;

        //Creates the poll
        Poll new_poll = new Poll(parameters.get(0), this.user, showVotes, timer);

        for (String option : parameters.subList(1, parameters.size())) {
            new_poll.add_option(option);
        }

        //adds to the polls list
        polls.add(new_poll);

        writeMessage("Votacao criada com sucesso.");
    }

    private void listPolls(){
        if (polls.size() == 0) {
            writeMessage("Nenhuma votacao disponivel.");
        }

        int counter = 1;
        for (Poll poll : polls) {
            poll.checkTimer();
            try {
                this.bufferedWriter.write("[" + counter + "] " + poll.getPollName() + " - " + poll.getStatus());
                this.bufferedWriter.newLine();
                counter++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showPoll(String command){
        int initialIndex = command.indexOf(" ");
        command = command.substring(initialIndex + 1);
        int poll_number = 0;
        try {
            poll_number = Integer.valueOf(command.substring(0));
        } catch (Exception e) {
            writeMessage("Numero de votacao invalido");
            return;
        }

        if (poll_number - 1 < 0 || poll_number > polls.size()) {
            writeMessage("Numero de votacao invalido");
            return;
        }

        Poll poll = polls.get(poll_number - 1);
        poll.checkTimer();
        
        try {
            bufferedWriter.write(poll.getPollName());
            bufferedWriter.newLine();

            ArrayList<String> messages = poll.get_optionsMessage();
            for (int i = 0; i < poll.getTotalOptions(); i++) {
                bufferedWriter.write(messages.get(i));
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            // TODO: handle exception
        }
        
    }

    private void vote(String command){
        int auxIndex = command.indexOf(" ");
        command = command.substring(auxIndex + 1);
        auxIndex = command.indexOf(" ");

        int poll_number = -1;
        try {
            poll_number = Integer.valueOf(command.substring(0, auxIndex));
        } catch (Exception e) {
            writeMessage("Numero de votacao invalido");
            return;
        }

        if (polls.get(poll_number - 1).getStatus().equals("Encerrado")) {
            writeMessage("Votacao ja foi encerrada");
            return;
        }

        command = command.substring(auxIndex + 1);
        int option_number = -1;

        try {
            option_number = Integer.valueOf(command);
        } catch (Exception e) {
            writeMessage("Opcao invalida");
            return;
        }

        //Option_number < 0 or option_number > number of options in poll
        if (option_number < 0 || option_number > polls.get(poll_number).getOptions_list().size()) {
            writeMessage("Opcao invalida");
            return;
        }


        boolean voted = polls.get(poll_number - 1).vote(this.user, option_number - 1);
        
        if (voted) {
            writeMessage("Voto realizado com sucesso.");
        }else {
            writeMessage("Usuario ja votou nessa votacao");
        }
    }

    private void endPoll(String command){
        int auxIndex = command.indexOf(" ");
        command = command.substring(auxIndex + 1);
        auxIndex = command.indexOf(" ");
        int poll_number = -1;
        try {
            poll_number = Integer.valueOf(command.substring(0));
        } catch (Exception e) {
            writeMessage("Numero de votacao invalido.");
            return;
        }

        if (poll_number > 0 && poll_number <= polls.size()) {
            if (polls.get(poll_number - 1).getCreator() == this.user) {
                polls.get(poll_number - 1).setStatus("Encerrado");
                polls.get(poll_number - 1).sort_poll();
                polls.get(poll_number - 1).setShowVotes(true);
                writeMessage("Votacao encerrada com sucesso.");
            }else{
                writeMessage("Usuario nao possui permissao para encerrar essa votacao.");
            }
        }else{
            writeMessage("Numero de votacao invalido.");
        }
    }

    private void help(){
        writeMessage("Os comandos disponiveis sao:\n-criarvotacao\n-listarvotacoes\n-vervotacao\n-votar\n-encerrar\nLeia o protocolo para mais informacoes na utilizacao de comandos");
    }

    @Override
    public void run(){
        String command;
        while (clientSocket.isConnected()) {
            try {
                command = bufferedReader.readLine();
                String commandType = readCommand(command);
                System.out.println(commandType);
                if(commandType.equals("error")){
                    try {
                        bufferedWriter.write("Comando nao encontrado. Digite help para ver os comandos disponiveis");
                        bufferedWriter.newLine();
                        bufferedWriter.flush();     
                    } catch (IOException e) {
                        // TODO: handle exception
                        break;
                    }
                }else{
                    runCommand(commandType, command);
                    try {
                        this.bufferedWriter.flush();
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                break;
            }
        }
    }
    
}