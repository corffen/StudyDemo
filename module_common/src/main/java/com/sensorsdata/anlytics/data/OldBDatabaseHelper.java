/*
 * Created by wangzhuozhou on 2015/08/01.
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

package com.sensorsdata.anlytics.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sensorsdata.anlytics.SALog;
import com.sensorsdata.anlytics.data.adapter.DbParams;


public class OldBDatabaseHelper extends SQLiteOpenHelper {
    OldBDatabaseHelper(Context context, String dbName) {
        super(context, dbName, null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    void getAllEvents(SQLiteDatabase sqLiteDatabase, SAProviderHelper.QueryEventsListener listener) {
        Cursor c = null;
        try {
            final SQLiteDatabase db = getReadableDatabase();
            c = db.rawQuery(String.format("SELECT * FROM %s ORDER BY %s", DbParams.TABLE_EVENTS, DbParams.KEY_CREATED_AT), null);
            sqLiteDatabase.beginTransaction();
            while (c.moveToNext()) {
                int dataIndex = c.getColumnIndex("data");
                int createIndex = c.getColumnIndex("created_at");
                listener.insert(c.getString(dataIndex), c.getString(createIndex));
            }
            sqLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            SALog.printStackTrace(e);
        } finally {
            sqLiteDatabase.endTransaction();
            close();
            if (c != null) {
                c.close();
            }
        }
    }
}
