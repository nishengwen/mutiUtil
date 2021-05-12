package com.nsw.xiuhua.ui;

import android.text.TextUtils;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.nsw.xiuhua.BR;


public class Model2 extends BaseObservable {

    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if(TextUtils.equals(title,this.title)){
            return;
        }
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

    public String title="777777777";


}
