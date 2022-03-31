package nctu.winlab.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AutoTestFlag {
    private final Logger log = LoggerFactory.getLogger(getClass());

    enum PingType {
        ICMPing, ARPing, TCPing, UDPing, SCTPing
    }

    enum TreatmentType {
        TCPSrc, TCPDst, UDPSrc, UDPDst, SCTPSrc, SCTPDst, IPSrc, IPDst, ETHSrc, ETHDst, OUTPUT, VlanPush, VlanPop, VlanVid
    }

    protected PingType currentMatchFieldFlag = PingType.ICMPing;
    protected TreatmentType currentTreatmentFlag = TreatmentType.OUTPUT;

    protected Boolean flagsProxyARP = false;

    protected Boolean flagsMatchARP = false;
    protected Boolean flagsMatchIPv4 = false;
    protected Boolean flagsMatchVlan = false;

    protected Boolean flagsUseDummySrcPort = false;
    protected Boolean flagsUseDummyDstPort = false;
    protected Boolean flagsUseDummySrcMac = false;
    protected Boolean flagsUseDummyDstMac = false;
    protected Boolean flagsUseDummySrcIp = false;
    protected Boolean flagsUseDummyDstIp = false;

    protected Boolean flagsUseVlanSrc = false;
    protected Boolean flagsUseVlanDst = false;
    protected Boolean flagsUseVlanPop = false;





    protected List<TreatmentType> treatmentFlags = new ArrayList<>();

    public AutoTestFlag() {
        reset();
    }

    // matchfield region
    public void reset() {
        treatmentFlags.clear();
        flagsProxyARP = false;

        flagsMatchARP = false;
        flagsMatchIPv4 = false;
        flagsMatchVlan = false;

        flagsUseDummySrcPort = false;
        flagsUseDummyDstPort = false;
        flagsUseDummySrcMac = false;
        flagsUseDummyDstMac = false;
        flagsUseDummySrcIp = false;
        flagsUseDummyDstIp = false;

        flagsUseVlanSrc = false;
        flagsUseVlanDst = false;
        flagsUseVlanPop = false;



        proxy(false);
    }

    public PingType GetMatchFieldFlag() {
        return currentMatchFieldFlag;
    }

    public void SetMatchFieldFlag(PingType s) {
        currentMatchFieldFlag = s;
    }

    public void EnableProxyARP() {
        if (!flagsProxyARP) {
            proxy(true);
            flagsProxyARP = true;
        }
    }

    public void DisableProxyARP() {
        proxy(false);
    }

    public Boolean MatchIPV4() {
        if (!flagsMatchIPv4) {
            flagsMatchIPv4 = true;
            return false;
        }
        return true;
    }

    public Boolean MatchARP() {
        if (!flagsMatchARP) {
            flagsMatchARP = true;
            return false;
        }
        return true;
    }

    public Boolean MatchVlan() {
        if (!flagsMatchVlan) {
            flagsMatchVlan = true;
            flagsUseVlanSrc = true;
            return false;
        }
        return true;
    }

    private void proxy(Boolean action) {
        try {
            if (action) {
                Runtime.getRuntime().exec("onos-app localhost activate org.onosproject.proxyarp");
            } else {
                Runtime.getRuntime().exec("onos-app localhost deactivate org.onosproject.proxyarp");
            }
            Thread.sleep(2000);
        } catch (Exception e) {
        }
    }

    // treatment region
    public void SetTreatmentFlag(TreatmentType s) {
        if (!treatmentFlags.contains(s)) {
            treatmentFlags.add(s);
        }
    }

    public void SetUseDummySrcIp() {
        flagsUseDummySrcIp = true;
    }

    public void SetUseDummyDstIp() {
        flagsUseDummyDstIp = true;
    }

    public void SetUseDummySrcMac() {
        flagsUseDummySrcMac = true;
    }

    public void SetUseDummyDstMac() {
        flagsUseDummyDstMac = true;
    }

    public void SetUseDummySrcPort() {
        flagsUseDummySrcPort = true;
    }

    public void SetUseDummyDstPort() {
        flagsUseDummyDstPort = true;
    }

    public void SetUseVlanSrc() {
        flagsUseVlanSrc = true;
    }

    public void SetUseVlanDst() {
        flagsUseVlanDst = true;
    }

    public void SetUseVlanPop(){
        flagsUseVlanPop = true;
    }

    public Boolean GetFlagsUseDummySrcIp() {
        return flagsUseDummySrcIp;
    }

    public Boolean GetFlagsUseDummyDstIp() {
        return flagsUseDummyDstIp;
    }

    public Boolean GetFlagsUseDummySrcMac() {
        return flagsUseDummySrcMac;
    }

    public Boolean GetFlagsUseDummyDstMac() {
        return flagsUseDummyDstMac;
    }

    public Boolean GetFlagsUseDummySrcPort() {
        return flagsUseDummySrcPort;
    }

    public Boolean GetFlagsUseDummyDstPort() {
        return flagsUseDummyDstPort;
    }

    public void ReverseArray() {
        Collections.reverse(treatmentFlags);
    }

    public String GetDumpCommand(List<MachineInfo> infos) {
        return GetDumpCommand(infos, 1);
    }

    public String GetDumpCommand(List<MachineInfo> infos, int paramDstId) {
        log.info("{}", treatmentFlags.toString());
        if (treatmentFlags.size() == 1 && treatmentFlags.get(0) == TreatmentType.OUTPUT) {
            log.info(ANSI.RED + "Generate By Protocol" + ANSI.RESET);
            return generateByProtocol(infos, paramDstId);
        }
        else {
            log.info(ANSI.RED + "Generate By List" + ANSI.RESET);
            return generateByList(infos, paramDstId);
        }
    }

    private String generateByList(List<MachineInfo> infos, int paramDstId) {
        String srcIp = getSrcIpToUseAtDump(infos),
            srcMac = infos.get(0).macAddress.toString(),
            dstIp = getDstIpToUseAtDump(infos, paramDstId),
            dstMac = infos.get(paramDstId).macAddress.toString(),
            commandStr;


        log.info(ANSI.RED + "dump : srcIp = {}" + ANSI.RESET, srcIp);
        log.info(ANSI.RED + "dump : dstIp = {}" + ANSI.RESET, dstIp);

        commandStr = "\"sudo -S tcpdump -n ";
        log.info(ANSI.YELLOW + "treatmentFlags.size() = {}, " + ANSI.RESET, treatmentFlags.size());
        for (int k = 0; k < treatmentFlags.size(); k++) {
            log.info(treatmentFlags.get(k).toString());
            switch(treatmentFlags.get(k)) {
                case ETHSrc:
                    commandStr += "ether src ";
                    commandStr += srcMac;
                    break;
                case ETHDst:
                    commandStr += "ether dst ";
                    commandStr += dstMac;
                    break;
                case TCPSrc:
                    commandStr += "tcp and src port 7650 ";
                    break;
                case TCPDst:
                    commandStr += "tcp and dst port 2830 ";
                    break;
                case UDPSrc:
                    commandStr += "udp and src port 7650 ";
                    break;
                case UDPDst:
                    commandStr += "udp and dst port 2830 ";
                    break;
                case IPSrc:
                    commandStr += "src ";
                    commandStr += srcIp;
                    break;
                case IPDst:
                    commandStr += "dst ";
                    commandStr += dstIp;
                    break;
                case OUTPUT:
                default:
                    break;
            }

            commandStr += getVlanDecision();
            log.info(ANSI.YELLOW + "{}" + ANSI.RESET, commandStr);
        }

        commandStr = commandStr.substring(0, commandStr.length()) + " -c 1 -i " + getDstIface(infos, paramDstId) + "\"";


        log.info(ANSI.RED + "{}" + ANSI.RESET, commandStr);



        return commandStr;
    }

    private String generateByProtocol(List<MachineInfo> infos, int paramDstId) {
        String src = infos.get(0).machineIp.toString();
        switch (currentMatchFieldFlag) {
            case ARPing: {
                return "\"sudo -S tcpdump -n src " + src + " and arp -c 1 -i " + infos.get(paramDstId).machineIface + "\"";
            }
            case ICMPing: {
                String commandStr = "\"sudo -S tcpdump -n src " + src + getVlanDecision() + " and icmp -c 1 -i " + infos.get(paramDstId).machineIface + "\"";
                log.info(ANSI.RED + commandStr + ANSI.RESET);
                return "\"sudo -S tcpdump -n src " + src + getVlanDecision() + " and icmp -c 1 -i " + infos.get(paramDstId).machineIface + "\"";
            }
            case TCPing: {
                return "\"sudo -S tcpdump -n src " + src + " and tcp -c 1 -i " + infos.get(paramDstId).machineIface + "\"";
            }
            case UDPing: {
                return "\"sudo -S tcpdump -n src " + src + " and udp -c 1 -i " + infos.get(paramDstId).machineIface + "\"";
            }
            case SCTPing: {
                return "\"sudo -S tcpdump -n src " + src + " and sctp -c 1 -i " + infos.get(paramDstId).machineIface + "\"";
            }
            default:
                return "";
        }
    }

    public String GetPingCommand(List<MachineInfo> infos) {
        return GetPingCommand(infos, 1);
    }

    public String GetPingCommand(List<MachineInfo> infos, int paramDstId) {
        String srcIp, dstIp, srcPort, dstPort, srcMac, dstMac, srcIface;
        dstPort = flagsUseDummyDstPort ? "3460" : "2830";
        // The Idolm@ster ShinyColors
        // for xxx_dst series, let scapy script send to dst 3460, and make treatment
        // rewrite dst to 2830
        // The Idolm@ster Cinderella Girls
        // otherwise, simply send to 2830

        srcPort = flagsUseDummySrcPort ? "9610" : "7650";
        // The Idolm@ster AllStars
        // for xxx_src series, let scapy script send from src 9610, and make treatment
        // rewrite src to 7650
        // The Idolm@ster MillionLive TheaterDays
        // otherwise, simply send from 7650
        srcMac = flagsUseDummySrcMac ? infos.get(infos.size()-1).macAddress.toString() : infos.get(0).macAddress.toString();
        dstMac = flagsUseDummyDstMac ? infos.get(infos.size()-1).macAddress.toString() : infos.get(paramDstId).macAddress.toString();
        
        srcIp = getSrcIpToUseAtPing(infos);
        dstIp = getDstIpToUseAtPing(infos, paramDstId);


        log.info(ANSI.RED + "ping : srcIp = {}" + ANSI.RESET, srcIp);
        log.info(ANSI.RED + "ping : dstIp = {}" + ANSI.RESET, dstIp);
        log.info(ANSI.YELLOW + "srcMac : {}" + ANSI.RESET, srcMac);
        log.info(ANSI.YELLOW + "dstMac : {}" + ANSI.RESET, dstMac);

        srcIface = getSrcIface(infos);
        switch (currentMatchFieldFlag) {
            case ARPing: {
                log.info(ANSI.YELLOW + "flagARPING, using ARPing..." + ANSI.RESET);
                return "\"sudo ./ping.py flagARPING " + dstIp + "\"";
            }
            case ICMPing: { // we don't care about ports while using icmping.
                log.info(ANSI.YELLOW + "flagICMPING, using ICMPing..." + ANSI.RESET);
                return "\"sudo ./ping.py flagPING " + concatIpStr(srcMac, dstMac, srcIp, dstIp, srcIface) + "\"";
            }
            case TCPing: {
                log.info(ANSI.YELLOW + "flagTCPING, using TCPing..." + ANSI.RESET);
                return "\"sudo ./ping.py flagTCPING " + concatPortStr(srcMac, dstMac, srcIp, dstIp, srcPort, dstPort, srcIface) + "\"";
            }
            case UDPing: {
                log.info(ANSI.YELLOW + "flagUDPING, using UDPing..." + ANSI.RESET);
                return "\"sudo ./ping.py flagUDPING " + concatPortStr(srcMac, dstMac, srcIp, dstIp, srcPort, dstPort, srcIface) + "\"";
            }
            case SCTPing: {
                log.info(ANSI.YELLOW + "flagSCTPING, using SCTPing..." + ANSI.RESET);
                return "\"sudo ./ping.py flagSCTPING " + concatPortStr(srcMac, dstMac, srcIp, dstIp, srcPort, dstPort, srcIface) + "\"";
            }
            default: {
                return "";
            }
        }
    }

    private String concatIpStr(String str1, String str2, String str3, String str4, String str5) {
        return String.format("%s %s %s %s %s", str1, str2, str3, str4, str5);
    }

    private String concatPortStr(String str1, String str2, String str3, String str4, String str5, String str6, String str7) {
        return String.format("%s %s %s %s %s %s %s", str1, str2, str3, str4, str5, str6, str7);
    }

    private String getSrcIpToUseAtDump(List<MachineInfo> infos) {
        if (flagsUseVlanSrc && flagsUseDummySrcIp) {
            // Vlan / Dummy yes -> tt: SetIpSrc + PopVlan / ModVlan [Multi treatment possible?]
            // or: Match: VlanVID + tt: SetIpSrc
            return infos.get(infos.size()-1).machineIpV.toString();
        }
        else if (flagsUseVlanSrc && !flagsUseDummySrcIp) {
            // no Dummy, Vlan yes -> tt:PopVlan / ModVlan
            return infos.get(0).machineIpV.toString();
        }
        else if (!flagsUseVlanSrc && flagsUseDummySrcIp) {
            // no Vlan, Dummy yes -> tt:SetIpSrc, which will be modified to the correct one
            // return infos.get(infos.size()-1).machineIp.toString();
            return infos.get(0).machineIp.toString();
        }
        else if (!flagsUseVlanSrc && !flagsUseDummySrcIp) {
            // no Vlan / Dummy -> tt:other normal situations
            return infos.get(0).machineIp.toString();
        }
        else {
            return "";
        }
    }

    private String getDstIpToUseAtDump(List<MachineInfo> infos, int paramDstId) {
        if (flagsUseVlanDst && flagsUseDummyDstIp) {
            return infos.get(infos.size()-1).machineIpV.toString();
        }
        else if (flagsUseVlanDst && !flagsUseDummyDstIp) {
            return infos.get(paramDstId).machineIpV.toString();
        }
        else if (!flagsUseVlanDst && flagsUseDummyDstIp) {
            // return infos.get(infos.size()-1).machineIp.toString();
            return infos.get(paramDstId).machineIp.toString();
        }
        else if (!flagsUseVlanDst && !flagsUseDummyDstIp) {
            return infos.get(paramDstId).machineIp.toString();
        }
        else {
            return "";
        }
    }


    private String getSrcIpToUseAtPing(List<MachineInfo> infos) {
        // same as < getSrcIpToUseAtDump >
        if (flagsUseVlanSrc && flagsUseDummySrcIp) {
            return infos.get(infos.size()-1).machineIpV.toString();
        }
        else if (flagsUseVlanSrc && !flagsUseDummySrcIp) {
            return infos.get(0).machineIpV.toString();
        }
        else if (!flagsUseVlanSrc && flagsUseDummySrcIp) {
            return infos.get(infos.size()-1).machineIp.toString();
        }
        else if (!flagsUseVlanSrc && !flagsUseDummySrcIp) {
            return infos.get(0).machineIp.toString();
        }
        else {
            return "";
        }
    }

    private String getDstIpToUseAtPing(List<MachineInfo> infos, int paramDstId) {
        if (flagsUseVlanDst && flagsUseDummyDstIp) {
            return infos.get(infos.size()-1).machineIpV.toString();
        }
        else if (flagsUseVlanDst && !flagsUseDummyDstIp) {
            return infos.get(paramDstId).machineIpV.toString();
        }
        else if (!flagsUseVlanDst && flagsUseDummyDstIp) {
            return infos.get(infos.size()-1).machineIp.toString();
        }
        else if (!flagsUseVlanDst && !flagsUseDummyDstIp) {
            return infos.get(paramDstId).machineIp.toString();
        }
        else {
            return "";
        }
    }

    private String getSrcIface(List<MachineInfo> infos) {
        if (flagsUseVlanSrc && flagsUseDummySrcIp) {
            return infos.get(infos.size()-1).machineIfaceV;
        }
        else if (flagsUseVlanSrc && !flagsUseDummySrcIp) {
            return infos.get(0).machineIfaceV;
        }
        else if (!flagsUseVlanSrc && flagsUseDummySrcIp) {
            return infos.get(0).machineIface;
        }
        else if (!flagsUseVlanSrc && !flagsUseDummySrcIp) {
            return infos.get(0).machineIface;
        }
        else {
            return "";
        }

        // right! (maybe)
    }

    private String getDstIface(List<MachineInfo> infos, int paramDstId) {

        return infos.get(paramDstId).machineIface;

        // if (flagsUseVlanDst && flagsUseDummyDstIp) {
        //     // return infos.get(infos.size()-1).machineIfaceV; // Receive by fake ifaceV? wrong baby
        //     return infos.get(paramDstId).machineIfaceV;
        // }
        // else if (flagsUseVlanDst && !flagsUseDummyDstIp) {
        //     return infos.get(paramDstId).machineIfaceV;
        // }
        // else if (!flagsUseVlanDst && flagsUseDummyDstIp) {
        //     return infos.get(paramDstId).machineIface;
        // }
        // else if (!flagsUseVlanDst && !flagsUseDummyDstIp) {
        //     return infos.get(paramDstId).machineIface;
        // }
        // else {
        //     return "";
        // }

        // Vlan / Not Vlan seperation ?????????

    }

    private String getVlanDecision() {
        if (flagsUseVlanPop){
            return "";
        }
        else {
            if (flagsUseVlanDst){
                return " vlan 2";
            }
            else {
                if (flagsMatchVlan){
                    return " vlan 1";
                }
                else{
                    return "";
                }
            }
        }



        // M:any T:Vpush -> vlan 2 
            // flagsMatchVlan = false;
            // flagsUseVlanSrc = false;
            // flagsUseVlanDst = true;
            // flagsVlanPop = false;


        // M:V_ID T:Vmod -> vlan 2 
            // flagsMatchVlan = true;
            // flagsUseVlanSrc = true;
            // flagsUseVlanDst = true;
            // flagsVlanPop = false;

        // M:V_ID T:else -> vlan 1
            // flagsMatchVlan = true;
            // flagsUseVlanSrc = true;
            // flagsUseVlanDst = false;
            // flagsVlanPop = false;
        
        // XX
            // flagsMatchVlan = false;
            // flagsUseVlanSrc = false;
            // flagsUseVlanDst = false;
            // flagsVlanPop = false;

        // Vlan Logic!!!
        // vlanpop = true -> X
            // usevlandst = true -> vlan 2 
                // MatchVlan = true -> vlan 1
                // else = X
    }
}



