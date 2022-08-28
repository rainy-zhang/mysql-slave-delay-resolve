package org.rainy.delaysolve;

import java.util.List;

/**
 * @author wt1734
 * create at 2022/8/24 0024 11:03
 */
public class MysqlConfig {
    
    private String database;
    
    private String host;
    
    private int port;
    
    private String username;
    
    private String password;
    
    private List<MysqlConfig> slaves;

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<MysqlConfig> getSlaves() {
        return slaves;
    }

    public void setSlaves(List<MysqlConfig> slaves) {
        this.slaves = slaves;
    }
}
