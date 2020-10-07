package cn.bridgeli.netty.demo.model;

/**
 * @author bridgeli
 */
public class GpsData {
    public GpsData(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    private String vehicleNo;

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

}
