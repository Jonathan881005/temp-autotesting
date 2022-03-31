#! /bin/bash

if [ $# -eq 0 ] ; then
    echo "Please specify iperf3 server"
    exit 0
else
    echo `iperf3 -c $1 | tail -4 | head -2 | awk -F " " 'END{print $7" "$8}'`
fi