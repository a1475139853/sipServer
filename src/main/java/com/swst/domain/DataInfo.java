package com.swst.domain;

/**
 * @author frosty
 * @description   元数据
 * @date 2020-01-12 12:15:23
 */


public class DataInfo {

    /**
     * 摄像头 code
     */
    private  String cameraCode;

    /**
     * 摄像头ip
     */
    private  String  cameraIp;
    /**
     * 摄像头端口
     */
     private  Integer CameraPort;

    /**
     * 接收端  code  本机code
     */
    private String receiveCode;
    /**
     * 接收端 (暂时是本机) ip
     */
    private String receiveIp;
    /**
     * 接收段 端口
     */
     private  Integer receivePort;
    /**
     * 阀值  (每一秒加一  大于8 秒就默认该连接断开)
     */
    private int  threshold;


    public String getCameraCode() {
        return cameraCode;
    }

    public void setCameraCode(String cameraCode) {
        this.cameraCode = cameraCode;
    }

    public String getCameraIp() {
        return cameraIp;
    }

    public void setCameraIp(String cameraIp) {
        this.cameraIp = cameraIp;
    }

    public Integer getCameraPort() {
        return CameraPort;
    }

    public void setCameraPort(Integer cameraPort) {
        CameraPort = cameraPort;
    }

    public String getReceiveIp() {
        return receiveIp;
    }

    public void setReceiveIp(String receiveIp) {
        this.receiveIp = receiveIp;
    }

    public Integer getReceivePort() {
        return receivePort;
    }

    public void setReceivePort(Integer receivePort) {
        this.receivePort = receivePort;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }


    public String getReceiveCode() {
        return receiveCode;
    }

    public void setReceiveCode(String receiveCode) {
        this.receiveCode = receiveCode;
    }

    @Override
    public String toString() {
        return "DataInfo{" +
                "cameraCode='" + cameraCode + '\'' +
                ", cameraIp='" + cameraIp + '\'' +
                ", CameraPort=" + CameraPort +
                ", receiveIp='" + receiveIp + '\'' +
                ", receivePort=" + receivePort +
                ", threshold=" + threshold +
                '}';
    }
}
