package com.tsvico.mobike.bean;

import cn.bmob.v3.BmobObject;


public class Credit extends BmobObject {
    private MyUser mMyUser;
    private Integer creditNub;

    public MyUser getMyUser() {
        return mMyUser;
    }

    public void setMyUser(MyUser myUser) {
        mMyUser = myUser;
    }

    public Integer getCreditNub() {
        return creditNub;
    }

    public void setCreditNub(Integer creditNub) {
        this.creditNub = creditNub;
    }
}
