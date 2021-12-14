package dal;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnectionPoolFactory extends BasePooledObjectFactory<Connection> {

  private static final String HOST_NAME = "rds-mysql-hw3.c5ranqgqojwa.us-east-1.rds.amazonaws.com";
  private static final String PORT = "3306";
  private static final String DATABASE = "SkierApplication";
  private static final String USERNAME = "admin";
  private static final String PASSWORD = "admin123";

  @Override
  public Connection create() throws Exception {
    Connection connection;
    try {
      try {
        Class.forName("com.mysql.cj.jdbc.Driver");
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
        throw new SQLException(e);
      }
      connection = DriverManager.getConnection("jdbc:mysql://" + HOST_NAME + ":" + PORT + "/" + DATABASE, USERNAME, PASSWORD);
    } catch (SQLException e) {
      e.printStackTrace();
      throw e;
    }
    return connection;
  }

  @Override
  public PooledObject<Connection> wrap(Connection connection) {
    return new DefaultPooledObject<>(connection);
  }
}
