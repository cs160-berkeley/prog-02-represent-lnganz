package com.represent.sigma.represent;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sigma on 2/28/2016.
 */
public class Representative implements Parcelable {
    public String name;
    public String party;
    public String chamber;
    public String emailAddress;
    public String website;
    public String latestTweet;
    public String endDate;
    public String committees;
    public String recentBills;
    public String twitterId;
    public String bioguideId;
    public String imageUrl;

    public Representative(String name) {
        this.name = name;
    }

    public Representative() {
        this.name = "PLACEHOLDER NAME";
        this.party = "X";
        this.emailAddress = "ABC@CDF.COM";
        this.website = "COOLSITE.COM";
        this.latestTweet = "JUST ATE A SANDWICH";
        this.endDate = "1/1/1970";
        this.committees = "COOL COMMITTEES";
        this.recentBills = "COOL BILLS";
        this.chamber = "DEFAULT CHAMBER";
        this.twitterId = "DEFAULT TWITTERID";
        this.bioguideId = "DEFAULT BIOGUIDEID";
        this.imageUrl = "DEFAULT URL";
    }

    public Representative(Parcel in) {
        String[] data = new String[11];
        in.readStringArray(data);
        this.name = data[0];
        this.party = data[1];
        this.emailAddress = data[2];
        this.website = data[3];
        this.endDate = data[4];
        this.committees = data[5];
        this.recentBills = data[6];
        this.chamber = data[7];
        this.twitterId = data[8];
        this.bioguideId = data[9];
        this.imageUrl = data[10];

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.name, this.party, this.emailAddress, this.website, this.endDate, this.committees, this.recentBills, this.chamber, this.twitterId, this.bioguideId, this.imageUrl});
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Representative createFromParcel(Parcel in) {
            return new Representative(in);
        }
        public Representative[] newArray(int size) {
            return new Representative[size];
        }
    };
}
