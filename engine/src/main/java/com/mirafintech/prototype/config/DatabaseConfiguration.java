package com.mirafintech.prototype.config;

import lombok.Getter;

import java.util.StringJoiner;


@Getter
public class DatabaseConfiguration {

  private final String endpoint = "localhost";
  private final int port = 5432;
  private final String databaseName = "mirafintech";
  private final String userName = "mirafintech";
  private final String password = "mirafintech";

  public DatabaseConfiguration() {
  }

  public String getJDBCConnection() {
    return String.format("jdbc:postgresql://%s:%d/%s?user=%s&password=%s", this.endpoint, this.port, this.databaseName, this.userName, this.password);
  }

  public String getDatabaseURL() {
    return String.format("jdbc:postgresql://%s:%d/%s", this.endpoint, this.port, databaseName);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", DatabaseConfiguration.class.getSimpleName() + "[", "]")
            .add("endpoint='" + endpoint + "'")
            .add("port=" + port)
            .add("databaseName='" + databaseName + "'")
            .add("userName='" + userName + "'")
            .toString();
  }
}
