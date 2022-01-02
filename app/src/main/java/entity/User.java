package entity;

public class User {
    private String nickname;
    private int socketfd;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getSocketfd() {
        return socketfd;
    }

    public void setSocketfd(int socketfd) {
        this.socketfd = socketfd;
    }
}
