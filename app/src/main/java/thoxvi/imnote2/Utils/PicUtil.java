package thoxvi.imnote2.Utils;

import java.util.Calendar;

import thoxvi.imnote2.R;

/**
 * Created by Thoxvi on 2017/3/1.
 */

public class PicUtil {
    private static final int[] TIME_IMAGE = {
            R.mipmap.pic_time1,
            R.mipmap.pic_time2,
            R.mipmap.pic_time3,
            R.mipmap.pic_time4,
    };

    public static int getTimePic() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);

        if (8 <= hour && hour < 12) {
            return TIME_IMAGE[1];
        } else if (12 <= hour && hour < 16) {
            return TIME_IMAGE[2];
        } else if (16 <= hour && hour < 20) {
            return TIME_IMAGE[3];
        } else {
            return TIME_IMAGE[0];
        }
    }

}
