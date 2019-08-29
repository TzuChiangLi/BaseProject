package com.ftrend.zgp.presenter;

import android.content.Context;
import android.database.Cursor;

import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.model.Dep;
import com.ftrend.zgp.model.User;
//import com.ftrend.zgp.model.User_Table;
import com.ftrend.zgp.utils.db.DatabaseManger;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

/**
 * 登录功能P层
 *
 * @author liziqiang@ftrend.cn
 */
public class LoginPresenter implements Contract.LoginPresenter {
    private Contract.LoginView mView;


    public LoginPresenter(Contract.LoginView mView) {
        this.mView = mView;
    }

    public static LoginPresenter createPresenter(Contract.LoginView mView) {
        return new LoginPresenter(mView);
    }


    @Override
    public void initDepData(Context context) {
        List<Dep> list = SQLite.select().from(Dep.class).queryList();
        mView.setDepData(list);


        /*List<Dep> depList = new ArrayList<>();
        Cursor cursor = DatabaseManger.getInstance(context).query("Dep", new String[]{"*"}, null, null, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Dep dep = new Dep();
                    dep.setDepName(cursor.getString(cursor.getColumnIndex("DepName")));
                    dep.setDepCode(cursor.getString(cursor.getColumnIndex("DepCode")));
                    depList.add(dep);
                } while (cursor.moveToNext());
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        mView.setDepData(depList);*/

    }

    @Override
    public void initUserData(String depCode) {
        List<User> list = SQLite.select().from(User.class).queryList();
        mView.setUserData(list);
    }


    @Override
    public void checkUserInfo() {

    }
}
