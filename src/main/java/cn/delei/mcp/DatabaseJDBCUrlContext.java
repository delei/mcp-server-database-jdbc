package cn.delei.mcp;

import cn.delei.mcp.dialect.OceanbaseOracleDatabaseMeta;
import cn.delei.mcp.dialect.OracleDatabaseMeta;
import cn.delei.mcp.meta.AbstractDatabaseMeta;
import cn.delei.mcp.meta.IDatabaseMeta;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class DatabaseJDBCUrlContext {

    private DatabaseJDBCUrlContext() {
    }

    private static final Multimap<DatabaseType, String> urlMultiMap;

    static {
        // 使用有序 key 实现，确保插入的 key 顺序
        urlMultiMap = LinkedListMultimap.create();
        urlMultiMap.putAll(DatabaseType.SQLSERVER,
                Arrays.asList("jdbc:microsoft:", "jdbc:log4jdbc:microsoft:"));
        urlMultiMap.putAll(DatabaseType.ORACLE,
                Arrays.asList("jdbc:oracle:", "jdbc:log4jdbc:oracle:"));
        urlMultiMap.putAll(DatabaseType.MYSQL,
                Arrays.asList("jdbc:mysql:", "jdbc:cobar:", "jdbc:log4jdbc:mysql:"));
        urlMultiMap.put(DatabaseType.MARIADB, "jdbc:mariadb:");
        urlMultiMap.put(DatabaseType.DB2, "jdbc:db2:");
        urlMultiMap.putAll(DatabaseType.POSTGRESQL,
                Arrays.asList("jdbc:postgresql:", "jdbc:log4jdbc:postgresql:"));
        urlMultiMap.put(DatabaseType.SQLITE, "jdbc:sqlite:");
        urlMultiMap.put(DatabaseType.DM, "jdbc:dm:");
        urlMultiMap.put(DatabaseType.OCEANBASE_ORACLE, "jdbc:oceanbase:oracle:");
        urlMultiMap.put(DatabaseType.OCEANBASE, "jdbc:oceanbase:");
        urlMultiMap.putAll(DatabaseType.GAUSSDB,
                Arrays.asList("jdbc:opengauss:", "jdbc:gaussdb:", "jdbc:dws:iam:"));
        urlMultiMap.put(DatabaseType.TIDB, "jdbc:tidb:");
        urlMultiMap.putAll(DatabaseType.KINGBASE,
                Arrays.asList("jdbc:kingbase:", "jdbc:kingbase8:"));
        urlMultiMap.put(DatabaseType.GBASE, "jdbc:gbase:");
    }

    public static void register(DatabaseType databaseType, String... rawUrlPrefix) {
        urlMultiMap.putAll(databaseType, Arrays.asList(rawUrlPrefix));
    }

    public static DatabaseType databaseTypeFromUrl(String rawUrl) {
        if (rawUrl == null) {
            return null;
        }
        Optional<Map.Entry<DatabaseType, String>> optional = urlMultiMap.entries().stream()
                .filter(entry -> rawUrl.startsWith(entry.getValue()))
                .findFirst();
        return optional.map(Map.Entry::getKey).orElse(DatabaseType.UNKNOWN);
    }

    public static IDatabaseMeta databaseMetaHandler(DatabaseType databaseType) {
        if (databaseType == null) {
            return new AbstractDatabaseMeta();
        }
        switch (databaseType) {
            case ORACLE:
                return new OracleDatabaseMeta();
            case OCEANBASE_ORACLE:
                return new OceanbaseOracleDatabaseMeta();
            default:
                return new AbstractDatabaseMeta();
        }
    }
}
