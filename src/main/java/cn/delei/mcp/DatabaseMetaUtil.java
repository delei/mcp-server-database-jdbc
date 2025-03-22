package cn.delei.mcp;

import cn.hutool.core.text.CharSequenceUtil;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 数据库元信息工具类
 *
 * @author deleiguo
 */
@Slf4j
public class DatabaseMetaUtil {
    private DatabaseMetaUtil() {
    }

    public static DatabaseMetaData getDatabaseMetaData(Connection connection) {
        try {
            return connection.getMetaData();
        } catch (SQLException e) {
            throw new MCPException("failed get DatabaseMetaData: " + e.getMessage(), e);
        }
    }

    /**
     * 获取数据库信息
     *
     * @param connection 连接信息
     * @return 数据库信息
     */
    public static Map<String, Object> databaseInfo(Connection connection) {
        try {
            DatabaseMetaData dbmd = getDatabaseMetaData(connection);
            return Map.of("databaseProductName", dbmd.getDatabaseProductName(),
                    "databaseMajorVersion", dbmd.getDatabaseMajorVersion(),
                    "databaseProductVersion", dbmd.getDatabaseProductVersion(),
                    "url", dbmd.getURL(),
                    "userName", dbmd.getUserName(),
                    "maxConnections", dbmd.getMaxConnections(),
                    "supportsTransactions", dbmd.supportsTransactions()
            );
        } catch (Exception e) {
            throw new MCPException(e);
        }
    }

    /**
     * 获取表，默认为 TABLE 类型
     *
     * @param connection 连接信息
     * @param catalog    目录
     * @return TABLE信息集合
     */
    public static List<Map<String, Object>> tableList(Connection connection, String catalog, String schemaPattern) {
        return tableTypeList(getDatabaseMetaData(connection), catalog, schemaPattern, new String[]{"TABLE"});
    }

    /**
     * 获取可读的 TABLE 类型
     *
     * @param connection    连接信息
     * @param catalog       目录
     * @param schemaPattern schema表达式
     * @return TABLE信息集合
     */
    public static List<Map<String, Object>> readableTableList(Connection connection, String catalog, String schemaPattern) {
        String[] tableTypes = new String[]{"TABLE", "VIEW", "ALIAS", "SYNONYM"};
        return tableTypeList(getDatabaseMetaData(connection), catalog, schemaPattern, tableTypes);
    }

    /**
     * 获取 TABLE 类型
     *
     * @param databaseMetaData 元数据对象
     * @return TABLE信息集合
     */
    public static List<Map<String, Object>> tableList(DatabaseMetaData databaseMetaData, String catalog, String schemaPattern) {
        return tableTypeList(databaseMetaData, catalog, schemaPattern, new String[]{"TABLE"});
    }

    /**
     * 指定获取TABLE类型信息
     *
     * @param conn    连接信息
     * @param catalog 目录
     * @return 表信息集合
     */
    public static List<Map<String, Object>> tableTypeList(Connection conn, String catalog, String schemaPattern, String[]
            tableTypes) {
        return tableTypeList(getDatabaseMetaData(conn), catalog, schemaPattern, tableTypes);
    }

    /**
     * 获取表信息
     *
     * @param databaseMetaData 元数据对象
     * @param catalog          目录
     * @param schemaPattern    schema表达式
     * @param types            表类型
     * @return 表信息集合
     */
    public static List<Map<String, Object>> tableTypeList(DatabaseMetaData databaseMetaData, String catalog, String
            schemaPattern, String[] types) {
        if (databaseMetaData == null) {
            throw new MCPException("DatabaseMetaData is null");
        }
        try (ResultSet rs = databaseMetaData.getTables(catalog, schemaPattern, "%", types)) {
            List<Map<String, Object>> tableList = new ArrayList<>();
            if (rs == null) {
                return tableList;
            }
            while (rs.next()) {
                tableList.add(Map.of("tableName", rs.getString("TABLE_NAME"),
                        "tableType", rs.getString("TABLE_TYPE"),
                        "tableRemark", rs.getString("REMARKS")));
            }
            return tableList;
        } catch (Exception e) {
            throw new MCPException(e);
        }
    }

    /**
     * 获取表字段信息
     *
     * @param connection 连接信息
     * @param schema     Schema名
     * @param tableName  表
     * @return 字段信息集合
     */
    public static List<Map<String, Object>> tableColumnsList(Connection connection, String schema, String tableName) {
        try {
            return tableColumnsList(getDatabaseMetaData(connection), connection.getCatalog(), schema, tableName);
        } catch (SQLException e) {
            throw new MCPException(e);
        }
    }

    /**
     * 获取表字段信息
     *
     * @param databaseMetaData 元数据对象
     * @param catalog          目录
     * @param schema           Schema名
     * @param tableName        表
     * @return 字段信息集合
     */
    public static List<Map<String, Object>> tableColumnsList(DatabaseMetaData databaseMetaData, String catalog,
                                                             String schema, String tableName) {
        if (databaseMetaData == null || CharSequenceUtil.isBlank(tableName)) {
            throw new MCPException("table is null");
        }
        try (ResultSet rs = databaseMetaData.getColumns(catalog,
                CharSequenceUtil.isBlank(schema) ? databaseMetaData.getUserName() : schema,
                tableName, null)) {
            List<Map<String, Object>> tableColumnList = new ArrayList<>();
            if (rs == null) {
                return tableColumnList;
            }
            while (rs.next()) {
                tableColumnList.add(
                        Map.of("column", rs.getString("COLUMN_NAME"),
                                "dataType", rs.getString("DATA_TYPE"),
                                "dataTypeName", rs.getString("TYPE_NAME"),
                                "columnSize", rs.getString("COLUMN_SIZE"),
                                "decimalDigits", rs.getString("DECIMAL_DIGITS"),
                                "nullable", rs.getString("NULLABLE"),
                                "remark", rs.getString("REMARKS"),
                                "defaultValue", rs.getString("COLUMN_DEF")
                        ));
            }
            return tableColumnList;
        } catch (Exception e) {
            throw new MCPException(e);
        }
    }

    /**
     * 获取PK
     *
     * @param connection 连接信息
     * @param tableName  表
     * @return PK列
     */
    public static Set<String> tablePrimaryKeys(Connection connection, String tableName) {
        return tablePrimaryKeys(getDatabaseMetaData(connection), tableName);
    }

    /**
     * 获取PK
     *
     * @param databaseMetaData 元数据对象
     * @param tableName        表
     * @return PK列
     */
    public static Set<String> tablePrimaryKeys(DatabaseMetaData databaseMetaData, String tableName) {
        if (databaseMetaData == null || CharSequenceUtil.isBlank(tableName)) {
            return Collections.emptySet();
        }
        Set<String> dataSet = new TreeSet<>();
        try (ResultSet rs = databaseMetaData.getPrimaryKeys(null, null, tableName)) {
            String columnName;
            while (rs.next()) {
                // 主键列名
                columnName = rs.getString("COLUMN_NAME");
                if (CharSequenceUtil.isNotBlank(columnName)) {
                    dataSet.add(columnName);
                }
            }
        } catch (Exception e) {
            throw new MCPException("failed to get PK: " + e.getMessage(), e);
        }
        return dataSet;
    }

}
