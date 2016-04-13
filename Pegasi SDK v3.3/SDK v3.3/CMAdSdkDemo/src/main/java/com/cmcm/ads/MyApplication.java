package com.cmcm.ads;

import android.app.Application;
import android.view.View;

import com.cmcm.adsdk.CMAdManager;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
        super.onCreate();
        if (BuildConfig.IS_CN_VERSION) {
            //初始化聚合sdk
            //第一个参数：Context
            //第二个参数： mid(产品接入广告的唯一标识，posid的前四位)
            //第三个参数：产品的渠道ID,没有可以传入“”
            CMAdManager.applicationInit(this, "1096", "");
        } else {
            //海外版本 mid = 1094
            CMAdManager.applicationInit(this, "1094", "");
        }
        //开启Debug模式，默认不开启不会打印log
        CMAdManager.enableLog();
    }

}
