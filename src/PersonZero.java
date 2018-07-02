/**
 *
 */
public class PersonZero extends User {
  private final String password;

  public PersonZero(String name, String username, String password) {
    super(name, username);
    this.password = password;
  }
}
