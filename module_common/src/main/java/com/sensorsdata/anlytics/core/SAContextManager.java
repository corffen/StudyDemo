/*
 * Created by dengshiwei on 2021/07/04.
 * Copyright 2015－2022 Sensors Data Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sensorsdata.anlytics.core;

import android.content.Context;
import android.text.TextUtils;

import com.sensorsdata.anlytics.AnalyticsMessages;
import com.sensorsdata.anlytics.SALog;
import com.sensorsdata.anlytics.SensorsDataAPI;
import com.sensorsdata.anlytics.SensorsDataScreenOrientationDetector;
import com.sensorsdata.anlytics.core.event.EventProcessor;
import com.sensorsdata.anlytics.core.event.InputData;
import com.sensorsdata.anlytics.core.event.TrackEventProcessor;
import com.sensorsdata.anlytics.core.mediator.SAModuleManager;
import com.sensorsdata.anlytics.data.adapter.DbAdapter;
import com.sensorsdata.anlytics.data.persistent.PersistentFirstDay;
import com.sensorsdata.anlytics.data.persistent.PersistentLoader;
import com.sensorsdata.anlytics.internal.beans.InternalConfigOptions;
import com.sensorsdata.anlytics.listener.SAEventListener;
import com.sensorsdata.anlytics.plugin.encrypt.SAStoreManager;
import com.sensorsdata.anlytics.plugin.property.PropertyPluginManager;
import com.sensorsdata.anlytics.remote.BaseSensorsDataSDKRemoteManager;
import com.sensorsdata.anlytics.remote.SensorsDataRemoteManager;
import com.sensorsdata.anlytics.useridentity.UserIdentityAPI;
import com.sensorsdata.anlytics.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class SAContextManager {
    private Context mContext;
    private List<SAEventListener> mEventListenerList;
    private SensorsDataScreenOrientationDetector mOrientationDetector;
    private SensorsDataAPI mSensorsDataAPI;
    private PersistentFirstDay mFirstDay;
    private PropertyPluginManager mPluginManager;
    private EventProcessor mTrackEventProcessor;
    private InternalConfigOptions mInternalConfigs;
    private AnalyticsMessages mMessages;
    /* 远程配置管理 */
    BaseSensorsDataSDKRemoteManager mRemoteManager;
    UserIdentityAPI mUserIdentityAPI;

    public SAContextManager() {
    }

    public SAContextManager(SensorsDataAPI sensorsDataAPI, InternalConfigOptions internalConfigs) {
        try {
            this.mSensorsDataAPI = sensorsDataAPI;
            mInternalConfigs = internalConfigs;
            this.mContext = internalConfigs.context.getApplicationContext();
            DbAdapter.getInstance(this);
            mMessages = AnalyticsMessages.getInstance(mContext, sensorsDataAPI, mInternalConfigs);
            mTrackEventProcessor = new TrackEventProcessor(this);
            this.mFirstDay = PersistentLoader.getInstance().getFirstDayPst();
            // 1. init plugin manager for advert module
            mPluginManager = new PropertyPluginManager(sensorsDataAPI, this);
            // 2. init store manager
            SAStoreManager.getInstance().registerPlugins(mInternalConfigs.saConfigOptions.getStorePlugins(), mContext);
            SAStoreManager.getInstance().upgrade();
            // 3. execute delay task
            executeDelayTask();
            // 4. init module service for encrypt sp
            SAModuleManager.getInstance().installService(this);
            // 5. init RemoteManager, it use Identity、track、SAStoreManager
            mRemoteManager = new SensorsDataRemoteManager(sensorsDataAPI, this);
            mRemoteManager.applySDKConfigFromCache();
            // 5. reset context because of delay init
            internalConfigs.context = mContext;
        } catch (Exception e) {
            SALog.printStackTrace(e);
        }
    }

    /**
     * execute delay task，before init module and track event 
     */
    private void executeDelayTask() {
        SACoreHelper.getInstance().trackQueueEvent(new Runnable() {
            @Override
            public void run() {
                final String anonymousId = mInternalConfigs.saConfigOptions.getAnonymousId();
                if (!TextUtils.isEmpty(anonymousId)) {
                    getUserIdentityAPI().identify(anonymousId);
                }
            }
        });
    }

    /**
     * 获取 SDK 事件监听回调
     *
     * @return 事件监听回调
     */
    public List<SAEventListener> getEventListenerList() {
        return mEventListenerList;
    }

    /**
     * SDK 事件回调监听，目前用于弹窗业务
     *
     * @param eventListener 事件监听
     */
    public void addEventListener(SAEventListener eventListener) {
        try {
            if (this.mEventListenerList == null) {
                this.mEventListenerList = new ArrayList<>();
            }
            this.mEventListenerList.add(eventListener);
        } catch (Exception ex) {
            SALog.printStackTrace(ex);
        }
    }

    /**
     * 移除 SDK 事件回调监听
     *
     * @param eventListener 事件监听
     */
    public void removeEventListener(SAEventListener eventListener) {
        try {
            if (mEventListenerList != null && mEventListenerList.contains(eventListener)) {
                this.mEventListenerList.remove(eventListener);
            }
        } catch (Exception ex) {
            SALog.printStackTrace(ex);
        }
    }

    public BaseSensorsDataSDKRemoteManager getRemoteManager() {
        return mRemoteManager;
    }

    public void setRemoteManager(BaseSensorsDataSDKRemoteManager mRemoteManager) {
        this.mRemoteManager = mRemoteManager;
    }

    public synchronized UserIdentityAPI getUserIdentityAPI() {
        if (mUserIdentityAPI == null) {
            mUserIdentityAPI = new UserIdentityAPI(this);
        }
        return mUserIdentityAPI;
    }

    public SensorsDataAPI getSensorsDataAPI() {
        return mSensorsDataAPI;
    }

    public boolean isFirstDay(long eventTime) {
        String firstDay = mFirstDay.get();
        if (firstDay == null) {
            mFirstDay.commit(TimeUtils.formatTime(System.currentTimeMillis(), TimeUtils.YYYY_MM_DD));
            return true;
        }
        try {
            String current = TimeUtils.formatTime(eventTime, TimeUtils.YYYY_MM_DD);
            return firstDay.equals(current);
        } catch (Exception e) {
            SALog.printStackTrace(e);
        }
        return true;
    }

    public PropertyPluginManager getPluginManager() {
        return mPluginManager;
    }

    public void trackEvent(InputData inputData) {
        try {
            mTrackEventProcessor.trackEvent(inputData);
        } catch (Exception e) {
            SALog.printStackTrace(e);
        }
    }

    public Context getContext() {
        return mContext;
    }

    public InternalConfigOptions getInternalConfigs() {
        return mInternalConfigs;
    }

    public AnalyticsMessages getAnalyticsMessages() {
        return mMessages;
    }

    public SensorsDataScreenOrientationDetector getOrientationDetector() {
        return mOrientationDetector;
    }

    public void setOrientationDetector(SensorsDataScreenOrientationDetector mOrientationDetector) {
        this.mOrientationDetector = mOrientationDetector;
    }
}
