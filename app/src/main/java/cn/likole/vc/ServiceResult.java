package cn.likole.vc;

import java.util.List;

/**
 * Created by likole on 8/22/18.
 */

public class ServiceResult {
    private int code;
    private String message;
    private String source;
    private String target;
    private List<String> list;
    private String ckpt;

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public String getCkpt() {
        return ckpt;
    }

    public void setCkpt(String ckpt) {
        this.ckpt = ckpt;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
