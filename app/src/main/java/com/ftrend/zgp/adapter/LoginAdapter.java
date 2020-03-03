package com.ftrend.zgp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ftrend.zgp.R;
import com.ftrend.zgp.model.Dep;
import com.ftrend.zgp.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Spinner适配器
 *
 * @author liziqiang@ftrend.cn
 */
public class LoginAdapter<T> extends BaseAdapter {
    private List<T> data = new ArrayList<>();
    private Context context;
    private int type = 0;//0是dep，1是用户

    public LoginAdapter(Context context, List<T> data, int type) {
        this.data = data;
        this.context = context;
        this.type = type;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String getItemCode(int position) {
        return type == 0 ? ((List<Dep>) data).get(position).getDepCode() : ((List<User>) data).get(position).getUserCode();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String code = "";
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.login_sp_item, null);
        if (convertView != null) {
            TextView titleTv = convertView.findViewById(R.id.login_sp_title);
            TextView codeTv = convertView.findViewById(R.id.login_sp_code);
            code = type == 0 ? ((List<Dep>) data).get(position).getDepCode() : ((List<User>) data).get(position).getUserCode();
            code = code.contains("-")?code.substring(0,code.indexOf("-")):code;
            /*titleTv.setText((type == 0 ? ((List<Dep>) data).get(position).getDepName() : ((List<User>) data).get(position).getUserName()));
            codeTv.setText(code);*/
            String title = (type == 0) ? ((List<Dep>) data).get(position).getDepName() : ((List<User>) data).get(position).getUserName();
            titleTv.setText(String.format(Locale.CHINA, "%s-%s", code, title));
            codeTv.setText(code);
        }

        return convertView;
    }
}
