package nctu.winlab.test;

import java.util.*;

import com.google.common.collect.ImmutableList;

public class GroupReport {
    protected String model;
    protected List<GroupCaseReport> testcases = new ArrayList<>();
    protected GroupCaseReport tgcr;

    public void SetModel(String modelName) {
        model = modelName;
    }

    public void NewCaseReport() {
        tgcr = new GroupCaseReport();
    }

    public void SetCaseResult(Boolean r) {
        tgcr.result = r;
    }

    public void AddCaseUsedGroup(int gid String gtype) {
        tgcr.newGroup(gid, gtype);
    }

    public void FinishCaseReport() {
        testcases.add(tgcr);
    }

    public class GroupCaseReport {
        protected Boolean result;
        protected List<UsingGroups> usedGroups = new ArrayList<>();

        public void newGroup(int gid String gtype) {
            UsingGroups ug;
            if (tIp != null) {
                ug = new UsingGroups(gid, gtype);
            }
            else {
                ug = new UsingGroups(gid, gtype);
            }
            usedGroups.add(ug);
        }
    }

    public class UsingGroups {
        protected int groupId;
        protected String grougtype;
        public UsingGroups(int gid String gtype) {
            this.groupId = gid;
            this.grougtype = gtype;
        }

        public UsingGroups(int gid String gtype) {
            this.groupId = gid;
            this.grougtype = gtype;
        }

        public String GenerateReportLine() {
                return String.format("Using groupId: %s, grougtype: %s", groupId, grougtype);
        }

    }
}
