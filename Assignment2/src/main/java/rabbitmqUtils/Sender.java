package rabbitmqUtils;

import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sender {
  private final static String QUEUE_NAME = "threadExQ";
  public static void send(String messageToSend) throws Exception {
    try {
      ChannelPool channelPool = ChannelPool.getInstance();
      Channel channel = channelPool.borrowObject();
      channel.queueDeclare(QUEUE_NAME, true, false, false, null);
      channel.basicPublish("", QUEUE_NAME, null, messageToSend.getBytes(StandardCharsets.UTF_8));
      channelPool.returnObject(channel);
    } catch (IOException | TimeoutException ex) {
      Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
