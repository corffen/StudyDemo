/*
 * Created by chenru on 2020/08/31.
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

package com.sensorsdata.anlytics.util;

import android.content.Context;

import com.sensorsdata.anlytics.SALog;

public class SADisplayUtil {
    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * get string by id
     *
     * @param context Context
     * @param id id
     * @return string
     */
    public static String getStringResource(Context context, int id) {
        try {
            return context.getString(id);
        } catch (Exception e) {
            SALog.printStackTrace(e);
        }
        return "";
    }
}
