package thoxvi.imnote2.Utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Thoxvi on 2017/3/13.
 */
public class SecurityUtilTest {
    @Test
    public void stringToMD5() throws Exception {
        String s = SecurityUtil.stringToMD5("Test测试123<.,.:? P.l");
        Assert.assertEquals("d83b92c1f8414f4071548e0a6c6bbbc8", s);
    }

    @Test
    public void stringToSHA_1() throws Exception {
        String s = SecurityUtil.stringToSHA_1("Test测试123<.,.:? P.l");
        Assert.assertEquals("f76ad94e32c7c47ad2e8f3b47a41f68faab7d6f4", s);
    }

    @Test
    public void stringToSHA_256() throws Exception {
        String s = SecurityUtil.stringToSHA_256("Test测试123<.,.:? P.l");
        Assert.assertEquals("9c074904d54a3b3bd17598641966b67392133f08f0cc75a7b103793b8a22714e", s);
    }

    @Test
    public void stringToSHA_512() throws Exception {
        String s = SecurityUtil.stringToSHA_512("Test测试123<.,.:? P.l");
        Assert.assertEquals("9db8a4076085d596e94e32c44d1ff9af58f61836f53603042e146719ec0066e680816952cf4a5c9d48c208fbb5ab0ae0dd66d787683f6e67fe9951f3e7ef26", s);
    }

    @Test
    public void stringToHash() throws Exception {
        String s = SecurityUtil.stringToHash("Test测试123<.,.:? P.l", 0);
        Assert.assertEquals("d83b92c1f8414f4071548e0a6c6bbbc8", s);

        s = SecurityUtil.stringToHash("Test测试123<.,.:? P.l", 1);
        Assert.assertEquals("f76ad94e32c7c47ad2e8f3b47a41f68faab7d6f4", s);

        s = SecurityUtil.stringToHash("Test测试123<.,.:? P.l", 2);
        Assert.assertEquals("9c074904d54a3b3bd17598641966b67392133f08f0cc75a7b103793b8a22714e", s);

        s = SecurityUtil.stringToHash("Test测试123<.,.:? P.l", 3);
        Assert.assertEquals("9db8a4076085d596e94e32c44d1ff9af58f61836f53603042e146719ec0066e680816952cf4a5c9d48c208fbb5ab0ae0dd66d787683f6e67fe9951f3e7ef26", s);

        s = SecurityUtil.stringToHash("Test测试123<.,.:? P.l", 4);
        Assert.assertEquals("", s);

        s = SecurityUtil.stringToHash("Test测试123<.,.:? P.l", -1);
        Assert.assertEquals("", s);
    }

}