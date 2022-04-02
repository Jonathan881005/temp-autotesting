package nctu.winlab.test;

import java.util.TimerTask;

import org.onosproject.core.ApplicationId;
import org.onosproject.core.GroupId;
import org.onosproject.net.DeviceId;
import org.onosproject.net.flow.DefaultTrafficTreatment;
import org.onosproject.net.flow.FlowRuleEvent;
import org.onosproject.net.flow.FlowRuleListener;
import org.onosproject.net.flow.FlowRuleService;
import org.onosproject.net.flow.TrafficSelector;
import org.onosproject.net.flow.TrafficTreatment;
import org.onosproject.net.flowobjective.FlowObjectiveService;
import org.onosproject.net.flowobjective.DefaultForwardingObjective;
import org.onosproject.net.flowobjective.ForwardingObjective;
import org.onosproject.net.group.DefaultGroupBucket;
import org.onosproject.net.group.DefaultGroupDescription;
import org.onosproject.net.group.DefaultGroupKey;
import org.onosproject.net.group.GroupBucket;
import org.onosproject.net.group.GroupBuckets;
import org.onosproject.net.group.GroupDescription;
import org.onosproject.net.group.GroupEvent;
import org.onosproject.net.group.GroupKey;
import org.onosproject.net.group.GroupListener;
import org.onosproject.net.group.GroupService;
import org.onosproject.net.host.HostService;
import org.onosproject.net.packet.PacketService;
import org.onosproject.net.topology.TopologyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nctu.winlab.test.GroupConfig_5000.Group_Info;
import nctu.winlab.test.GroupConfig_5000.GroupTestCase;

import java.util.*;


import org.onosproject.net.flow.DefaultTrafficSelector;
import org.onosproject.net.flow.TrafficSelector;


public class GroupTest {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final FlowRuleListener rInstallListener = new InstallListener();
	private final FlowRuleListener rDeleteListener = new DeleteListener();
	private final GroupListener gInstallListener = new GroupInstallListener();
	private final GroupListener gDeleteListener = new GroupDeleteListener();

	protected FlowObjectiveService flowObjectiveService;
	protected PacketService packetService;
	protected HostService hostService;
	protected FlowRuleService flowRuleService;
	protected TopologyService topologyService;
	protected GroupService groupService;
	protected DefaultGroupDescription defaultGroupService;

	protected int case_num;
	protected int testcase = 0, groupCount = 0;
	protected AutoTestFlag ATFG = new AutoTestFlag();
	protected GroupReport gr;

	protected Timer installTimer;

	protected ApplicationId appId;
	protected TestConfig testConfig;

	protected MatchField mf;
	protected Treatment tt;

	protected ForwardingObjective fb;

	public void getService(HostService hService, FlowRuleService frService, TopologyService tService,
			FlowObjectiveService fService, GroupService gService, ApplicationId id) {
		hostService = hService;
		flowRuleService = frService;
		topologyService = tService;
		groupService = gService;
		flowObjectiveService = fService;
		appId = id;
	}

	public void getConfig(TestConfig config) {
		testConfig = config;
		testConfig.parseHostInfo();
		testConfig.parseGroupConfig_5000();
		testcase = 0;
		case_num = config.case_num;
		gr = new GroupReport();
		gr.SetModel("DXS-5000");

		for (int k = 0; k < testConfig.groupConfig_5000.getTestCaseSize(); k++) {
			log.info(ANSI.GREEN + "testcase {}" + ANSI.RESET, k);
			for (int z = 0; z < testConfig.groupGetTestCaseMatchFieldSize(k); z++) {
				log.info(ANSI.BLUE + "{}" + ANSI.RESET, testConfig.groupGetTestCaseMatchField(k, z));
			}
		}

		// host0 = src, host1 = dst1, host2 = dst2.....
		for (int k = 0; k < testConfig.machineInfos.size(); k++) {
			if (!TopoChecker.Check(k, testConfig, hostService)) {
				log.info(ANSI.RED + "This topology is not correct." + ANSI.RESET);
				return;
			}
		}
		testConfig.machineInfos.add(new MachineInfo(2, "10.28.3.83", "10.28.3.84", "98:E7:9A:27:FE:49"));

	}

	public class GroupInstallListener implements GroupListener {
		@Override
		public void event(GroupEvent event) {
			if (event.subject().appId().equals(appId) && event.type().equals(GroupEvent.Type.GROUP_ADDED)) {
				//installTimer.cancel();
				log.info(ANSI.RED + "Group Install Triggered" + ANSI.RESET);
				if (groupCount < testConfig.groupConfig.getTestCase(testcase).GetGroupInfoSize()) {
					installGroup(testcase, groupCount);
				} else {
					groupService.removeListener(gInstallListener);
					installFlowRule(testcase);
				}
			}
		}
	}

	public class GroupDeleteListener implements GroupListener {
		@Override
		public void event(GroupEvent event) {
			if (event.subject().appId().equals(appId) && event.type().equals(GroupEvent.Type.GROUP_REMOVED)) {
				groupCount--;
				if (groupCount == 0) {
					groupService.removeListener(gDeleteListener);
					try {
						Thread.sleep(2500);
					} catch (Exception e) {
						log.info(e.toString());
					}
					InstallTestingRules();
				}
			}
		}
	}

	public class InstallListener implements FlowRuleListener {
		@Override
		public void event(FlowRuleEvent event) {
			// other case
			if (event.type() == FlowRuleEvent.Type.RULE_ADDED && event.subject().appId() == appId.id()) {
				log.info(ANSI.GREEN + "INSTALL TRIGGERED" + ANSI.RESET);

				//installTimer.cancel();

				List<SSHComponent> ls = new ArrayList<>();

				SSHComponent src = new SSHComponent();

				ATFG.ReverseArray();

				SSHComponent z = new SSHComponent();
				// int dst = testConfig.groupGetPipeLine(testcase, k - 1).destination;
				// int dstHost = testConfig.machineInfos.get(dst).machineId;
				int dstHost = 1;
				String rec_cmd = "onos localhost sshctl -t " + String.valueOf(dstHost) + " exec "
						+ ATFG.GetDumpCommand(testConfig.machineInfos, dst);

				z.StartUpConnection(rec_cmd);
				ls.add(z);
				// zzz

				try {
					Thread.sleep(3000);
				} catch (Exception e) {
				}

				String send_cmd = "onos localhost sshctl -t 0 exec " + ATFG.GetPingCommand(testConfig.machineInfos);
				src.StartUpConnection(send_cmd);

				try {
					Thread.sleep(3000);
				} catch (Exception e) {
				}

				Boolean flag = false;

				for (int k = 0; k < ls.size(); k++) {
					if (ls.get(k).CheckIsStillAlive()) {
						flag = true;
						break;
					} else {
						log.info(ANSI.RED + "{}" + ANSI.RESET, ls.get(k).Result());
					}
				}

				if (flag) {
					log.info("Failure!");
					gr.SetCaseResult(false);
				} else {
					log.info("Success!");
					gr.SetCaseResult(true);
				}

				gr.FinishCaseReport();

				testcase++;

				log.info(ANSI.BLUE + "removing rules.." + ANSI.RESET);
				flowRuleService.removeListener(rInstallListener);
				flowRuleService.addListener(rDeleteListener);
				flowRuleService.removeFlowRulesById(appId);
			}
		}
	}

	public class DeleteListener implements FlowRuleListener {
		@Override
		public void event(FlowRuleEvent event) {
			if (event.type() == FlowRuleEvent.Type.RULE_REMOVED && event.subject().appId() == appId.id()) {
				log.info(ANSI.GREEN + "DELETE TRIGGERED" + ANSI.RESET);
				flowRuleService.removeListener(rDeleteListener);
				groupService.addListener(gDeleteListener);
				groupService.purgeGroupEntries();
			}
		}
	}

	public class TimerDelay extends TimerTask {
		@Override
		public void run() {
			// Put the field into failed list.
			testcase++;
			log.info(ANSI.RED + "removing rules.." + ANSI.RESET);
			flowRuleService.removeFlowRulesById(appId);
		}
	}

	public void InstallTestingRules() {
		mf = new MatchField();
		tt = new Treatment();
		ATFG = new AutoTestFlag();
		groupCount = 0;

		if (testcase < case_num) {

			log.info(ANSI.BLUE + "Start testcase {}..." + ANSI.RESET, testcase);
			groupService.addListener(gInstallListener);
			// ----------Install GroupBucket----------
			gr.NewCaseReport();
			installGroup(testcase, groupCount);
		}
		else {
			log.info(ANSI.GREEN + "All Testcases finished. Writing result..." + ANSI.RESET);
			StoreResult.GroupReport(gr);
		}
	}

	public void BuildFlowObjective(TrafficSelector.Builder selector, TrafficTreatment.Builder treatment,
			DeviceId configDeviceId) {

		ForwardingObjective forwardingObjective;
		forwardingObjective = DefaultForwardingObjective.builder().withSelector(selector.build())
				.withTreatment(treatment.build()).withPriority(50000).withFlag(ForwardingObjective.Flag.VERSATILE)
				.fromApp(appId).makePermanent().add();
		flowObjectiveService.forward(configDeviceId, forwardingObjective);
	}

	private void installGroup(int caseCount, int groupCount) {
		Group_Info gi = testConfig.groupConfig_5000.getTestCase(caseCount).GetGroupInfo(groupCount);
		printGroupInfo(gi);
		installGroupRule(gi, testConfig.machineInfos);
		groupCount++;
	}

	public void installFlowRule(int tid) {
		GroupTestCase gtc = testConfig.groupConfig.getTestCase(tid);
		for (int z = 0; z < gtc.GetMatchFieldSize(); z++) {
			mf.newMatchField(gtc.GetMatchField(z), testConfig.machineInfos, ATFG);
		}

		TrafficTreatment.Builder ttl = DefaultTrafficTreatment.builder()
				.group(GroupId.valueOf(lastUsedGid.get(lastUsedGid.size() - 1)));
				
		DeviceId dId = testConfig.machineInfos.get(1).machineHost.location().deviceId();

		log.info(ANSI.BLUE + "MatchField = {}" + ANSI.RESET, gtc.GetMatchField(0));
		log.info(ANSI.BLUE + "GroupId = {}" + ANSI.RESET, GroupId.valueOf(lastUsedGid.get(lastUsedGid.size() - 1)));
		log.info(ANSI.BLUE + "dId = {}" + ANSI.RESET, dId.toString());

		flowRuleService.addListener(rInstallListener);

		BuildFlowObjective(mf.GetSelector(), ttl, dId);
	}

	private void installGroupRule(Group_Info gi, List<MachineInfo> infos) {
		GroupKey gk = new DefaultGroupKey();

		// Create a GroupBucketList
		List<GroupBucket> gbl = new ArrayList<>();
		if(infos.grouptype == "Indirect"){
			if(infos.action_buckets.size() > 1){ // Buckets number error handle
				log.info(ANSI.RED + "Indirect group should have not {} but 1 bucket." + ANSI.RESET, infos.action_buckets.size());
				// do something to report
				return;
			}
			ArrayList <String> bucket(infos.action_buckets[0]);
			Treatment tt = new Treatment();
			for(int j = 0; j < bucket.size(); j++){
				tt.NewTreatment(bucket(j), infos, ATFG);
			}
			GroupBucket gb = DefaultGroupBucket.createIndirectGroupBucket(tt.GetTreatment().build());
			gbl.add(gb);
		} 
		else if(infos.grouptype == "All"){
			for(int i = 0; i < infos.action_buckets.size(); i++){
				ArrayList <String> bucket(infos.action_buckets[i]);
				Treatment tt = new Treatment();
				for(int j = 0; j < bucket.size(); j++){
					tt.NewTreatment(bucket(j), infos, ATFG);
				}
				GroupBucket gb = DefaultGroupBucket.createAllGroupBucket(tt.GetTreatment().build());
				gbl.add(gb);
			}
		}
		else{ // Type error handle
			log.info(ANSI.RED + "Doesn't support grouptype except \"Indirect\" and \"All\"" + ANSI.RESET);
			// do something to report
			return;
		}

		GroupBuckets gbs = new GroupBuckets(gbl);


		gr.AddCaseUsedGroup(infos.groupid, gi.grouptype);
		// shoule be modified later

		DefaultGroupDescription dGroupDescription = new DefaultGroupDescription(
				infos.get(1).machineHost.location().deviceId(), GroupDescription.Type.valueOf(infos.grouptype), gbs, gk,
				infos.groupid, appId);
		groupService.addGroup(dGroupDescription);
	}


	private void printGroupInfo(Group_Info gi) {
		log.info(ANSI.YELLOW + "grouptype: {}" + ANSI.RESET, gi.grouptype);
		
	}
}
