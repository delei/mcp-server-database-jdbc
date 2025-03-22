package cn.delei;

import cn.delei.mcp.MCPServerDatabaseService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * MCP Server
 *
 * @author deleiguo
 */
@SpringBootApplication
public class MCPServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(MCPServerApplication.class, args);
    }

    @Bean
    public ToolCallbackProvider databaseTools(MCPServerDatabaseService databaseService) {
        return MethodToolCallbackProvider.builder().toolObjects(databaseService).build();
    }
}
