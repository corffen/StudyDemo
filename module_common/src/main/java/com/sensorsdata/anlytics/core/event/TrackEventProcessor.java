/*
 * Created by dengshiwei on 2022/06/14.
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

package com.sensorsdata.anlytics.core.event;

import com.sensorsdata.anlytics.core.SAContextManager;

public class TrackEventProcessor extends EventProcessor {

    public TrackEventProcessor(SAContextManager saContextManager) {
        super(saContextManager);
    }

    /**
     * track event
     *
     * @param input DataInput
     */
    @Override
    public void trackEvent(InputData input) {
        process(input);
    }
}
