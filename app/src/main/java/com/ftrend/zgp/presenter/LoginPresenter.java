package com.ftrend.zgp.presenter;

import android.content.Context;

import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.model.Dep;
import com.ftrend.zgp.model.User;
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
//        String depCode, clsCode;
//        for (int i = 0; i < 200; i++) {
//            DepProduct depProduct = new DepProduct();
//            depProduct.setSpec(String.valueOf(0.1 + i));
//            depProduct.setPrice((0.1f + i));
//            depProduct.setProdName("测试商品" + i);
//            depProduct.setProdCode(String.valueOf(10000 + i));
//            if (i > 0 && i < 30) {
//                depCode = "1000";
//                clsCode = depCode;
//            } else if (i >= 30 && i < 90) {
//                depCode = "1001";
//                clsCode = depCode;
//            } else {
//                depCode = "1002";
//                clsCode = depCode;
//            }
//            depProduct.setDepCode(depCode);
//            depProduct.setClsCode(clsCode);
//            depProduct.insert();
//        }
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
        List<User> userList = SQLite.select().from(User.class).queryList();
        mView.setUserData(userList);
    }


    @Override
    public void checkUserInfo() {

    }
}
