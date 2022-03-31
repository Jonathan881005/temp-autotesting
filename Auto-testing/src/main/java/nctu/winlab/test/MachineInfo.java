package nctu.winlab.test;

import org.onlab.packet.Ip4Address;
import org.onlab.packet.MacAddress;
import org.onosproject.net.Host;

public class MachineInfo {
    public int machineId;
    public Ip4Address machineIp;
    public String machineIface;
    public Ip4Address machineIpV;
    public String machineIfaceV;
    public Host machineHost;
    public MacAddress macAddress;

    MachineInfo(int mId, String mIface, String mIP, String mIfaceV, String mIPV) {
        this.machineId = mId;
        this.machineIp = Ip4Address.valueOf(mIP);
        this.machineIpV = Ip4Address.valueOf(mIPV);
        this.machineIface = mIface;
        this.machineIfaceV = mIfaceV;
    }

    MachineInfo(int mId, String mIP, String mIPV, String mMac) {
        // unreachable
        this.machineId = mId;
        this.machineIp = Ip4Address.valueOf(mIP);
        this.machineIpV = Ip4Address.valueOf(mIPV);
        this.macAddress = MacAddress.valueOf(mMac);
    }
}
