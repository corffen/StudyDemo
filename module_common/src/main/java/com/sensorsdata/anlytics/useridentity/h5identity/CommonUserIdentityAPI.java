package com.sensorsdata.anlytics.useridentity.h5identity;


import com.sensorsdata.anlytics.SALog;
import com.sensorsdata.anlytics.useridentity.Identities;
import com.sensorsdata.anlytics.useridentity.UserIdentityAPI;

public class CommonUserIdentityAPI extends H5UserIdentityAPI {
    UserIdentityAPI mUserIdentityAPI;

    public CommonUserIdentityAPI(UserIdentityAPI userIdentityAPI) {
        this.mUserIdentityAPI = userIdentityAPI;
    }

    @Override
    public boolean updateIdentities() {
        try {
            //合并原生部分的 Identities 属性
            mergeIdentities(mUserIdentityAPI.getIdentitiesInstance().getIdentities(Identities.State.DEFAULT));
        } catch (Exception e) {
            SALog.printStackTrace(e);
        }
        //通知其他业务
        mUserIdentityAPI.trackH5Notify(mEventObject);
        return true;
    }
}
