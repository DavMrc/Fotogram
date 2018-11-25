package com.example.utente.fotogram;

public class User {
    private String nickname;
    private String password;
    private String session_id;
    //    private propic

    public User(String nickname, String password) {
        this.nickname = nickname;
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    @Override
    public String toString() {
        return "Nickname: "+this.nickname+", Password: "+this.password+", Session ID: "+this.session_id;
    }
}
