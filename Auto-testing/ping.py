#! /usr/bin/env python3

from asyncio.windows_events import NULL
import sys
from scapy.all import *

#print(sys.argv)

if sys.argv[1] == "flagARPING":
    arping(sys.argv[2], iface = sys.argv[3])

if sys.argv[1] == "flagPING":
    sendp(Ether(src = sys.argv[2], dst = sys.argv[3])/IP(src = sys.argv[4], dst = sys.argv[5])/ICMP(), iface = sys.argv[6])

if sys.argv[1] == "flagTCPING":
    sendp(Ether(src = sys.argv[2], dst = sys.argv[3])/IP(src = sys.argv[4], dst = sys.argv[5])/TCP(sport=int(sys.argv[6]),dport=int(sys.argv[7]), flags = "SEC"), iface = sys.argv[8])

if sys.argv[1] == "flagUDPING":
    sendp(Ether(src = sys.argv[2], dst = sys.argv[3])/IP(src = sys.argv[4], dst = sys.argv[5])/UDP(sport=int(sys.argv[6]),dport=int(sys.argv[7])), iface = sys.argv[8])

if sys.argv[1] == "flagSCTPING":
    sendp(Ether(src = sys.argv[2], dst = sys.argv[3])/IP(src = sys.argv[4], dst = sys.argv[5])/SCTP(sport=int(sys.argv[6]),dport=int(sys.argv[7])), iface = sys.argv[8])
