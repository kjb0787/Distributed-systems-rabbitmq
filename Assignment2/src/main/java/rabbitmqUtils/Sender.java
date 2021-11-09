package rabbitmqUtils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sender {
  private final static String QUEUE_NAME = "threadExQ";
  private final static int NUM_MESSAGES_PER_THREAD = 10;

  public void send(String messageToSend) throws Exception {
    Runnable runnable = () -> {
      try {
        // channel per thread
        ChannelPool channelPool = ChannelPool.getInstance();
        Channel channel = channelPool.borrowObject();
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        for (int i=0; i < NUM_MESSAGES_PER_THREAD; i++) {
          String message = "Here's a message " +  Integer.toString(i);
          channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
        }
        channel.close();
        System.out.println(" [All Messages  Sent '" );
      } catch (IOException | TimeoutException ex) {
        Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
      } catch (Exception e) {
        e.printStackTrace();
      }
    };
    // start threads and wait for completion
    Thread t1 = new  Thread (runnable);
    Thread t2 = new  Thread (runnable);
    t1.start();
    t2.start();
    t1.join();
    t2.join();
  }
}
