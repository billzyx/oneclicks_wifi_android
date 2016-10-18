package cn.oneclicks.wifi_school;


import java.io.InputStream;

/**
 * Created by tianwai on 2016/10/18.
 */

public class StreamTool {
    /**
     * @方法功能 InputStream 转为 byte
     * @param inStream
     * @return 字节数组
     * @throws Exception
     */
    public static byte[] inputStream2Byte(InputStream inStream)
            throws Exception {
        int count = 0;
        while (count == 0) {
            count = inStream.available();
        }
        byte[] b = new byte[count];
        inStream.read(b);
        return b;
    }
}
