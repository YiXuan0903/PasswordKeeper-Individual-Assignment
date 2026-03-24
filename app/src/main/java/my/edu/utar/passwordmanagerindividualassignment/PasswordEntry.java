package my.edu.utar.passwordmanagerindividualassignment;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "password_table")
public class PasswordEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String siteName;
    private String username;
    private String password;
    private String extraPin;
    private String challengeQuestion;
    private String challengeAnswer;

    public PasswordEntry(int id, String siteName, String username, String password,
                         String extraPin, String challengeQuestion, String challengeAnswer) {
        this.id = id;
        this.siteName = siteName;
        this.username = username;
        this.password = password;
        this.extraPin = extraPin;
        this.challengeQuestion = challengeQuestion;
        this.challengeAnswer = challengeAnswer;
    }

    public int getId() { return id; }
    public String getSiteName() { return siteName; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getExtraPin() { return extraPin; }
    public String getChallengeQuestion() { return challengeQuestion; }
    public String getChallengeAnswer() { return challengeAnswer; }

    public void setId(int id) { this.id = id; }
    public void setSiteName(String siteName) { this.siteName = siteName; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setExtraPin(String extraPin) { this.extraPin = extraPin; }
    public void setChallengeQuestion(String challengeQuestion) { this.challengeQuestion = challengeQuestion; }
    public void setChallengeAnswer(String challengeAnswer) { this.challengeAnswer = challengeAnswer; }
}