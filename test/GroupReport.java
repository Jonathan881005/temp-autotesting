package nctu.winlab.test;

import java.util.*;

import com.google.common.collect.ImmutableList;

public class GroupReport {
    protected String model;
    protected List<GroupCaseReport> testcases = new ArrayList<>();
    protected GroupCaseReport tgcr;
    enum groupEnum {
        OUTPUT, FORWARD;
    }

    public void SetModel(String modelName) {
        model = modelName;
    }

    public void NewCaseReport() {
        tgcr = new GroupCaseReport();
    }

    public void SetCaseResult(Boolean r) {
        tgcr.result = r;
    }

    public void AddCaseUsedGroup(int gid, int vid, String ptype, String tIp, ArrayList<Integer> tGroup) {
        tgcr.newGroup(gid, vid, ptype, tIp, tGroup);
    }

    public void FinishCaseReport() {
        testcases.add(tgcr);
    }

    public class GroupCaseReport {
        protected Boolean result;
        protected List<UsingGroups> usedGroups = new ArrayList<>();

        public void newGroup(int gid, int vid, String ptype, String tIp, ArrayList<Integer> tGroup) {
            UsingGroups ug;
            if (tIp != null) {
                ug = new UsingGroups(gid, vid, ptype, tIp);
            }
            else {
                ug = new UsingGroups(gid, vid, ptype, tGroup);
            }
            usedGroups.add(ug);
        }
    }

    public class UsingGroups {
        protected groupEnum gEnum;
        protected int groupId;
        protected int vlanId;
        protected String pipeType;
        protected String targetIp;
        protected List<Integer> targetGroup;
        //protected 
        public UsingGroups(int gid, int vid, String ptype, String tIp) {
            this.groupId = gid;
            this.vlanId = vid;
            this.pipeType = ptype;
            this.targetIp = tIp;
            this.gEnum = groupEnum.OUTPUT;
        }

        public UsingGroups(int gid, int vid, String ptype, ArrayList<Integer> tGroup) {
            this.groupId = gid;
            this.vlanId = vid;
            this.pipeType = ptype;
            this.targetGroup = ImmutableList.copyOf(tGroup);
            this.gEnum = groupEnum.FORWARD;
        }

        public String GenerateReportLine() {
            switch (gEnum) {
                case OUTPUT:
                    return String.format("Using groupId: %s, vlanId: %s, groupType: %s, OUTPUT Packet to iface: %s", groupId, vlanId, pipeType, targetIp);
                case FORWARD:
                    return String.format("Using groupId: %s, vlanId: %s, groupType: %s, FORWARD Packets to group: %s", groupId, vlanId, pipeType, targetGroup.toString());
                default:
                    return "";
            }
        }

    }
}
