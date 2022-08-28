package org.rainy.delaysolve;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author wt1734
 * create at 2022/8/24 0024 10:50
 */
public class ConfigReader {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public MysqlConfig read() {
        try (InputStream in = this.getClass().getResourceAsStream("/mysql-config.json")) {
            return objectMapper.readValue(in, MysqlConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
}
