package cn.delei.ai.mcp;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service
public class MCPServerDatabaseService {
    @Tool(description = "获取数据库信息")
    public String databaseInfo() {
        return null;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, jdbcUser.orElse(null), jdbcPassword.orElse(null));
    }
}
