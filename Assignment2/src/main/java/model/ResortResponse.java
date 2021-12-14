package model;

public class ResortResponse {
  private final String resortID;
  private final int totalVert;

  public ResortResponse(String resortID, int totalVert) {
    this.resortID = resortID;
    this.totalVert = totalVert;
  }

  public int getTotalVert() {
    return totalVert;
  }

  public String getResortID() {
    return resortID;
  }
}
