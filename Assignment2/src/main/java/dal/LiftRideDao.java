package dal;

import java.sql.ResultSet;
import java.sql.SQLException;

import model.LiftRide;
import model.Resort;

public class LiftRideDao {

  public int getVerticalsBySkierId(String skierIdStr) throws SQLException {
    int verticals = 0;
    String query = "SELECT COUNT(*) AS Verticals FROM LiftRides WHERE skierId = " + skierIdStr;
    DBConnectionManager manager = null;
    try {
      manager = new DBConnectionManager();
    } catch (Exception e) {
      e.printStackTrace();
    }
    assert manager != null;
    ResultSet rs = manager.execReadOnlyQuery(query);
    if (rs.next()) verticals = rs.getInt("Verticals") * 100;
    return verticals;
  }

  public int getVerticalsBySkierIdAndDay(String skierId, String dayId) throws SQLException {
    int verticals = 0;
    String query = "SELECT COUNT(*) AS Vertical FROM LiftRides WHERE skierId = " + skierId
            + " AND dayId = " + dayId;
    DBConnectionManager manager = null;
    try {
      manager = new DBConnectionManager();
    } catch (Exception e) {
      e.printStackTrace();
    }
    assert manager != null;
    ResultSet rs = manager.execReadOnlyQuery(query);
    if (rs.next()) verticals = rs.getInt("Vertical") * 100;
    return verticals;
  }

  public Resort getResort(String dayId) throws SQLException {
    int uniqueSkiers = 0;
    String query = "SELECT COUNT(*) AS skierNum FROM (SELECT DISTINCT SkierId FROM LiftRides WHERE dayId = " + dayId + ") AS temp;";
    DBConnectionManager manager = null;
    try {
      manager = new DBConnectionManager();
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (manager == null) throw new AssertionError();
    ResultSet rs = manager.execReadOnlyQuery(query);
    if (rs.next()) uniqueSkiers = rs.getInt("skierNum") * 100;
    return new Resort("Mission Ridge", Integer.toString(uniqueSkiers));
  }

  private static String paramParse(String... args) {
    return "(" + String.join(",", args) + ")";
  }

  public static boolean createLiftRide(LiftRide newLiftRide) {
    String vertical = "100";
    String query = "INSERT INTO LiftRides(SkierId, ResortId, SeasonId, DayId, Time, LiftId, Vertical) VALUES";
    query += paramParse(Integer.toString(newLiftRide.getSkierId()),
            Integer.toString(newLiftRide.getResortId()),
            Integer.toString(newLiftRide.getSeasonId()),
            Integer.toString(newLiftRide.getDayId()),
            Integer.toString(newLiftRide.getTime()),
            Integer.toString(newLiftRide.getLiftId()),
            vertical);
    query += " ON DUPLICATE KEY UPDATE Vertical = Vertical";
    // System.out.println(query);
    try (
            DBConnectionManager manager = new DBConnectionManager();
            ResultSet rs = manager.execWriteOnlyQuery(query);
    ) {
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
}
