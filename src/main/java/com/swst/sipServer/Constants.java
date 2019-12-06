package com.swst.sipServer;

/**
 * @Auther: fregun
 * @Date: 19-11-5 17:02
 * @Description:
 */
public class Constants {
    public static String content = "<?xml version=\"1.0\"?>\n" +
            "<Query>\n" +
            "<CmdType>Catalog</CmdType>\n" +
            "<SN>248</SN>\n" +
            "<DeviceID>C5-07-D2</DeviceID>\n" +
            "</Query>";
    public static String enlarge = "<?xml version=\"1.0\"?>\n" +
            "<Control>\n" +
            "<CmdType>DeviceControl</CmdType>\n" +
            "<SN>11</SN>\n" +
            "<DeviceID>C5-07-D2</DeviceID>\n" +
            "<PTZCmd>A50F0110000010D5</PTZCmd>\n" +
            "</Control>\n";
    public static String reduce = "<?xml version=\"1.0\"?>\n" +
            "<Control>\n" +
            "<CmdType>DeviceControl</CmdType>\n" +
            "<SN>11</SN>\n" +
            "<DeviceID>C5-07-D2</DeviceID>\n" +
            "<PTZCmd>A50F0120000010E5</PTZCmd>\n" +
            "</Control>";
    public static String down = "<?xml version=\"1.0\"?>\n" +
            "<Control>\n" +
            "<CmdType>DeviceControl</CmdType>\n" +
            "<SN>11</SN>\n" +
            "<DeviceID>C5-07-D2</DeviceID>\n" +
            "<PTZCmd>A50F0104001F00D8</PTZCmd>\n" +
            "</Control>";
    public static String up = "<?xml version=\"1.0\"?>\n" +
            "<Control>\n" +
            "<CmdType>DeviceControl</CmdType>\n" +
            "<SN>11</SN>\n" +
            "<DeviceID>C5-07-D2</DeviceID>\n" +
            "<PTZCmd>A50F0108001F00DC</PTZCmd>\n" +
            "</Control>";
    public static String right = "<?xml version=\"1.0\"?>\n" +
            "<Control>\n" +
            "<CmdType>DeviceControl</CmdType>\n" +
            "<SN>11</SN>\n" +
            "<DeviceID>C5-07-D2</DeviceID>\n" +
            "<PTZCmd>A50F01011F0000D5</PTZCmd>\n" +
            "</Control>";
    public static String left = "<?xml version=\"1.0\"?>\n" +
            "<Control>\n" +
            "<CmdType>DeviceControl</CmdType>\n" +
            "<SN>11</SN>\n" +
            "<DeviceID>C5-07-D2</DeviceID>\n" +
            "<PTZCmd>A50F01021F0000D6</PTZCmd>\n" +
            "</Control>";
    public static String stop = "<?xml version=\"1.0\"?>\n" +
            "<Control>\n" +
            "<CmdType>DeviceControl</CmdType>\n" +
            "<SN>11</SN>\n" +
            "<DeviceID>C5-07-D2</DeviceID>\n" +
            "<PTZCmd>A50F0100000000B5</PTZCmd>\n" +
            "</Control>";
    public static String actureTime = "v=0\n" +
            "o=C5-07-EDE 0 0 IN IP4 192.168.6.201\n" +
            "s=Play\n" +
            "c=IN IP4 192.168.6.201\n" +
            "t=0 0\n" +
            "m=video 5062 RTP/AVP 96\n" +
            "a=recvonly\n" +
            "a=rtpmap:96 H264/90000\n" +
            "y=0100000001\n" +
            "f=\n";
}
