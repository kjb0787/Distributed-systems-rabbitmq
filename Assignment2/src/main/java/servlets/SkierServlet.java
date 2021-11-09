package servlets;

import com.google.gson.Gson;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

import java.io.IOException;

import model.LiftRide;
import model.Message;
import model.PostBody;
import rabbitmqUtils.Sender;

@WebServlet(name = "SkierServlet", value = "/skiers/*")
public class SkierServlet extends HttpServlet {
  private final int URL_ARR_LENGTH = 8;
  private final int DAY_ID_MIN = 1;
  private final int DAY_ID_MAX = 366;
  private final String SEASONS_PARAMETER = "seasons";
  private final String DAYS_PARAMETER = "days";
  private final String SKIERS_PARAMETER = "skiers";
  private final Gson gson  = new Gson();

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    String urlPath = request.getPathInfo();

    // check we have a URL!
    if (urlPath == null || urlPath.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write("missing parameters");
      return;
    }
    String[] urlParts = urlPath.split("/");
    // and now validate url path and return the response status code
    // (and maybe also some value if input is valid)
    if (!isUrlValid(urlParts)) {
      Message message = new Message("request not valid");
      response.getWriter().write(gson.toJson(message));
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    } else {
      PostBody postBody = gson.fromJson(request.getReader(), PostBody.class);
      Sender sender = new Sender();
      LiftRide liftRide = new LiftRide(Integer.parseInt(urlParts[1]),
              Integer.parseInt(urlParts[3]),
              Integer.parseInt(urlParts[5]),
              Integer.parseInt(urlParts[7]),
              postBody.getTime(), postBody.getLiftID());
      try {
        sender.send(gson.toJson(liftRide, LiftRide.class));
      } catch (Exception e) {
        e.printStackTrace();
      }
      // response.getWriter().write(gson.toJson(liftRide));
      response.setStatus(HttpServletResponse.SC_CREATED);
    }
  }

  protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    res.setContentType("text/plain");
    String urlPath = req.getPathInfo();
    // check we have a URL!
    if (urlPath == null || urlPath.isEmpty()) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      res.getWriter().write("missing parameters");
      return;
    }

    String[] urlParts = urlPath.split("/");
    // and now validate url path and return the response status code
    // (and maybe also some value if input is valid)

    if (!isUrlValid(urlParts)) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } else {
      res.setStatus(HttpServletResponse.SC_OK);
      // do any sophisticated processing with urlParts which contains all the url params
      // TODO: process url params in `urlParts`
      res.getWriter().write("It works!");
    }
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
