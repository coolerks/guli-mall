package top.integer.common.exception;

/**
 *
 */
public enum BizCodeEnume {
    /**
     *
     */
    UNKNOW_EXCEPTION(10000,"系统未知异常"),
    VAILD_EXCEPTION(10001,"参数格式校验失败"),
    SMS_CODE_EXCEPTION(10002,"请稍候再尝试获取验证码"),
    PRODUCT_UP_EXCEPTION(11000,"商品上架异常"),
    USER_EXIST_EXCEPTION(15000,"用户已存在"),
    LOGIN_INVALID(15003,"登陆失败，用户名或密码不正确。");

    private int code;
    private String msg;
    BizCodeEnume(int code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
