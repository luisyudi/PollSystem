import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Poll {
    private ArrayList<Option> options_list;
    private ArrayList<Integer> usersVote_list;
    private String poll_name;
    private String status;
    private boolean showVotes;
    private int total_votes;
    private int creator;
    private long timer;

    public Poll(String poll_name, int creator, boolean showVotes, long timer){
        this.options_list = new ArrayList<>();
        this.usersVote_list = new ArrayList<>();
        this.poll_name = poll_name;
        this.creator = creator;
        this.status = "Em andamento";
        this.showVotes = showVotes;
        this.timer = timer;
        this.total_votes = 0;
    }

    public void add_option(String option){
        Option new_option = new Option(option);
        options_list.add(new_option);
    }


    //Returns a options formatted messages arraylist to print
    public ArrayList<String> get_optionsMessage(){
        ArrayList<String> message_list = new ArrayList<>();
        int counter = 1;
        if(showVotes) {
            for (Option option : this.options_list) {
                float percentage = (float) option.getVoteCount() / total_votes * 100;
                String formattedPercentage = String.format("%.1f", percentage); 

                if (formattedPercentage.equals("NaN")) {
                    //formattedPercentage = "0";
                }
                
                message_list.add("["+counter+"] " +option.getOptionName() + " - " + option.getVoteCount() + " ("+ formattedPercentage +"%)");
                counter++;
            }
        }else{
            for (Option option : this.options_list) {
                message_list.add("["+counter+"] " +option.getOptionName());
                counter++;
            }
        }

        return message_list;
    }

    public void sort_poll(){
        Collections.sort(this.options_list, new Comparator<Option>() {
            @Override
            public int compare(Option o1, Option o2) {
                return Integer.compare(o2.getVoteCount(), o1.getVoteCount()); // Ordem decrescente
            }
        });
    }

    public boolean vote(int user, int option){
        for (int userVote : this.usersVote_list) {
            if (user == userVote) {
                return false;
            }
        }
        options_list.get(option).addVote();
        usersVote_list.add(user);
        this.total_votes++;

        System.out.println("votos: " + options_list.get(option).getVoteCount());
        return true;
    }

    public void checkTimer(){
        if (this.timer >= System.currentTimeMillis()) {
            this.setStatus("Encerrado");
            this.sort_poll();
            this.setShowVotes(true);
        }
    }

    public String getPollName() {
        return poll_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCreator() {
        return creator;
    }

    public int getTotalOptions(){
        return options_list.size();
    }

    public void setShowVotes(boolean status){
        this.showVotes = status;
    }

    public ArrayList<Option> getOptions_list() {
        return options_list;
    }

    public ArrayList<Integer> getUsersVote_list() {
        return usersVote_list;
    }
    
}

