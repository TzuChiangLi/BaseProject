package com.ftrend.zgp.presenter;

import android.content.Context;

import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.model.Dep;
import com.ftrend.zgp.utils.log.LogUtil;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

/**
 * 登录功能P层
 *
 * @author liziqiang@ftrend.cn
 */
public class LoginPresenter implements Contract.LoginPresenter {
    private Contract.LoginView mView;


    private LoginPresenter(Contract.LoginView mView) {
        this.mView = mView;
    }

    public static LoginPresenter createPresenter(Contract.LoginView mView) {
        return new LoginPresenter(mView);
    }


    @Override
    public void initDepData(Context context) {
        List<Dep> depList = SQLite.select().from(Dep.class).queryList();
        mView.setDepData(depList);
//        Cursor cursor = DatabaseManger.getInstance(context).query("Dep", new String[]{"*"}, null, null, null, null, null, null);
//        if (cursor != null) {
//            if (cursor.moveToFirst()) {
//                do {
//                    Dep dep = new Dep();
//                    dep.setDepName(cursor.getString(cursor.getColumnIndex("DepName")));
//                    dep.setDepCode(cursor.getString(cursor.getColumnIndex("DepCode")));
//                    depList.add(dep);
//                } while (cursor.moveToNext());
//            }
//        }
//        if (cursor != null) {
//            cursor.close();
//        }


    }

    @Override
    public void initUserData(String depCode) {

    }


    @Override
    public void checkUserInfo() {

    }
}
