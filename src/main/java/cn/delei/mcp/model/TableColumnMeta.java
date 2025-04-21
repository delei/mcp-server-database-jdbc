package cn.delei.mcp.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TableColumnMeta {
    /**
     * 列名
     */
    private String name;
    /**
     * 数据类型
     */
    private String dataType;
    /**
     * 类型名称
     */
    private String typeName;
    /**
     * 大小
     */
    private String columnSize;
    /**
     * 小数位数
     */
    private String decimalDigits;
    /**
     * 是否为空
     */
    private String nullable;
    /**
     * 默认值
     */
    private String defaultValue;
    /**
     * 注释信息
     */
    private String comment;
}
