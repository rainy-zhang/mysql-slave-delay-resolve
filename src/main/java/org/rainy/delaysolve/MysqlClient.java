package org.rainy.delaysolve;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;

/**
 * @author wt1734
 * create at 2022/8/24 0024 10:39
 */
public class MysqlClient {

    private final String username;

    private final String password;

    private final String url;

    private volatile Connection connection;

    public MysqlClient(MysqlConfig config) throws SQLException {
        this(config.getHost(), config.getPort(), config.getDatabase(), config.getUsername(), config.getPassword());
    }

    public MysqlClient(String host, int port, String schema, String username, String password) throws SQLException {
        this(String.format("jdbc:mysql://%s:%d/%s", host, port, schema), username, password);
    }

    public MysqlClient(String url, String username, String password) throws SQLException {
        this.url = url;
        this.username = username;
        this.password = password;

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("mysql driver load failed!", e);
        }
        connect();
    }

    private void connect() throws SQLException {
        this.connection = DriverManager.getConnection(url, username, password);
    }

    public void query(String sql, Callback<ResultSet> callback) throws SQLException {
        connection.setAutoCommit(false);
        PreparedStatement statement = connection.prepareStatement(sql);
        try (ResultSet rs = statement.executeQuery()) {
            callback.execute(rs);
        } finally {
            connection.commit();
        }
    }


    public void execute(Callback<Statement> callback) throws SQLException {
        connection.setAutoCommit(false);
        try (Statement statement = connection.createStatement()) {
            callback.execute(statement);
        } finally {
            connection.commit();
        }
    }

    public void execute(String... commands) throws SQLException {
        this.execute(statement -> {
            for (String command : commands) {
                statement.execute(command);
            }
        });
    }


    private void close(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException throwable) {
            throwable.printStackTrace();
        }
    }


}
