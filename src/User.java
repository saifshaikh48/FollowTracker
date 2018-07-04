import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class User {
  protected String name;
  protected String username;
  protected List<User> followers;
  protected List<User> following;

  public User(String name, String username) throws IllegalArgumentException {
    if (username == null) {
      throw new IllegalArgumentException("Username cannot be null");
    }

    if (name == null) {
      this.name = "";
    }
    else {
      this.name = name;
    }

    this.username = username;
    this.followers = new ArrayList<>();
    this.following = new ArrayList<>();
  }

  public void addFollowing(User u) throws IllegalArgumentException {
    if (this.following.contains(u)) {
      throw new IllegalArgumentException(this + " is already following " + u);
    }
    this.following.add(u);
  }

  public void addFollower(User u) throws IllegalArgumentException {
    if (this.followers.contains(u)) {
      throw new IllegalArgumentException(this + " is already followed by " + u);
    }
    this.followers.add(u);
  }

  public List<User> getFollowers() {
    return followers;
  }

  public List<User> getFollowing() {
    return following;
  }

  public boolean isFollowedBy(User u) {
    return this.followers.contains(u);
  }

  public boolean isFollowing(User u) {
    return this.following.contains(u);
  }

  public int numEdgesAway(User u) throws IllegalArgumentException {
    return numEdgesAwayHelp(u, 0, new ArrayList<User>());
  }

  private int numEdgesAwayHelp(User u, int acc, List<User> seen) {
    //if (seen.contains(u))
    if (this.equals(u)) { //override equals and hashcode
      return acc;
    }
    else if (this.isFollowedBy(u) || this.isFollowing(u)) {
      return acc + 1;
    }
    else {
      seen.addAll(this.getFollowing());
      seen.addAll(this.getFollowers());
      return numEdgesAwayHelp(u, acc + 1, seen);
    }
  }

  @Override
  public String toString() {
    return "( " + this.username + " : " + this.name + " )";
  }
}

/*
 * Represents a person.
 *
 * TEMPLATE:
 *  Fields:
 *  - ... this.username ... - String
 *  - ... this.buddies ...  - ILoBuddy
 *  - ... this.diction ...  - double
 *  - ... this.hearing ...  - double
 *  Methods:
 *  - ... this.hasDirectBuddy(Person) ...                       - boolean
 *  - ... this.partyCount() ...                                 - int
 *  - ... this.countCommonbuddies(Person) ...                   - int
 *  - ... this.hasExtendedBuddy(Person) ...                     - boolean
 *  - ... this.addBuddy(Person) ...                             - void
 *  - ... this.getExtendedBuddies(ILoBuddy) ...                 - ILoBuddy
 *  - ... this.maxLikelihood(Person) ...                        - double
 *  - ... this.maxLikelihoodHelp(Person, double, ILoBuddy) ...  - double
 *  Methods on Fields:
 *  - ... this.buddies.contains(Person) ...                         - boolean
 *  - ... this.buddies.countCommon(ILoBuddy) ...                    - int
 *  - ... this.buddies.getExtendedBuddies(ILoBuddy) ...             - ILoBuddy
 *  - ... this.buddies.length() ...                                 - int
 *  - ... this.buddies.maxLikelihood(Person, double, ILoBuddy) ...  - double
 */