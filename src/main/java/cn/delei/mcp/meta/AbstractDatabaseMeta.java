package cn.delei.mcp.meta;

import cn.delei.mcp.DatabaseJDBCUrlContext;
import cn.delei.mcp.DatabaseType;
import cn.delei.mcp.common.MCPException;
import cn.delei.mcp.model.TableColumnMeta;
import cn.delei.mcp.model.TableMeta;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class AbstractDatabaseMeta implements IDatabaseMeta {

    @Override
    public DatabaseType databaseType(Connection connection) {
        if (connection == null) {
            return DatabaseType.UNKNOWN;
        }
        try {
            return DatabaseJDBCUrlContext.databaseTypeFromUrl(this.getMetaData(connection).getURL());
        } catch (SQLException e) {
            throw new MCPException(e);
        }
    }

    /**
     * 获取表，默认为 TABLE 类型
     *
     * @param connection 连接信息
     * @return TABLE信息集合
     */
    @Override
    public List<TableMeta> tableList(Connection connection) {
        try {
            return this.tableTypeList(connection, this.getCatalog(connection), this.getSchema(connection), new String[]{"TABLE"});
        } catch (SQLException e) {
            throw new MCPException(e);
        }
    }

    /**
     * 指定获取TABLE类型信息
     *
     * @param connection 连接信息
     * @return 表信息集合
     */
    @Override
    public List<TableMeta> readableTableList(Connection connection) {
        try {
            String[] tableTypes = new String[]{"TABLE", "VIEW", "ALIAS", "SYNONYM"};
            return this.tableTypeList(connection, this.getCatalog(connection), this.getSchema(connection), tableTypes);
        } catch (SQLException e) {
            throw new MCPException(e);
        }
    }

    /**
     * 获取表字段信息
     *
     * @param connection 连接信息
     * @param tableName  表
     * @return 字段信息集合
     */
    @Override
    public List<TableColumnMeta> tableColumnsList(Connection connection, String tableName) {
        return this.tableColumnsList(connection, null, null, tableName, null);
    }

    protected String getCatalog(Connection connection) throws SQLException {
        try {
            return connection.getCatalog();
        } catch (Exception e) {
            return null;
        }
    }

    protected String getSchema(Connection connection) throws SQLException {
        try {
            return connection.getSchema();
        } catch (Exception e) {
            return null;
        }
    }

    protected DatabaseMetaData getMetaData(Connection connection) {
        try {
            return connection.getMetaData();
        } catch (Exception e) {
            throw new MCPException(e);
        }
    }

    protected List<TableMeta> tableTypeList(Connection connection, String catalog, String schemaPattern, String[] types) {
        try (ResultSet rs = this.getMetaData(connection).getTables(catalog, schemaPattern, "%", types)) {
            if (rs == null) {
                return Collections.emptyList();
            }
            List<TableMeta> tableList = new ArrayList<>();
            while (rs.next()) {
                tableList.add(TableMeta.builder()
                        .name(rs.getString("TABLE_NAME"))
                        .type(rs.getString("TABLE_TYPE"))
                        .comment(rs.getString("REMARKS"))
                        .build());
            }
            return tableList;
        } catch (Exception e) {
            throw new MCPException(e);
        }
    }

    protected List<TableColumnMeta> tableColumnsList(Connection connection, String catalog, String schemaPattern,
                                                     String tableNamePattern, String columnNamePattern) {
        try (ResultSet rs = this.getMetaData(connection).getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern)) {
            if (rs == null) {
                return Collections.emptyList();
            }
            List<TableColumnMeta> tableColumnList = new ArrayList<>();
            while (rs.next()) {
                tableColumnList.add(TableColumnMeta.builder()
                        .name(rs.getString("COLUMN_NAME"))
                        .dataType(rs.getString("DATA_TYPE"))
                        .typeName(rs.getString("TYPE_NAME"))
                        .columnSize(rs.getString("COLUMN_SIZE"))
                        .decimalDigits(rs.getString("DECIMAL_DIGITS"))
                        .nullable(rs.getString("NULLABLE"))
                        .comment(rs.getString("REMARKS"))
                        .defaultValue(rs.getString("COLUMN_DEF"))
                        .build());
            }
            return tableColumnList;
        } catch (Exception e) {
            throw new MCPException(e);
        }
    }
}
