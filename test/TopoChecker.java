package nctu.winlab.test;

import org.onlab.packet.Ip4Address;
import org.onosproject.net.Host;
import org.onosproject.net.host.HostService;

public class TopoChecker {
    static Boolean Check(int hostId, TestConfig ts, HostService hostService) {
        Ip4Address hostIp = ts.getIpInfo(hostId);
        if (hostService.getHostsByIp(hostIp).iterator().hasNext()) {
			Host thisHost = hostService.getHostsByIp(hostIp).iterator().next();
            ts.setHostInfo(hostId, thisHost);
			ts.setHostMac(hostId, thisHost.mac());
			return true;
		} else {
			//log.info(ANSI.RED + "This topology is not correct" + ANSI.RESET);
			return false;
		}
    }
}
