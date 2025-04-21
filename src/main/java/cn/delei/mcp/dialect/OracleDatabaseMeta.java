package cn.delei.mcp.dialect;

import cn.delei.mcp.DatabaseType;
import cn.delei.mcp.meta.AbstractDatabaseMeta;
import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.OracleConnection;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

@Slf4j
public class OracleDatabaseMeta extends AbstractDatabaseMeta {
    @Override
    public DatabaseType databaseType(Connection connection) {
        return DatabaseType.ORACLE;
    }

    @Override
    public DatabaseMetaData getMetaData(Connection conn) {
        if (conn == null) {
            return null;
        }
        try {
            OracleConnection oracleConn = conn.unwrap(OracleConnection.class);
            if (oracleConn != null) {
                oracleConn.setRemarksReporting(true);
                return oracleConn.getMetaData();
            }
            return conn.getMetaData();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
