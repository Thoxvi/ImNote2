package thoxvi.imnote2.Utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Thoxvi on 2017/3/13.
 */
public class StringUtilTest {
    private static class TestMe {
        String a = "123";
        int b = 123;
        double c = 123.000000;
        boolean d = false;
    }

    @Test
    public void sliteString() throws Exception {
        String s = StringUtil.sliteString("1234567890", 5);
        Assert.assertEquals(s, "12345…");

        s = StringUtil.sliteString("1234", 5);
        Assert.assertEquals(s, "1234");

        s = StringUtil.sliteString("", 5);
        Assert.assertEquals(s, "");

        s = StringUtil.sliteString("", -1);
        Assert.assertEquals(s, "");

        s = StringUtil.sliteString("1234", 0);
        Assert.assertEquals(s, "");

        s = StringUtil.sliteString("1234", -1);
        Assert.assertEquals(s, "");

        s = StringUtil.sliteString("123456", -1);
        Assert.assertEquals(s, "");
    }

    @Test
    public void objToJson() throws Exception {
        TestMe m = new TestMe();
        String s = StringUtil.objToJson(m);
        Assert.assertEquals(s, "{\"a\":\"123\",\"b\":123,\"c\":123.0,\"d\":false}");
    }

    @Test
    public void objFromJson() throws Exception {
        String s = "{\"a\":\"123\",\"b\":123,\"c\":123.0,\"d\":false}";
        TestMe m = StringUtil.objFromJson(s, TestMe.class);
        Assert.assertEquals(m.a, "123");
        Assert.assertEquals(m.b, 123);
        Assert.assertEquals(m.c, 123.0, 1);
        Assert.assertEquals(m.d, false);

        s="VGVzdOa1i+ivlTEyMzwuCiwuOj8gUC5s";
        m = StringUtil.objFromJson(s, TestMe.class);
        Assert.assertEquals(m, null);
    }
}