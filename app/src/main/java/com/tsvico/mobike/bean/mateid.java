package com.tsvico.mobike.bean;
/****
 *
 * id与nfc匹配
 */

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

public class mateid extends BmobObject implements Serializable {

    private String id;
    private String devces;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getDevces() {
        return devces;
    }
    public void setDevces(String devces) {
        this.devces = devces;
    }
}
