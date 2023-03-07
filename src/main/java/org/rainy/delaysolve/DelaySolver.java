package org.rainy.delaysolve;

import java.sql.SQLException;

/**
 * @author wt1734
 * create at 2022/8/24 0024 10:31
 */
public class DelaySolver {

    private final MysqlClient master;
    private final MysqlClient[] slaves;

    public DelaySolver(MysqlClient master, MysqlClient[] slaves) {
        this.master = master;
        this.slaves = slaves;
    }

    /**
     * 判断主备是否存在延迟
     * @return
     */
    public MysqlClient checkDelay() {
        try {
            for (MysqlClient slave : slaves) {
                final int[] behindMaster = new int[1];
                slave.query("show slave status", rs -> {
                    rs.next();
                    behindMaster[0] = rs.getInt("Seconds_Behind_Master");
                });
                
                // 每个事务的binlog都会记录生成时间
                // second_behind_master表示从库收到最新的binlog与系统当前时间的差值
                if (behindMaster[0] == 0) {
                    return slave;
                }
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return master;
    }

    /**
     * 等待主库位点
     * 适用于对于更新后立刻获取数据的场景
     * @return
     */
    public MysqlClient waitingLoci() {
        try {
            final String[] binlogFile = new String[1];
            final int[] binlogPosition = new int[1];
            master.query("show master status", rs -> {
                rs.next();
                binlogFile[0] = rs.getString("File");
                binlogPosition[0] = rs.getInt("Position");
            });

            for (MysqlClient slave : slaves) {
                final int[] pos = new int[1];
                
                // master_pos_wait(file, pos [,timeout]) 这个命令会返回一个数字，表示从参数中指定的binlog位置与执行了多少事务，
                // 如果没有执行到指定的binlog位置，就返回0，超时返回-1
                String sql = String.format("select master_pos_wait('%s', %d, %s) pos", binlogFile[0], binlogPosition[0], 0.1);
                slave.query(sql, rs -> {
                    rs.next();
                    pos[0] = rs.getInt("pos");
                });
                
                // 如果返回值大于0，说明从库已经执行过这个位置的binlog了
                if (pos[0] > 0) {
                    return slave;
                }
            }
            
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return master;
    }

    /**
     * 等GTID
     * 适用于对于更新后立刻获取数据的场景
     * @return
     */
    public MysqlClient waitingGtid() {
        try {
            final String[] gtid = new String[1];
            master.query("show master status", rs -> {
                rs.next();
                gtid[0] = rs.getString("Executed_Gtid_Set");
            });
            
            if (gtid[0].isEmpty()) {
                // 未开启GTID
                return master;
            }
            
            for (MysqlClient slave : slaves) {
                final int[] res = new int[1];
                
                // wait_for_executed_gtid_set(gtid[,timeout]) 这个命令的逻辑是等参数指定的gtid对应的事务执行完之后返回0，超时返回1
                String sql = String.format("select wait_for_executed_gtid_set('%s', %s) gtid", gtid[0], 0.1);
                slave.query(sql, rs -> {
                    rs.next();
                    res[0] = rs.getInt("gtid");
                });
                
                if (res[0] == 0) {
                    return slave;
                }
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return master;
    }
    
}
