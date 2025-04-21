package cn.delei.mcp;

public enum DatabaseType {
    UNKNOWN(-1L),
    SQLSERVER(10L),
    ORACLE(11L),
    MYSQL(12L),
    MARIADB(13L),
    DB2(14L),
    POSTGRESQL(15L),
    SQLITE(16L),

    DM(100L),
    OCEANBASE(101L),
    OCEANBASE_ORACLE(102L),
    GAUSSDB(103L),
    TIDB(104L),
    KINGBASE(105L),
    GBASE(106L);

    public final long code;

    DatabaseType(long code) {
        this.code = code;
    }

    public static long of(DatabaseType... types) {
        long value = 0;

        for (DatabaseType type : types) {
            value |= type.code;
        }
        return value;
    }

    public static DatabaseType of(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        try {
            return valueOf(name.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }

    public static DatabaseType ofUrl(String rawUrl) {
        return DatabaseJDBCUrlContext.databaseTypeFromUrl(rawUrl);
    }
}
