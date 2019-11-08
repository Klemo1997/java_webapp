package main;

public class User {

    private String userName;
    private String password;
    private int id;

    public User()
    {

    }

    public void setUser(String userName, String password)
    {
        this.id = getIdByName(userName);
        this.userName = userName;
        this.password = password;
    }

    public boolean verify()
    {
        return this.userName.equals("admin") && this.password.equals("admin");
    }

    public String getName()
    {
        return this.userName;
    }

    public int getId() {
        return this.id;
    }

    private int getIdByName(String userName){
        if (userName.equals("admin")) {
            return 1;
        }
        return -1;
    }
}
