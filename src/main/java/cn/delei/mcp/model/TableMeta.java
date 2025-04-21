package cn.delei.mcp.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TableMeta {
    /**
     * 表名
     */
    private String name;
    /**
     * 类型
     */
    private String type;
    /**
     * 注释
     */
    private String comment;
}
