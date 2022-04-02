/*
 * Copyright 2020-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nctu.winlab.test;

import org.onosproject.net.packet.*;

import org.onosproject.net.Host;
import org.onosproject.net.host.HostService;
import org.onosproject.net.DeviceId;

import org.onosproject.core.ApplicationId;

import org.onosproject.net.flowobjective.*;

import org.onosproject.net.flow.*;

import org.onosproject.net.topology.TopologyService;
import org.onosproject.net.Link;
import org.onosproject.net.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import java.lang.Thread;
import java.lang.Exception;

/**
 * Skeletal ONOS application component.
 */

public class BasicTest {

	private final Logger log = LoggerFactory.getLogger(getClass());
	private final FlowRuleListener ruleListener = new InstallListener();
	private final FlowRuleListener deleteListener = new DeleteListener();

	protected FlowObjectiveService flowObjectiveService;
	protected PacketService packetService;
	protected HostService hostService;
	protected FlowRuleService flowRuleService;
	protected TopologyService topologyService;

	protected int case_num;
	protected int testcase = 0;
	protected Map<Integer, String> testCaseStr = new HashMap<>();
	protected Map<Integer, String> dependencyMap = new HashMap<>();
	protected List<String> success = new ArrayList<>();
	protected List<String> fail = new ArrayList<>();
	protected Timer timer;
	protected Timer install_timer;
	protected StoreResult sr = new StoreResult();
	protected TestConfig testConfig;
	protected AutoTestFlag ATFG;

	protected int counting = 0;
	protected int del_counting = 0;

	protected ApplicationId appId;

	protected MatchField mf;
	protected Treatment tt;

	protected Host src_host;
	protected Host dst_host;
	protected Path path;
	protected List<Link> link = new ArrayList<>();

	protected Boolean finished;

	public void addListener() {
		flowRuleService.addListener(ruleListener);
		flowRuleService.addListener(deleteListener);
	}

	public void delListener() {
		flowRuleService.removeListener(ruleListener);
		flowRuleService.removeListener(deleteListener);
	}

	public void getService(HostService hService, FlowRuleService frService, TopologyService tService,
			FlowObjectiveService foService, PacketService pService, ApplicationId id) {
		hostService = hService;
		flowRuleService = frService;
		topologyService = tService;
		flowObjectiveService = foService;
		packetService = pService;
		appId = id;
		finished = false;
		ATFG = new AutoTestFlag();
	}

	public void getConfig(TestConfig config) {
		testConfig = config;
		testConfig.parseHostInfo();
		testConfig.parseBasicConfig();

		testcase = 0;

		for (int i = 0; i < testConfig.case_num; i++) {
			log.info(ANSI.GREEN + "testcase {}" + ANSI.RESET, i);
			log.info("Matchfield");
			for (int j = 0; j < testConfig.basicGetTestCaseMatchFieldSize(i); j++) {
				log.info(ANSI.BLUE + "{}" + ANSI.RESET, testConfig.basicGetTestCaseMatchField(i, j));
			}
			log.info("Treatment");
			for (int j = 0; j < testConfig.basicGetTestCaseTreatmentSize(i); j++) {
				log.info(ANSI.BLUE + "{}" + ANSI.RESET, testConfig.basicGetTestCaseTreatment(i, j));
			}
		}
		case_num = config.case_num;

		for (int k = 0; k < testConfig.machineInfos.size(); k++) {
			if(!TopoChecker.Check(k, testConfig, hostService)) {
				log.info(ANSI.RED + "This topology is not correct." + ANSI.RESET);
			}
		}
		testConfig.machineInfos.add(new MachineInfo(2, "10.28.3.83", "10.28.3.84", "98:E7:9A:27:FE:49"));
		
	}

	public class InstallListener implements FlowRuleListener {
		@Override
		public void event(FlowRuleEvent event) {
			if (event.type() == FlowRuleEvent.Type.RULE_ADDED && event.subject().appId() == appId.id()) {
				log.info(ANSI.GREEN + "INSTALL TRIGGERED" + ANSI.RESET);
				counting++;
				if (counting == 1) {
					install_timer.cancel();
					// log.info(mf.dumpCommand(src_ip, src_ipV));
					String rec_cmd = "onos localhost sshctl -t 1 exec " + ATFG.GetDumpCommand(testConfig.machineInfos);

					SSHComponent sComponent1 = new SSHComponent();
					sComponent1.StartUpConnection(rec_cmd);

					try {
						Thread.sleep(5000);
					} catch (Exception e) {
					}

					String send_cmd = "onos localhost sshctl -t 0 exec " + ATFG.GetPingCommand(testConfig.machineInfos);

					SSHComponent sComponent2 = new SSHComponent();
					sComponent2.StartUpConnection(send_cmd);

					try {
						Thread.sleep(5000);
					} catch (Exception e) {
					}

					log.info(ANSI.BLUE + "Checking result..." + ANSI.RESET);
					if (!sComponent1.CheckIsStillAlive()) {
						log.info(ANSI.GREEN + "output result {}" + ANSI.RESET, sComponent1.Result());
						success.add(String.valueOf(testcase));
					} else {
						sComponent1.KillConnection();
						log.info(ANSI.RED + "failed!" + ANSI.RESET);
						fail.add(String.valueOf(testcase));
					}

					testcase = testcase + 1;
					counting = 0;

					log.info(ANSI.BLUE + "removing rules.." + ANSI.RESET);
					addListener();
					flowRuleService.removeFlowRulesById(appId);
				}
			}

		}
	}

	public class DeleteListener implements FlowRuleListener {
		@Override
		public void event(FlowRuleEvent event) {
			if (event.type() == FlowRuleEvent.Type.RULE_REMOVED && event.subject().appId() == appId.id()) {
				del_counting++;
				log.info(ANSI.GREEN + "DELETE TRIGGERED" + ANSI.RESET);
				log.info(ANSI.GREEN + "{}/{}" + ANSI.RESET, testcase, case_num);
				if (del_counting == 1) {
					if (testcase < case_num && !finished) {
						del_counting = 0;

						InstallTestingRules();
					} else {
						finished = true;

						Finalize();
					}
				}
			}
		}
	}
	
	// note that if flowrule failed to add, remove a "pending add" flow rule won't trigger delete listener
	public class TimerDelay extends TimerTask {
		@Override
		public void run() {
			// Put the field into failed list.
			fail.add(String.valueOf(testcase));
			testcase++;

			log.info(ANSI.RED + "removing rules.." + ANSI.RESET);

			flowRuleService.removeFlowRulesById(appId);

			if (testcase == case_num) {
				finished = true;
				Finalize();
			}
			else {
				InstallTestingRules();
			}
		}
	}

	public class InstallDelay extends TimerTask {
		@Override
		public void run() {
			log.info("This flow rule cannot be installed!");
		}
	}

	public void InstallTestingRules() {

		delListener();
		log.info(ANSI.GREEN + "InstallTestRules" + ANSI.RESET);
		addListener();

		ATFG.reset();
		mf = new MatchField();
		tt = new Treatment();

		if (testcase < case_num) {

			counting = 0;
			del_counting = 0;

			log.info(ANSI.BLUE + "Start testcase {}..." + ANSI.RESET, testcase);

			// ----------Install treatment----------- 
			for (int i = 0; i < testConfig.basicGetTestCaseTreatmentSize(testcase); i++) {
				log.info(ANSI.BLUE + "Set treatment..." + ANSI.RESET);
				tt.NewTreatment(
					testConfig.basicGetTestCaseTreatment(testcase, i), 
					testConfig.machineInfos, 
					ATFG
				);
			}

			// ----------Install match field----------
			for (int i = 0; i < testConfig.basicGetTestCaseMatchFieldSize(testcase); i++) {
				// Install match field
				log.info(ANSI.BLUE + "Install match field..." + ANSI.RESET);
				mf.newMatchField(testConfig.basicGetTestCaseMatchField(testcase, i), testConfig.machineInfos, ATFG);
			}

			testCaseStr.put(testcase, mf.GetMatchInfo());
			dependencyMap.put(testcase, mf.GetDependentInfo());

			install_timer = new Timer();
			install_timer.schedule(new TimerDelay(), 10000);

			DeviceId dId = testConfig.machineInfos.get(1).machineHost.location().deviceId();
			BuildFlowObjective(mf.selector, tt.trt, dId);
		} 
	}

	public void BuildFlowObjective(TrafficSelector.Builder selector, TrafficTreatment.Builder treatment,
			DeviceId configDeviceId) {
		ForwardingObjective forwardingObjective;
		log.info(ANSI.BLUE + "Config ObjectiveFlow..." + ANSI.RESET);
		forwardingObjective = DefaultForwardingObjective.builder().withSelector(selector.build())
				.withTreatment(treatment.build()).withPriority(50000).withFlag(ForwardingObjective.Flag.VERSATILE)
				.fromApp(appId).makeTemporary(40).add(); // t = 30
		log.info(ANSI.BLUE + "Forward ObjectiveFlow..." + ANSI.RESET);
		flowObjectiveService.forward(configDeviceId, forwardingObjective);
		log.info(ANSI.RED + "Complete Forward !!!" + ANSI.RESET);
	}

	public void Finalize() {
		del_counting = 0;
		testcase = 0;
		sr.Report(testCaseStr, dependencyMap, success, fail, "BasicTest");
		success.clear();
		fail.clear();
		testCaseStr.clear();
	}
}