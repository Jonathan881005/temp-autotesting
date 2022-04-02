package nctu.winlab.test;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreResult {
	private final Logger log = LoggerFactory.getLogger(getClass());
	//記得把這邊改成static然後用basic report那個class重寫= =
    public void Report(Map<Integer, String> testCaseStr, Map<Integer, String> depMap, List<String> Suc, List<String> Fail, String testType) {
        log.info(ANSI.BLUE + "Showing result of testcases and generating report....." + ANSI.RESET);

		try {
			String home = System.getProperty("user.home");

			File dir = new File(home + "/report");

			if (!dir.exists()) {
				dir.mkdirs();
			}

			String FileName = testType + "-report-" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + ".csv";
			FileWriter testResult = new FileWriter(dir + "/" + FileName);
			log.info(ANSI.BLUE + "Report file generated. Writing content..." + ANSI.RESET);
			testResult.write("TestCase, TestResult, Dependency1, Dependency2, Dependency3\n");

			log.info(ANSI.GREEN + "Successful testcases:" + ANSI.RESET);
			for (int i = 0; i < Suc.size(); i++) {
				log.info(ANSI.BLUE + "{}" + ANSI.RESET, testCaseStr.get(Integer.parseInt(Suc.get(i))));
				String ts = testCaseStr.get(Integer.parseInt(Suc.get(i))),
						dp = depMap.get(Integer.parseInt(Suc.get(i)));
				testResult.write(ts + ", success, " + dp + "\n");

			}

			log.info(ANSI.RED + "Failed testcases:" + ANSI.RESET);
			for (int i = 0; i < Fail.size(); i++) {
				log.info(ANSI.BLUE + "{}" + ANSI.RESET, testCaseStr.get(Integer.parseInt(Fail.get(i))));
				String ts = testCaseStr.get(Integer.parseInt(Fail.get(i))),
						dp = depMap.get(Integer.parseInt(Fail.get(i)));
				testResult.write(ts + ", failed, " + dp + "\n");

			}

			testResult.close();
		} catch (IOException e) {
			log.info(e.toString());
		}
    }

	public static void GroupReport(GroupReport gReport) {
		try {
			String home = System.getProperty("user.home");

			File dir = new File(home + "/report");

			if (!dir.exists()) {
				dir.mkdirs();
			}

			String FileName = "GroupReport-" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + ".log";
			FileWriter testResult = new FileWriter(dir + "/" + FileName);

			testResult.write("Model: " + gReport.model + "\n");

			for (int k = 0; k < gReport.testcases.size(); k++ ){ 
				testResult.write("------Start testcase " + String.valueOf(k) + "------\n");

				testResult.write("Testcase: " + String.valueOf(k) + ", Test result: " + (gReport.testcases.get(k).result ? "Success" : "Failed") + "\n");
				testResult.write("Using pipeline: \n");
				for (int z = 0; z < gReport.testcases.get(k).usedGroups.size(); z ++) {
					testResult.write(gReport.testcases.get(k).usedGroups.get(z).GenerateReportLine() + "\n");
				}
				testResult.write("------ End testcase " + String.valueOf(k) + " ------\n\n");
			}

			testResult.close();
		} catch (IOException e) {
		}		
	}
}
