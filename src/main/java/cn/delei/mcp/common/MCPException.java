package cn.delei.mcp.common;

/**
 * 自定义异常
 *
 * @author deleiguo
 */
public class MCPException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public MCPException() {
        super();
    }

    public MCPException(String message, Throwable cause) {
        super(message, cause);
    }

    public MCPException(String message) {
        super(message);
    }

    public MCPException(Throwable cause) {
        super(cause);
    }

}
