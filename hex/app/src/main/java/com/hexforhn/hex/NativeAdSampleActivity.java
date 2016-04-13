package com.hexforhn.hex;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

//import com.cmcm.ads.ui.OrionNativeAdview;
import com.cmcm.adsdk.nativead.NativeAdManager;
import com.cmcm.baseapi.ads.INativeAd;
import com.cmcm.baseapi.ads.INativeAdLoaderListener;
import com.hexforhn.hex.BuildConfig;
import com.hexforhn.hex.R;
import com.hexforhn.hex.ui.OrionNativeAdview;

public class NativeAdSampleActivity extends Activity {

    /* 广告 Native大卡样式 */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.front_page_list_item);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }








}