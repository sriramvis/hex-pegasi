package com.hexforhn.hex.activity.frontpage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cmcm.adsdk.CMAdManager;
import com.cmcm.adsdk.nativead.NativeAdManager;
import com.cmcm.adsdk.nativead.NativeAdManagerEx;
import com.cmcm.baseapi.ads.INativeAd;
import com.cmcm.baseapi.ads.INativeAdLoaderListener;
import com.hexforhn.hex.HexApplication;
import com.hexforhn.hex.NativeAdSampleActivity;
import com.hexforhn.hex.R;
import com.hexforhn.hex.activity.story.StoryActivity;
import com.hexforhn.hex.adapter.FrontPageListAdapter;
import com.hexforhn.hex.asynctask.FrontPageItemsHandler;
import com.hexforhn.hex.asynctask.GetFrontPageItems;
import com.hexforhn.hex.decoration.DividerItemDecoration;
import com.hexforhn.hex.listener.ClickListener;
import com.hexforhn.hex.model.Item;
import com.hexforhn.hex.model.Story;
import com.hexforhn.hex.ui.OrionNativeAdview;
import com.hexforhn.hex.util.view.RefreshHandler;
import com.hexforhn.hex.util.view.SwipeRefreshManager;
import com.hexforhn.hex.viewmodel.ItemListItemViewModel;
import com.hexforhn.hex.viewmodel.factory.ItemListItemFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FrontPageActivity extends AppCompatActivity implements FrontPageItemsHandler,
        FrontPageStateHandler, ClickListener, RefreshHandler {
    Map<Integer, String> mActionMap = new HashMap<>();
    private RecyclerView mRecyclerView;
    private List<? extends Item> mItems = new ArrayList<>();
    private SwipeRefreshManager mSwipeRefreshManager;
    private final static String STORY_TITLE_INTENT_EXTRA_NAME = "storyTitle";
    private final static String STORY_ID_INTENT_EXTRA_NAME = "storyId";
    private GetFrontPageItems mGetFrontPageItems;
    private FrontPageState mState;
    private  Button loadAdButton;


    private NativeAdManager nativeAdManager;
    private FrameLayout nativeAdContainer;
    /* 广告 Native大卡样式 */
    //private Button loadAdButton;
    private String mAdPosid = "1398100";

    private OrionNativeAdview mAdView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);
        setupToolbar();
        setupRecyclerView();
        setupItemsUnavailableView();
        setupRefreshLayout();
        setupState();
//        CMAdManager.applicationInit(this, "1398100", "");
        NativeAdManagerEx nativeAdManagerEx = new NativeAdManagerEx(this, "1398100");
        //mActionMap.put(R.id.btn_native, "NativeAdSampleActivity");
        //loadAdButton = (Button) findViewById(R.id.btn_native);
        iClicked();
        nativeAdContainer = (FrameLayout) findViewById(R.id.big_ad_container);
        initNativeAd();
    }

    @Override
    public void onEnterLoading() {
        fetchFrontPageItems();
        mSwipeRefreshManager.start();
    }

    @Override
    public void onEnterLoaded() {
        displayItems(mItems);
        hideContentUnavailable();
        showItems();
        mSwipeRefreshManager.stop();
        mSwipeRefreshManager.enable();
    }

    @Override
    public void onEnterRefresh() {
        mSwipeRefreshManager.start();
        mSwipeRefreshManager.disable();
        fetchFrontPageItems();
    }

    @Override
    public void onEnterUnavailable() {
        mSwipeRefreshManager.stop();
        if (mItems.isEmpty()) {
            hideItems();
            mSwipeRefreshManager.disable();
            showContentUnavailable();
        } else {
            mSwipeRefreshManager.enable();
        }
        showRefreshFailedSnackbar();
    }

    @Override
    public void onItemsReady(List<? extends Item> items) {
        setItems(items);
        mState.sendEvent(FrontPageState.Event.LOAD_SUCCEEDED);
    }

    @Override
    public void onItemsUnavailable() {
        mState.sendEvent(FrontPageState.Event.LOAD_FAILED);
    }

    @Override
    public void onClick(View v, int position, boolean isLongClick) {
        openStoryAtPosition(position);
    }

    @Override
    public void onRefresh() {
        mState.sendEvent(FrontPageState.Event.LOAD_REQUESTED);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) { actionBar.setTitle(R.string.frontPage); }
    }

    private void setupRefreshLayout() {
        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mSwipeRefreshManager = new SwipeRefreshManager(refreshLayout, this);
    }

    private void setupRecyclerView() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView = (RecyclerView) findViewById(R.id.items);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this));
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.Adapter mAdapter = new FrontPageListAdapter(Collections.EMPTY_LIST, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setupItemsUnavailableView() {
        TextView loadingFailed = (TextView) findViewById(R.id.loading_failed_text);
        loadingFailed.setText(R.string.error_unableToLoadFrontPage);

        Button tryAgain = (Button) findViewById(R.id.try_again);
        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mState.sendEvent(FrontPageState.Event.LOAD_REQUESTED);
            }
        });
    }

    private void setupState() {
        mState = new FrontPageState(this);
        mState.sendEvent(FrontPageState.Event.LOAD_REQUESTED);
    }

    private void displayItems(List<? extends Item> items) {
        List<ItemListItemViewModel> itemListItems = ItemListItemFactory.createItemListItems(items);
        RecyclerView.Adapter mAdapter = new FrontPageListAdapter(itemListItems, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void showItems() {
        findViewById(R.id.items).setVisibility(View.VISIBLE);
    }

    private void hideItems() {
        findViewById(R.id.items).setVisibility(View.GONE);
    }

    private void showContentUnavailable() {
        findViewById(R.id.content_unavailable).setVisibility(View.VISIBLE);
    }

    private void hideContentUnavailable() {
        findViewById(R.id.content_unavailable).setVisibility(View.GONE);
    }

    private void fetchFrontPageItems() {
        if (mGetFrontPageItems != null) {
            mGetFrontPageItems.removeHandler();
        }

        mGetFrontPageItems = new GetFrontPageItems(this, (HexApplication) this.getApplication());
        mGetFrontPageItems.execute();
    }

    private void setItems(List<? extends Item> items) {
        mItems = items;
    }

    private void openStoryAtPosition(int position) {
        Intent storyIntent = new Intent(this, StoryActivity.class);
        Story story =  (Story) mItems.get(position);
        storyIntent.putExtra(STORY_TITLE_INTENT_EXTRA_NAME, story.getTitle());
        storyIntent.putExtra(STORY_ID_INTENT_EXTRA_NAME, story.getId());

        startActivity(storyIntent);
    }

    private void showRefreshFailedSnackbar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.front_page_container),
                R.string.error_unableToLoadFrontPage, Snackbar.LENGTH_LONG);
        TextView snackbarTextView = (TextView) snackbar.getView()
                .findViewById(android.support.design.R.id.snackbar_text);
        snackbarTextView.setTextColor(Color.WHITE);

        snackbar.show();
    }


    public void iClicked() {
        Context context = getApplicationContext();
        CharSequence text = "Hello toast";
        int duration = Toast.LENGTH_SHORT;
        System.out.println("HEEEEEEEEEEEEEEEEEEEERE");
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        //loadAd ();
    }

    public void click(View v){
        if (mActionMap.containsKey(v.getId())) {
            String cls = mActionMap.get(v.getId());
            /*try {
                //Class activityClass = Class.forName("com.hexforhn.hex." + cls);


                startActivity(new Intent(this, NativeAdSampleActivity.class));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }*/

            startActivity(new Intent(this, NativeAdSampleActivity.class));

        }
    }
    private void initNativeAd() {

        Log.d("SRIRAM", "I am inside initNativeAd");

        nativeAdManager = new NativeAdManager(this, mAdPosid);
        nativeAdManager.setNativeAdListener(new INativeAdLoaderListener() {
            @Override
            public void adLoaded() {
                Log.d("SRIRAM", "I am inside adLoaded");
                INativeAd ad = nativeAdManager.getAd();
                Toast.makeText(FrontPageActivity.this, "adLoaded", Toast.LENGTH_SHORT).show();
                if (mAdView != null) {
                    // 把旧的广告view从广告容s器中移除
                    nativeAdContainer.removeView(mAdView);
                }
                if (ad == null) {
                    Toast.makeText(FrontPageActivity.this,
                            "no native ad loaded!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //通过广告数据渲染广告View
                mAdView = OrionNativeAdview.createAdView(getApplicationContext(), ad);
                nativeAdContainer.addView(mAdView);
            }

            @Override
            public void adFailedToLoad(int i) {
                Toast.makeText(FrontPageActivity.this, "Ad failed to load errorCode:" + i,
                        Toast.LENGTH_SHORT).show();
            }


            @Override
            public void adClicked(INativeAd ad) {
                Toast.makeText(FrontPageActivity.this, "adClicked",
                        Toast.LENGTH_SHORT).show();
            }

        });
        nativeAdManager.loadAd();

    }
}
