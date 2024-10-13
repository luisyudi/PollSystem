public class Option {
    private String option_name;
    private int vote_count;

    public Option(String option_name){
        this.option_name = option_name;
        this.vote_count = 0;
    }

    public String getOptionName() {
        return option_name;
    }

    public int getVoteCount() {
        return vote_count;
    }

    public void addVote() {
        this.vote_count++;
    }
}

