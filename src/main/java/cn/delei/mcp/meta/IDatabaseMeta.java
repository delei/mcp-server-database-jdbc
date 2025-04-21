package cn.delei.mcp.meta;

import cn.delei.mcp.DatabaseJDBCUrlContext;
import cn.delei.mcp.DatabaseType;
import cn.delei.mcp.model.TableColumnMeta;
import cn.delei.mcp.model.TableMeta;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IDatabaseMeta {
    default DatabaseType databaseType(Connection connection) throws SQLException {
        return DatabaseJDBCUrlContext.databaseTypeFromUrl(connection.getMetaData().getURL());
    }

    /**
     * 获取表，默认为 TABLE 类型
     *
     * @param connection 连接信息
     * @return TABLE信息集合
     */
    default List<TableMeta> tableList(Connection connection) {
        throw new UnsupportedOperationException();
    }

    /**
     * 指定获取TABLE类型信息
     *
     * @param connection 连接信息
     * @return 表信息集合
     */
    default List<TableMeta> readableTableList(Connection connection) {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取表字段信息
     *
     * @param connection 连接信息
     * @param tableName  表
     * @return 字段信息集合
     */
    default List<TableColumnMeta> tableColumnsList(Connection connection, String tableName) {
        throw new UnsupportedOperationException();
    }
}
