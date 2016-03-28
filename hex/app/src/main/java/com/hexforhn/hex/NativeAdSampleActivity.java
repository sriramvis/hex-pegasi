package com.cmcm.ads;

import android.app.Activity;
import android.os.Bundle;
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

public class NativeAdSampleActivity extends Activity implements OnClickListener {

    /* 广告 Native大卡样式 */
    private NativeAdManager nativeAdManager;
    private FrameLayout nativeAdContainer;
    /* 广告 Native大卡样式 */
    private Button loadAdButton;
    private String mAdPosid = "1096101";

    //private OrionNativeAdview mAdView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //loadAdButton = (Button) findViewById(R.id.btn_load);
        loadAdButton.setOnClickListener(this);
        initNativeAd();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //case R.id.btn_load:
                //requestNativeAd(v.getId());
                //break;
            default:
                break;
        }
    }


    private void requestNativeAd(int id) {
        //if (id == R.id.btn_load) {
            //nativeAdManager.loadAd();
        //}
    }

    private void initNativeAd() {
        nativeAdManager = new NativeAdManager(this, mAdPosid);
        nativeAdManager.setNativeAdListener(new INativeAdLoaderListener() {
            @Override
            public void adLoaded() {
                INativeAd ad = nativeAdManager.getAd();
                Toast.makeText(NativeAdSampleActivity.this, "adLoaded", Toast.LENGTH_SHORT).show();
//                if (mAdView != null) {
//                    // 把旧的广告view从广告容器中移除
//                    nativeAdContainer.removeView(mAdView);
//                }
                if (ad == null) {
                    Toast.makeText(NativeAdSampleActivity.this,
                            "no native ad loaded!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //通过广告数据渲染广告View
               // mAdView = OrionNativeAdview.createAdView(getApplicationContext(), ad);
                //nativeAdContainer.addView(mAdView);
            }

            @Override
            public void adFailedToLoad(int i) {
                Toast.makeText(NativeAdSampleActivity.this, "Ad failed to load errorCode:" + i,
                        Toast.LENGTH_SHORT).show();
            }


            @Override
            public void adClicked(INativeAd ad) {
                Toast.makeText(NativeAdSampleActivity.this, "adClicked",
                        Toast.LENGTH_SHORT).show();
            }

        });
    }



}