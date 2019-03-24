package com.tsvico.mobike.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * desc:骑行数据
 */

public class RideSummary extends BmobObject implements Serializable {
    private MyUser mMyUser;
    private String ride;
    private String save;
    private String kaluli;

    public MyUser getMyUser() {
        return mMyUser;
    }

    public void setMyUser(MyUser myUser) {
        mMyUser = myUser;
    }

    public String getRide() {
        return ride;
    }

    public void setRide(String ride) {
        this.ride = ride;
    }

    public String getSave() {
        return save;
    }

    public void setSave(String save) {
        this.save = save;
    }

    public String getKaluli() {
        return kaluli;
    }

    public void setKaluli(String kaluli) {
        this.kaluli = kaluli;
    }
}
