package com.example.giveapp;

import java.io.Serializable;

public class PassChallenges implements Serializable {

    private String friendId;
    private String challengeId;
    private String friendName;
    private String challengeName;
    private String status;

    public PassChallenges() {

    }

    public PassChallenges(String friendId, String challengeId, String friendName, String challengeName, String status) {
        this.friendId = friendId;
        this.challengeId = challengeId;
        this.friendName = friendName;
        this.challengeName = challengeName;
        this.status = status;
    }

    public String getFriendId() {
        return friendId;
    }

    public String getChallengeId() {
        return challengeId;
    }

    public String getFriendName() {
        return friendName;
    }

    public String getChallengeName() {
        return challengeName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setChallengeId(String challengeId) {
        this.challengeId = challengeId;
    }

}
