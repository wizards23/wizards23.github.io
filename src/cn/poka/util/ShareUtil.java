package cn.poka.util;

import com.example.pda.R;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ShareUtil {
    public static Context context;

    public static boolean toRepeatrun = false;// 是否调用rfid的repeat命令

    public static void toastShow(int stringId, Context context) {
        Toast toast = new Toast(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.toast, null);
        TextView text = (TextView) view.findViewById(R.id.toast);
        text.setText(stringId);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void toastShow(String string, Context context) {
        Toast toast = new Toast(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.toast, null);
        TextView text = (TextView) view.findViewById(R.id.toast);
        text.setText(string);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

}