package com.ftrend.zgp;

import com.ftrend.zgp.base.BaseActivity;
import com.ftrend.zgp.utils.db.DBHelper;
import com.ftrend.zgp.utils.log.LogUtil;
import com.ftrend.zgp.utils.permission.PermissionUtil;

/**
 * @author liziqiang@ftrend.cn
 */
public class MainActivity extends BaseActivity {


    @Override
    protected int getLayoutID() {
        return R.layout.main_activity;
    }

    @Override
    protected void initData() {
        DBHelper dbHelper = new DBHelper(this, "TEST.db", null, 1);
        dbHelper.getWritableDatabase();
    }

    @Override
    protected void initView() {
        PermissionUtil.checkAndRequestPermission();

    }

    @Override
    protected void initTitleBar() {
        LogUtil.d("initTitleBar");
    }


}
