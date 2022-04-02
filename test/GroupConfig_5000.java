package nctu.winlab.test;

import java.util.ArrayList;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

//after eatup config file, it create a instance of groupconfig in testconfig.java
//then for each test case, it store a class GroupTestCase in testCases.
//each GroupTestCase stores group type & buckets.

public class GroupConfig_5000 {
	//private final Logger log = LoggerFactory.getLogger(getClass());
    private ArrayList<GroupTestCase> testCases = new ArrayList<>();
    private GroupTestCase tempCase;
    public void newTestCase() {
        tempCase = new GroupTestCase();
    }

    public void AddMatchField(String mf) {
        tempCase.NewMatchField(mf);
    }

    public void AddGroupInfo(int gid, String gtype, ArrayList<ArrayList<String> > action_buckets) {
        tempCase.NewGroupInfo(gid, gtype, action_buckets);
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
        private ArrayList<Group_Info> group_infos = new ArrayList<>();

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

        public int GetGroupInfoSize() {
            return group_infos.size();
        }

        public Group_Info GetGroupInfo(int i) {
            return group_infos.get(i);
        }

        public void NewGroupInfo(int gid, String gtype, ArrayList<ArrayList<String> > action_buckets) {
            group_infos.add(new Group_Info(gid, gtype, action_buckets));
        }
    }
    
    public class Group_Info {
        public int groupid;
        public String grouptype;
        public ArrayList<ArrayList<String> > buckets = new ArrayList<>();

        public Group_Info(int gid, String gtype, ArrayList<ArrayList<String> > action_buckets) {
            this.groupid = gid;
            this.grouptype = gtype;
            this.buckets.addAll(action_buckets);
        }
    }
}
