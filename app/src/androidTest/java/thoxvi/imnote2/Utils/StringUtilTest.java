package thoxvi.imnote2.Utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Thoxvi on 2017/3/13.
 */
public class StringUtilTest {
    @Test
    public void stringToBase64() throws Exception {
        String s = StringUtil.stringToBase64("Test测试123<.\n,.:? P.l");
        Assert.assertEquals(s, "VGVzdOa1i+ivlTEyMzwuCiwuOj8gUC5s");
    }

    @Test
    public void stringFromBase64() throws Exception {
        String s = StringUtil.stringFromBase64("VGVzdOa1i+ivlTEyMzwuCiwuOj8gUC5s");
        Assert.assertEquals(s, "Test测试123<.\n,.:? P.l");
        s=StringUtil.stringFromBase64("测试一下！");
        Assert.assertEquals(s, "");
        s=StringUtil.stringFromBase64("dlakfjalsdgkjlkdaflkjweiocn");
        Assert.assertNotEquals(s, "");
        s=StringUtil.stringFromBase64("654654654");
        Assert.assertEquals(s, "");
    }

}