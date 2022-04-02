package nctu.winlab.test;

import org.onlab.packet.ARP;
import org.onlab.packet.Ethernet;
import org.onlab.packet.IPv4;
import org.onlab.packet.MacAddress;
import org.onlab.packet.TpPort;
import org.onlab.packet.VlanId;
import org.onosproject.net.flow.DefaultTrafficSelector;
import org.onosproject.net.flow.TrafficSelector;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MatchField {
    private final String IN_PORT = "IN_PORT";
    private final String ETH_SRC = "ETH_SRC";
    private final String ETH_DST = "ETH_DST";
    private final String ETH_TYPE = "ETH_TYPE";
    private final String IP_ECN = "IP_ECN";
    private final String IP_PROTO = "IP_PROTO";
    private final String IP_DST = "IP_DST";
    private final String IP_SRC = "IP_SRC";
    private final String ARP_OP = "ARP_OP";
    private final String ARP_SHA = "ARP_SHA";
    private final String ARP_SPA = "ARP_SPA";
    private final String ARP_THA = "ARP_THA";
    private final String ARP_TPA = "ARP_TPA";
    private final String TCP_SRC = "TCP_SRC";
    private final String TCP_DST = "TCP_DST";
    private final String UDP_SRC = "UDP_SRC";
    private final String UDP_DST = "UDP_DST";
    private final String SCTP_SRC = "SCTP_SRC";
    private final String SCTP_DST = "SCTP_DST";
    private final String VLAN_ID = "VLAN_ID";

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected TrafficSelector.Builder selector = DefaultTrafficSelector.builder();
    protected String MatchInfo = "", DependentInfo = "";

    //public AutoTestFlag ATFG = new AutoTestFlag();

    public MatchField() {
        //ATFG.reset();
    }

    public void newMatchField(String matchField, List<MachineInfo> infos, AutoTestFlag ATFG) {
        switch (matchField) {
            case IN_PORT: {
                ATFG.SetMatchFieldFlag(AutoTestFlag.PingType.ICMPing);
                ATFG.EnableProxyARP();

                selector.matchInPort(infos.get(0).machineHost.location().port());
                MatchInfo = MatchInfo + IN_PORT + " ";
                break;
            }
            case ETH_DST: {
                ATFG.SetMatchFieldFlag(AutoTestFlag.PingType.ICMPing);
                ATFG.EnableProxyARP();
                if (ATFG.GetFlagsUseDummyDstMac()) {
                    selector.matchEthDst(infos.get(infos.size() - 1).macAddress);
                }
                else {
                    selector.matchEthDst(infos.get(1).macAddress);
                }
                MatchInfo = MatchInfo + ETH_DST + " ";
                break;
            }
            case ETH_SRC: {
                ATFG.SetMatchFieldFlag(AutoTestFlag.PingType.ICMPing);
                ATFG.EnableProxyARP();
                if (ATFG.GetFlagsUseDummySrcMac()) {
                    selector.matchEthSrc(infos.get(infos.size() - 1).macAddress);
                }
                else {
                    selector.matchEthSrc(infos.get(0).macAddress);
                }
                MatchInfo = MatchInfo + ETH_SRC + " ";
                break;
            }
            case ETH_TYPE: {
                ATFG.SetMatchFieldFlag(AutoTestFlag.PingType.ICMPing);
                ATFG.EnableProxyARP();

                selector.matchEthType(Ethernet.TYPE_IPV4);
                MatchInfo = MatchInfo + ETH_TYPE + " ";
                break;
            }
            case IP_ECN: {
                ATFG.SetMatchFieldFlag(AutoTestFlag.PingType.TCPing);
                ATFG.EnableProxyARP();

                if (!ATFG.MatchIPV4()) {
                    selector.matchEthType(Ethernet.TYPE_IPV4);

                    DependentInfo += "matchEthType = IPV4";
                }
                // selector.matchIPEcn()
                break;
            }
            case IP_PROTO: {
                ATFG.SetMatchFieldFlag(AutoTestFlag.PingType.ICMPing);
                ATFG.EnableProxyARP();

                if (!ATFG.MatchIPV4()) {
                    selector.matchEthType(Ethernet.TYPE_IPV4);

                    DependentInfo += "matchEthType = IPV4";
                }
                selector.matchIPProtocol(IPv4.PROTOCOL_ICMP);
                MatchInfo = MatchInfo + IP_PROTO + " ";
                break;
            }
            case IP_SRC:
                ATFG.SetMatchFieldFlag(AutoTestFlag.PingType.ICMPing);
                ATFG.EnableProxyARP();

                if (!ATFG.MatchIPV4()) {
                    selector.matchEthType(Ethernet.TYPE_IPV4);

                    DependentInfo += "matchEthType = IPV4";
                }
               //selector.matchIPSrc(infos.get(0).machineIp.toIpPrefix());

                if (ATFG.GetFlagsUseDummySrcIp()) {
                    selector.matchIPSrc(infos.get(infos.size() - 1).machineIp.toIpPrefix());
				    log.info(ANSI.RED + "macth : srcIP = {}" + ANSI.RESET, infos.get(infos.size() - 1).machineIp.toIpPrefix());
                }
                else {
                    selector.matchIPSrc(infos.get(0).machineIp.toIpPrefix());
				    log.info(ANSI.RED + "macth : srcIP = {}" + ANSI.RESET, infos.get(0).machineIp.toIpPrefix());
                }
                MatchInfo = MatchInfo + IP_SRC + " ";
                break;
            case IP_DST: {
                ATFG.SetMatchFieldFlag(AutoTestFlag.PingType.ICMPing);
                ATFG.EnableProxyARP();

                if (!ATFG.MatchIPV4()) {
                    selector.matchEthType(Ethernet.TYPE_IPV4);

                    DependentInfo += "matchEthType = IPV4";
                }
                //selector.matchIPDst(infos.get(1).machineIp.toIpPrefix());
                if (ATFG.GetFlagsUseDummyDstIp()) {
                    selector.matchIPDst(infos.get(infos.size() - 1).machineIp.toIpPrefix());
				    log.info(ANSI.RED + "macth : dstIP = {}" + ANSI.RESET, infos.get(infos.size() - 1).machineIp.toIpPrefix());
                }
                else {
                    selector.matchIPDst(infos.get(1).machineIp.toIpPrefix());
				    log.info(ANSI.RED + "macth : dstIP = {}" + ANSI.RESET, infos.get(1).machineIp.toIpPrefix());
                }
                MatchInfo = MatchInfo + IP_DST + " ";
                break;
            }
            case ARP_OP: {
                ATFG.SetMatchFieldFlag(AutoTestFlag.PingType.ARPing);
                ATFG.DisableProxyARP();

                if (!ATFG.MatchARP()) {
                    selector.matchEthType(Ethernet.TYPE_ARP);

                    DependentInfo += "matchEthType = ARP";
                }
                selector.matchArpOp(ARP.OP_REQUEST);
                MatchInfo = MatchInfo + ARP_OP + " ";
                break;
            }
            case ARP_SHA: {
                ATFG.SetMatchFieldFlag(AutoTestFlag.PingType.ARPing);
                ATFG.DisableProxyARP();

                if (!ATFG.MatchARP()) {
                    selector.matchEthType(Ethernet.TYPE_ARP);

                    DependentInfo += "matchEthType = ARP";
                }
                selector.matchArpSha(infos.get(0).macAddress);
                MatchInfo = MatchInfo + ARP_SHA + " ";
                break;
            }
            case ARP_SPA: {
                ATFG.SetMatchFieldFlag(AutoTestFlag.PingType.ARPing);
                ATFG.DisableProxyARP();

                if (!ATFG.MatchARP()) {
                    selector.matchEthType(Ethernet.TYPE_ARP);

                    DependentInfo += "matchEthType = ARP";
                }
                selector.matchArpSpa(infos.get(0).machineIp);
                MatchInfo = MatchInfo + ARP_SPA + " ";
                break;
            }
            case ARP_THA: {
                ATFG.SetMatchFieldFlag(AutoTestFlag.PingType.ARPing);
                ATFG.DisableProxyARP();

                if (!ATFG.MatchARP()) {
                    selector.matchEthType(Ethernet.TYPE_ARP);

                    DependentInfo += "matchEthType = ARP";
                }
                selector.matchArpTha(MacAddress.BROADCAST);
                MatchInfo = MatchInfo + ARP_THA + " ";
                break;
            }
            case ARP_TPA: {
                ATFG.SetMatchFieldFlag(AutoTestFlag.PingType.ARPing);
                ATFG.DisableProxyARP();

                if (!ATFG.MatchARP()) {
                    selector.matchEthType(Ethernet.TYPE_ARP);

                    DependentInfo += "matchEthType = ARP";
                }
                selector.matchArpTpa(infos.get(1).machineIp);
                MatchInfo = MatchInfo + ARP_TPA + " ";
                break;
            }
            case TCP_SRC: {
                ATFG.SetMatchFieldFlag(AutoTestFlag.PingType.TCPing);
                ATFG.EnableProxyARP();

                if (!ATFG.MatchIPV4()) {
                    selector.matchEthType(Ethernet.TYPE_IPV4).matchIPProtocol(IPv4.PROTOCOL_TCP);

                    DependentInfo += "matchEthType = IPV4, matchIPProtocol = TCP";
                }
                if (ATFG.GetFlagsUseDummySrcPort()) {
                    selector.matchTcpSrc(TpPort.tpPort(9610));
                }
                else {
                    selector.matchTcpSrc(TpPort.tpPort(7650));
                }
                MatchInfo = MatchInfo + TCP_SRC + " ";
                break;
            }
            case TCP_DST: {
                ATFG.SetMatchFieldFlag(AutoTestFlag.PingType.TCPing);
                ATFG.EnableProxyARP();

                if (!ATFG.MatchIPV4()) {
                    selector.matchEthType(Ethernet.TYPE_IPV4).matchIPProtocol(IPv4.PROTOCOL_TCP);

                    DependentInfo += "matchEthType = IPV4, matchIPProtocol = TCP";
                }
                if (ATFG.GetFlagsUseDummyDstPort()) {
                    selector.matchTcpDst(TpPort.tpPort(3460));
                }
                else {
                    selector.matchTcpDst(TpPort.tpPort(2830));
                }
                MatchInfo = MatchInfo + TCP_DST + " ";
                break;
            }
            case UDP_SRC: {
                ATFG.SetMatchFieldFlag(AutoTestFlag.PingType.UDPing);
                ATFG.EnableProxyARP();

                if (!ATFG.MatchIPV4()) {
                    selector.matchEthType(Ethernet.TYPE_IPV4).matchIPProtocol(IPv4.PROTOCOL_UDP);

                    DependentInfo += "matchEthType = IPV4, matchIPProtocol = UDP";
                }
                if (ATFG.GetFlagsUseDummySrcPort()) {
                    selector.matchUdpSrc(TpPort.tpPort(9610));
                }
                else {
                    selector.matchUdpSrc(TpPort.tpPort(7650));
                }
                MatchInfo = MatchInfo + UDP_SRC + " ";
                break;
            }
            case UDP_DST: {
                ATFG.SetMatchFieldFlag(AutoTestFlag.PingType.UDPing);
                ATFG.EnableProxyARP();

                if (!ATFG.MatchIPV4()) {
                    selector.matchEthType(Ethernet.TYPE_IPV4).matchIPProtocol(IPv4.PROTOCOL_UDP);

                    DependentInfo += "matchEthType = IPV4, matchIPProtocol = UDP";
                }
                if (ATFG.GetFlagsUseDummyDstPort()) {
                    selector.matchUdpDst(TpPort.tpPort(3460));
                }
                else {
                    selector.matchUdpDst(TpPort.tpPort(2830));
                }
                MatchInfo = MatchInfo + UDP_DST + " ";
                break;
            }
            case SCTP_SRC: {
                ATFG.SetMatchFieldFlag(AutoTestFlag.PingType.SCTPing);
                ATFG.EnableProxyARP();

                if (!ATFG.MatchIPV4()) {
                    selector.matchEthType(Ethernet.TYPE_IPV4).matchIPProtocol((byte) 132);

                    DependentInfo += "matchEthType = IPV4, matchIPProtocol = SCTP";
                }
                if (ATFG.GetFlagsUseDummySrcPort()) {
                    selector.matchSctpSrc(TpPort.tpPort(9610));
                }
                else {
                    selector.matchSctpSrc(TpPort.tpPort(7650));
                }
                MatchInfo = MatchInfo + SCTP_SRC + " ";
                break;
            }
            case SCTP_DST: {
                ATFG.SetMatchFieldFlag(AutoTestFlag.PingType.SCTPing);
                ATFG.EnableProxyARP();

                if (!ATFG.MatchIPV4()) {
                    selector.matchEthType(Ethernet.TYPE_IPV4).matchIPProtocol((byte) 132);

                    DependentInfo += "matchEthType = IPV4, matchIPProtocol = SCTP";
                }
                if (ATFG.GetFlagsUseDummyDstPort()) {
                    selector.matchTcpSrc(TpPort.tpPort(3460));
                }
                else {
                    selector.matchTcpSrc(TpPort.tpPort(2830));
                }
                MatchInfo = MatchInfo + SCTP_DST + " ";
                break;
            }
            case VLAN_ID: { // no vlan support MDFK
                // Use vlanID 283 for testing XX
                // Use vlanID 1 instead of 283
                ATFG.SetMatchFieldFlag(AutoTestFlag.PingType.ICMPing);

                if (!ATFG.MatchVlan()) {
                    selector.matchVlanId(VlanId.vlanId("1"));
                            // .matchVlanId(VlanId.vlanId((short) 1));
                            // .matchEthType(Ethernet.TYPE_VLAN)
                }
                MatchInfo = MatchInfo + VLAN_ID + " ";
                break;
            }
            default:
                log.info("------------------------");
                log.info(ANSI.RED + "No Such MatchField: {}" + ANSI.RESET, matchField);
                log.info("------------------------");
                break;
        }
    }

    public String GetMatchInfo() {
        return MatchInfo;
    }

    public String GetDependentInfo() {
        return DependentInfo;
    }

    public TrafficSelector.Builder GetSelector() {
        return selector;
    }

    public TrafficSelector build() {
        return selector.build();
    }
}
