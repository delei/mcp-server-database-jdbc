package cn.delei.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * MCP Server Database Service
 *
 * @author deleiguo
 */
@Service
@EnableConfigurationProperties(MCPJDBCProperties.class)
@Slf4j
public class MCPServerDatabaseService {

    private final ObjectMapper objectMapper;
    private final MCPJDBCProperties properties;

    public MCPServerDatabaseService(ObjectMapper objectMapper, MCPJDBCProperties properties) {
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    @Tool(description = "获取数据库信息")
    public String databaseInfo() {
        try (Connection connection = this.getConnection()) {
            return this.objectMapper.writeValueAsString(DatabaseMetaUtil.databaseInfo(connection));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MCPException("failed get database info: " + e.getMessage(), e);
        }
    }

    @Tool(description = "获取表信息")
    public String listTables(@ToolParam(description = "库名", required = false) String schema) {
        try (Connection connection = this.getConnection()) {
            return this.objectMapper.writeValueAsString(DatabaseMetaUtil.tableList(connection, schema, null));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MCPException("failed get describe table: " + e.getMessage(), e);
        }
    }

    @Tool(description = "根据表名获取字段信息")
    public String listColumns(@ToolParam(description = "库名", required = false) String schema,
                              @ToolParam(description = "数据库表名", required = true) String table) {
        try (Connection connection = this.getConnection()) {
            return this.objectMapper.writeValueAsString(DatabaseMetaUtil.tableColumnsList(connection, schema, table));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MCPException("failed get describe table column: " + e.getMessage(), e);
        }
    }

    @Tool(description = "执行 SELECT 查询语句")
    public String readQuery(@ToolParam(description = "查询语句") String querySql) {
        try (Connection connection = this.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(querySql);
             ResultSet resultSet = preparedStatement.executeQuery(querySql)) {
            ResultSetMetaData rsMetaData = resultSet.getMetaData();
            List<LinkedHashMap> resultData = new ArrayList<>();
            while (resultSet.next()) {
                LinkedHashMap<String, Object> rowData = new LinkedHashMap<>();
                for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {
                    String columnName = rsMetaData.getColumnName(i);
                    rowData.put(columnName, resultSet.getObject(columnName));
                }
                resultData.add(rowData);
            }
            return this.objectMapper.writeValueAsString(resultData);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MCPException("Query execution failed: " + e.getMessage(), e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.properties.url(), this.properties.username(), this.properties.password());
    }
}
