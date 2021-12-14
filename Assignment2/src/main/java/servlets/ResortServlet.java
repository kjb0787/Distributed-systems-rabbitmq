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
import model.Resort;

@WebServlet(name = "ResortServlet", value = "/resorts/*")
public class ResortServlet extends HttpServlet {

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
    if (urlParts.length != 6) {
      badRequest(res, "wrong url length");
      return;
    }

    String dayId = urlParts[4];
    Resort resort;
    LiftRideDao liftRideDao = new LiftRideDao();
    try {
      resort = liftRideDao.getResort(dayId);
    } catch (SQLException e) {
      badRequest(res, "no such day in DB.");
      e.printStackTrace();
      return;
    }
    res.setStatus(HttpServletResponse.SC_OK);
    Gson gson = new Gson();
    res.getWriter().write(gson.toJson(resort));
  }

  protected void badRequest(HttpServletResponse res, String message) throws IOException {
    res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    Gson gson = new Gson();
    res.getWriter().write(gson.toJson(new Message(message)));
  }
}
