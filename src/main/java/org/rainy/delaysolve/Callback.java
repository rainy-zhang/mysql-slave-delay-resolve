package org.rainy.delaysolve;

import java.sql.SQLException;

/**
 * @author wt1734
 * create at 2022/8/24 0024 16:33
 */
public interface Callback<T> {
    
    void execute(T t) throws SQLException;
    
}
