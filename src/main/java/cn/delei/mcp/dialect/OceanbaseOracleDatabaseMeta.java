package cn.delei.mcp.dialect;

import cn.delei.mcp.DatabaseType;
import cn.delei.mcp.meta.AbstractDatabaseMeta;
import com.oceanbase.jdbc.OceanBaseConnection;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class OceanbaseOracleDatabaseMeta extends AbstractDatabaseMeta {
    @Override
    public DatabaseType databaseType(Connection connection) {
        return DatabaseType.OCEANBASE_ORACLE;
    }

    @Override
    public String getSchema(Connection conn) {
        if (conn == null) {
            return null;
        }
        try {
            OceanBaseConnection oracleConn = this.unwrap(conn);
            if (oracleConn != null && oracleConn.getProtocol().isOracleMode()) {
                return this.getOracleSchemaInternal(oracleConn);
            }
            return null;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public DatabaseMetaData getMetaData(Connection conn) {
        if (conn == null) {
            return null;
        }
        try {
            OceanBaseConnection oracleConn = this.unwrap(conn);
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

    private OceanBaseConnection unwrap(Connection conn) {
        try {
            return conn.unwrap(OceanBaseConnection.class);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private String getOracleSchemaInternal(OceanBaseConnection oracleConn) throws SQLException {
        String schema = null;
        Statement stmt = null;
        ResultSet rs;

        try {
            stmt = oracleConn.createStatement();
            stmt.setFetchSize(1);
            rs = stmt.executeQuery("SELECT SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA') FROM DUAL");
            if (rs.next()) {
                schema = rs.getString(1);
            }
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
        return schema;
    }
}
