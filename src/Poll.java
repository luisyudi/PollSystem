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

    public Poll(String poll_name, int creator, boolean showVotes){
        this.options_list = new ArrayList<>();
        this.usersVote_list = new ArrayList<>();
        this.poll_name = poll_name;
        this.creator = creator;
        this.status = "Em andamento";
        this.showVotes = showVotes;
        this.total_votes = 0;
    }

    public void add_option(String option){
        Option new_option = new Option(option);
        options_list.add(new_option);
    }

    //Returns a options formatted messages arraylist to print
    public ArrayList<String> get_optionsMessage(){
        ArrayList<String> message_list = new ArrayList<>();

        if(showVotes) {
            for (Option option : this.options_list) {
                float percentage = (float) option.getVoteCount() / total_votes * 100;
                String formattedPercentage = String.format("%.1f", percentage); 
                
                message_list.add(option.getOptionName() + " - " + option.getVoteCount() + " ("+ formattedPercentage +"%)");
            }
        }else{
            for (Option option : this.options_list) {
                message_list.add(option.getOptionName());
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
        options_list.get(option - 1).addVote();
        this.total_votes++;
        return true;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

