package data;

public class CreateUser {
    private String email;
    private String password;
    private String name;

    public CreateUser withEmail(String email) {
        this.email = email;
        return this;
    }

    public CreateUser withPassword(String password) {
        this.password = password;
        return this;
    }

    public CreateUser withName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }
}
