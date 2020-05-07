package io.dracula.test.dubbo.cloud.two.way.demo.dubbo.api;

import java.io.Serializable;

/**
 * @author dk
 */
public class SomeMsg implements Serializable {

    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
