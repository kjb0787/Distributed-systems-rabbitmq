package model;

public class Resort {
  private final String time;
  private final String numSkiers;

  public Resort(String time, String numSkiers) {
    this.time = time;
    this.numSkiers = numSkiers;
  }

  public String getTime() {
    return time;
  }

  public String getNumSkiers() {
    return numSkiers;
  }
}
