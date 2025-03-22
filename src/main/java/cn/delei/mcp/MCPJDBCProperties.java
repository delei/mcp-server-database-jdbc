package cn.delei.mcp;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = JDBCProperties.PREFIX)
public record JDBCProperties(String url, String username, String password, String driverClassName) {
    static final String PREFIX="jdbc";
}
