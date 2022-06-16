package utils;

import android.text.TextUtils;

import java.util.List;


public class DataUtils {
    public static boolean isListEmpty(List<?> list) {
        return list == null || list.size() == 0;
    }

    public static int getListSize(List<?> list) {
        if (list == null) {
            return 0;
        }
        return list.size();
    }
    public static boolean isEmpty(CharSequence charSequence){
        return TextUtils.isEmpty(charSequence);
    }
}
