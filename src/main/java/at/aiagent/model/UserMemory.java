package at.aiagent.model;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserMemory {

    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public boolean hasUserName() {
        return userName != null && !userName.isBlank();
    }

    public void rememberUserNameFromMessage (String message) {

        Pattern pattern = Pattern.compile(
                "(?i)\\b(?:ich heiße|mein name ist|ich bin)\\s+([\\p{L}][\\p{L}'-]*)"
        );

        Matcher matcher= pattern.matcher(message);

        if (matcher.find()) {
            setUserName(matcher.group(1));
        }
    }
}
