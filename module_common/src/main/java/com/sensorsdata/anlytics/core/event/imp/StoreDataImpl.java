/*
 * Created by dengshiwei on 2022/06/15.
 * Copyright 2015－2021 Sensors Data Inc.
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

package com.sensorsdata.anlytics.core.event.imp;

import com.sensorsdata.anlytics.SALog;
import com.sensorsdata.anlytics.core.event.Event;
import com.sensorsdata.anlytics.core.event.EventProcessor;
import com.sensorsdata.anlytics.data.adapter.DbAdapter;
import com.sensorsdata.anlytics.exceptions.DebugModeException;

public class StoreDataImpl implements EventProcessor.IStoreData {
    private static final String TAG = "SA.StoreDataImpl";
    private final DbAdapter mDbAdapter;

    public StoreDataImpl() {
        mDbAdapter = DbAdapter.getInstance();
    }

    @Override
    public int storeData(Event event) {
        if (event == null) {
            return 0;
        }
        int ret = mDbAdapter.addJSON(event.toJSONObject());
        if (ret < 0) {
            String error = "Failed to enqueue the event: " + event.toJSONObject();
            if (SALog.isDebug()) {
                throw new DebugModeException(error);
            } else {
                SALog.i(TAG, error);
            }
        }
        return ret;
    }
}
