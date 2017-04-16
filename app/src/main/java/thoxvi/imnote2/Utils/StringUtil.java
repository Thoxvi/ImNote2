package thoxvi.imnote2.Utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.provider.ContactsContract;
import android.util.Base64;
import android.widget.Toast;

import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import thoxvi.imnote2.BaseDatas.Note.INoteBO;
import thoxvi.imnote2.BaseDatas.Note.Note;

/**
 * Created by Thoxvi on 2017/3/1.
 */

public class StringUtil {
    private static final Gson gson = new Gson();

    public static String getRandomString(int n) {
        String s = "";
        for (int j = 0; j < n; j++) {
            String STRING_RANDOME_62 = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789";
            int i = new Random().nextInt(STRING_RANDOME_62.length());
            s += STRING_RANDOME_62.charAt(i);
        }
        return s;
    }

    public static String getRandomString() {
        return getRandomString(32);
    }

    public static String sliteString(String content, int lenght) {
        if (lenght <= 0) return "";
        if (content.length() > lenght) {
            return content.substring(0, lenght) + "…";
        } else {
            return content;
        }
    }

    public static void setClipString(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("来自Note2", text);
        clipboard.setPrimaryClip(clip);
    }

    //About Json
    public static String objToJson(Object o) {
        return gson.toJson(o);
    }

    public static <T> T objFromJson(String s, Class<T> c) {
        try {
            return gson.fromJson(s, c);
        } catch (Exception e) {
            return null;
        }

    }

    //About Base64
    public static String stringToBase64(String s) {
        try {
            return new String(Base64.encode(s.getBytes(), Base64.NO_WRAP), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        }
    }

    public static String stringFromBase64(String s) {
        try {
            return new String(Base64.decode(s, Base64.NO_WRAP), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean maybeBase64(String text) {
        return !stringFromBase64(text).isEmpty();
    }

    public static boolean maybeBase64ThenJson(String text){
        String json=stringFromBase64(text);
        if (json.isEmpty())return false;
        INoteBO note=objFromJson(json, Note.class);
        return note != null;
    }


}
