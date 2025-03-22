package cn.delei.mcp;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = MCPJDBCProperties.PREFIX)
public record MCPJDBCProperties(String url, String username, String password, String driverClassName) {
    static final String PREFIX = "mcp.jdbc";
}
