/**
 * Copyright 2016 JustWayward Team
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.justwayward.reader.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.justwayward.reader.R;
import com.justwayward.reader.api.BookApi;
import com.justwayward.reader.base.BaseActivity;
import com.justwayward.reader.bean.BookMixAToc;
import com.justwayward.reader.bean.ChapterRead;
import com.justwayward.reader.component.AppComponent;
import com.justwayward.reader.component.DaggerMainComponent;
import com.justwayward.reader.ui.contract.BookReadContract;
import com.justwayward.reader.ui.presenter.BookReadPresenter;
import com.justwayward.reader.utils.NetworkUtils;
import com.justwayward.reader.wifitransfer.Defaults;
import com.justwayward.reader.wifitransfer.ServerRunner;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by xiaoshu on 2016/10/9.
 */
public class WifiBookActivity extends BaseActivity {

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, WifiBookActivity.class));
    }

    @Bind(R.id.mTvWifiName)
    TextView mTvWifiName;
    @Bind(R.id.mTvWifiIp)
    TextView mTvWifiIp;

    @Bind(R.id.tvRetry)
    TextView tvRetry;

    @Inject
    BookApi mBookApi;

    @Override
    public int getLayoutId() {
        return R.layout.activity_wifi_book;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMainComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void initToolBar() {
        mCommonToolbar.setTitle("WiFi book");
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
    }

    @Override
    public void initDatas() {
        String wifiname = NetworkUtils.getConnectWifiSsid(mContext);
        if (!TextUtils.isEmpty(wifiname)) {
            mTvWifiName.setText(wifiname.replace("\"", ""));
        } else {
            mTvWifiName.setText("Unknow");
        }

        String wifiIp = NetworkUtils.getConnectWifiIp(mContext);
        if (!TextUtils.isEmpty(wifiIp)) {
            tvRetry.setVisibility(View.GONE);
            mTvWifiIp.setText("http://" + NetworkUtils.getConnectWifiIp(mContext) + ":" + Defaults.getPort());
            // 启动wifi传书服务器
            ServerRunner.startServer(mBookApi);
        } else {
            mTvWifiIp.setText("Please turn on Wifi and try again");
            tvRetry.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void configViews() {

    }

    @OnClick(R.id.tvRetry)
    public void retry() {
        initDatas();
    }

    @Override
    public void onBackPressed() {
        if (ServerRunner.serverIsRunning) {
            new AlertDialog.Builder(this)
                    .setTitle("prompt")
                    .setMessage("Are you sure you want to close? Wifi book transfer will be interrupted!")
                    .setPositiveButton("determine", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ServerRunner.stopServer();
    }


}
