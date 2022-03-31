package nctu.winlab.test;

import java.util.*;

public class BasicConfig {
    protected List<BasicTestCase> testCases = new ArrayList<>();
    protected BasicTestCase btc;
    
    public void newTestCase() {
        btc = new BasicTestCase();
    }

    public void addNewMatchField(String matchfieldStr) {
        btc.matchField.add(matchfieldStr);
    }

    public void addNewTreatment(String treatmentStr) {
        btc.treatment.add(treatmentStr);
    }

    public void finishTestCase() {
        testCases.add(btc);
    }

    public int getTestCaseSize() {
        return testCases.size();
    }

    public class BasicTestCase {
        public List<String> matchField = new ArrayList<>();
        public List<String> treatment = new ArrayList<>();

        public String getMatchField(int index) {
            return matchField.get(index);
        }

        public String getTreatment(int index) {
            return treatment.get(index);
        }
    }
}
