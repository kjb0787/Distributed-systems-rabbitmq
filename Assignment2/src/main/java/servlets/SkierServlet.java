package servlets;

import com.google.gson.Gson;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dal.LiftRideDao;
import model.Message;
import model.ResortResponse;

@WebServlet(name = "SkierServlet", value = "/skiers/*")
public class SkierServlet extends HttpServlet {
  private final int URL_ARR_LENGTH = 8;
  private final int DAY_ID_MIN = 1;
  private final int DAY_ID_MAX = 366;
  private final String SEASONS_PARAMETER = "seasons";
  private final String DAYS_PARAMETER = "days";
  private final String SKIERS_PARAMETER = "skiers";
  private final Gson gson  = new Gson();

  protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
    res.setContentType("application/json");
    String urlPath = req.getPathInfo();
    // check we have a URL!
    if (urlPath == null || urlPath.isEmpty()) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      res.getWriter().write("missing parameters");
      return;
    }

    String[] urlParts = urlPath.substring(1).split("/");

    switch (urlParts.length) {
      // routing /{skierID}/vertical
      case 2: {
        if (!urlParts[1].equals("vertical")) {
          badRequest(res, "wrong parameter");
          return;
        }
//        String resortId = req.getParameter("resort");
//        if (resortId == null) {
//          badRequest(res, "please provide resort ID");
//          return;
//        }
//        String seasonId = req.getParameter("season");
//        try {
//          Integer.parseInt(seasonId);
//        } catch (Exception e){
//          badRequest(res, "invalid season ID");
//          return;
//        }
        String skierId = urlParts[0];
        try {
          Integer.parseInt(skierId);
        } catch (Exception e){
          badRequest(res, "invalid skier ID");
          return;
        }

        LiftRideDao liftRideDao = new LiftRideDao();
        int verticals;
        try {
          verticals = liftRideDao.getVerticalsBySkierId(skierId);
        } catch (SQLException e) {
          badRequest(res, "no such skier in DB.");
          e.printStackTrace();
          return;
        }
        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write(gson.toJson(new ResortResponse("66", verticals)));
        break;
      }

      // routing /{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}
      case 7: {
        String resortId = urlParts[0];
        String seasonId = urlParts[2];
        String dayId = urlParts[4];
        String skierId = urlParts[6];

        int verticalsOfDay;
        LiftRideDao liftRideDao = new LiftRideDao();
        try {
          verticalsOfDay = liftRideDao.getVerticalsBySkierIdAndDay(skierId, dayId);
        } catch (SQLException e) {
          badRequest(res, "no such skier on such day in DB.");
          e.printStackTrace();
          return;
        }
        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write(Integer.toString(verticalsOfDay));
        break;
      }
      default: {
        badRequest(res, "GET method called wrongly");
      }
    }
  }

  protected void badRequest(HttpServletResponse res, String message) throws IOException {
    res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    Gson gson = new Gson();
    res.getWriter().write(gson.toJson(new Message(message)));
  }

  private boolean isUrlValid(String[] urlPath) {
    // urlPath  = "/1/seasons/2019/days/1/skiers/123"
    // urlParts = [, 1, seasons, 2019, days, 1, skiers, 123]
    if (urlPath.length == URL_ARR_LENGTH) {
      return (urlPath[3].length() == 4
              && Integer.parseInt(urlPath[5]) >= DAY_ID_MIN
              && Integer.parseInt(urlPath[5]) <= DAY_ID_MAX
              && urlPath[2].equals(SEASONS_PARAMETER)
              && urlPath[4].equals(DAYS_PARAMETER)
              && urlPath[6].equals(SKIERS_PARAMETER));
    }
    return false;
  }
}
