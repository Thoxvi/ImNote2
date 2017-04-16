package thoxvi.imnote2.Utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Thoxvi on 2017/3/13.
 */

public class SecurityUtil {

    public static String[] HASH_NAME_LIST = {
            "MD5",
            "SHA-1",
            "SHA-256",
            "SHA-512"
    };

    //About Hash
    public static String stringToHash(String s, int t) {
        if (t < 0 || t > HASH_NAME_LIST.length - 1) return "";
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_NAME_LIST[t]);
            md.update(s.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
        }
        return "";
    }

    public static String stringToMD5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(s.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
        }
        return "";
    }

    public static String stringToSHA_1(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(s.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
        }
        return "";
    }

    public static String stringToSHA_256(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(s.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
        }
        return "";
    }

    public static String stringToSHA_512(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(s.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
        }
        return "";
    }

}
