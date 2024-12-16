package com.example.synthwavechat;

public class ChatMessage {
    private String nickname;
    private String message;
    private String profilePictureUrl;

    public ChatMessage(String nickname, String message, String profilePictureUrl)
    {
        this.nickname = nickname;
        this.message = message;
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public String getMessage() {
        return message;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }
}
