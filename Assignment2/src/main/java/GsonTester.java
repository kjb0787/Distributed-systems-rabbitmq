import com.google.gson.Gson;

import model.Message;

public class GsonTester {
  static Gson gson = new Gson();
  public static void main(String... args) {
    System.out.println(gson.toJson(new Message("this is a message")));
    Message message = gson.fromJson(gson.toJson(new Message("this is a message")), Message.class);
    System.out.println(message.getMessage());
  }
}
