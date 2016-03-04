package com.represent.sigma.represent;

/**
 * Created by Sigma on 2/28/2016.
 */
public class Representative {
    public String name;
    public char party;
    public String emailAddress;
    public String website;
    public String latestTweet;
    public String endDate;
    public String committees;
    public String recentBills;

    public Representative(String name) {
        this.name = name;
    }

    public Representative() {
        this.name = "PLACEHOLDER NAME";
        this.party = 'X';
        this.emailAddress = "ABC@CDF.COM";
        this.website = "COOLSITE.COM";
        this.latestTweet = "JUST ATE A SANDWICH";
        this.endDate = "1/1/1970";
        this.committees = "COOL COMMITTEES";
        this.recentBills = "COOL BILLS";
    }
}
