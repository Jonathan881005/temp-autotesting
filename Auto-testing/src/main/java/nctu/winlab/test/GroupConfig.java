package nctu.winlab.test;

import java.util.ArrayList;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

//after eatup config file, it create a instance of groupconfig in testconfig.java
//then for each test case, it store a class GroupTestCase in testCases.
//each GroupTestCase stores group type & buckets.

public class GroupConfig {
	//private final Logger log = LoggerFactory.getLogger(getClass());
    private ArrayList<GroupTestCase> testCases = new ArrayList<>();
    private GroupTestCase tempCase;
    public void newTestCase() {
        tempCase = new GroupTestCase();
    }

    public void AddMatchField(String mf) {
        tempCase.NewMatchField(mf);
    }

    public void AddPipeLine(int tid, int vid, String btype, String tStr, int dst) {
        tempCase.NewPipeLine(tid, vid, btype, tStr, dst);
    }

    public void caseFinish() {
        testCases.add(tempCase);
    }

    public int getTestCaseSize() {
        return testCases.size();
    }

    public GroupTestCase getTestCase(int i) {
        return testCases.get(i);
    }

    public class GroupTestCase {
        private ArrayList<String> matchFields = new ArrayList<>();
        private ArrayList<GroupPipeLine> pipeLines = new ArrayList<>();

        public GroupTestCase() { };

        public void NewMatchField(String mf) {
            matchFields.add(mf);
        }

        public int GetMatchFieldSize() {
            return matchFields.size();
        }

        public String GetMatchField(int i) {
            return matchFields.get(i);
        }

        public int GetPipeLineSize() {
            return pipeLines.size();
        }

        public GroupPipeLine GetPipeLine(int i) {
            return pipeLines.get(i);
        }

        public void NewPipeLine(int tid, int vid, String ptype, String tStr, int dst) {
            pipeLines.add(new GroupPipeLine(tid, vid, ptype, tStr, dst));
        }
    }
    
    public class GroupPipeLine {
        public int tableid;
        public int vlanid;
        public String pipeType;
        public String treatment;
        public int destination;
        public GroupPipeLine(int tid, int vid, String pType, String tStr, int dst) {
            this.tableid = tid;
            this.vlanid = vid;
            this.pipeType = pType;
            this.treatment = tStr;
            this.destination = dst;
        }
    }
}
