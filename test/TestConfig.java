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

import com.fasterxml.jackson.databind.JsonNode;

import org.onlab.packet.Ip4Address;
import org.onlab.packet.MacAddress;
import org.onosproject.core.ApplicationId;
import org.onosproject.net.Host;
import org.onosproject.net.config.Config;

import nctu.winlab.test.GroupConfig.GroupPipeLine;
import nctu.winlab.test.GroupConfig_5000.Group_Info;

import java.util.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class TestConfig extends Config<ApplicationId> {

	//private final Logger log = LoggerFactory.getLogger(getClass());

	private static final String FIELD = "matchfield";
	private static final String TREATMENT = "treatment";
	private static final String TESTCASE = "testcase";
	private static final String PIPELINE = "pipeline";

	private static final String GROUPID = "groupid";
	private static final String GROUPTYPE = "grouptype";
	private static final String BUCKET = "buckets";

	//public Map<Integer, String> groutType = new HashMap<>();

	public GroupConfig groupConfig;
	public GroupConfig_5000 groupConfig_5000;
	public BasicConfig basicConfig;

	public List<MachineInfo> machineInfos = new ArrayList<>();

	public String type;
	public int case_num;

	//希望更動，通通用arraylist<MachineInfo>來存，陣列[0] => src, 陣列[1] => dst1, 陣列[2] => dst2
	public void parseHostInfo() {
		// log.info(node.asText());
		switch (type) {
			case "BasicTest": {
				JsonNode j = node.get("src_host");
				String iface[] = j.path("iface").asText().split(":"),
					ifaceV[] = j.path("ifaceV").asText().split(":");
				machineInfos.add(
					new MachineInfo(
						j.path("id").asInt(), 
						iface[0],
						iface[1],
						ifaceV[0],
						ifaceV[1]
				));
				j = null;
				Iterator<JsonNode> dsts = node.get("dst_host").elements();
				while (dsts.hasNext()) {
					j = dsts.next();
					iface = j.path("iface").asText().split(":");
					ifaceV = j.path("ifaceV").asText().split(":");
					machineInfos.add(
						new MachineInfo(
							j.path("id").asInt(), 
							iface[0],
							iface[1],
							ifaceV[0],
							ifaceV[1]
						)
					);
				}
				j = null;
				break;
			}
			case "GroupTest": {
				JsonNode j = node.get("src_host");
				String iface[] = j.path("iface").asText().split(":"),
					ifaceV[] = j.path("ifaceV").asText().split(":");
				machineInfos.add(new MachineInfo(j.path("id").asInt(), iface[0], iface[1], ifaceV[0], ifaceV[1]));
				j = null;
				Iterator<JsonNode> dsts = node.get("dst_host").elements();
				while (dsts.hasNext()) {
					j = dsts.next();
					iface = j.path("iface").asText().split(":");
					ifaceV = j.path("ifaceV").asText().split(":");
					machineInfos.add(new MachineInfo(j.path("id").asInt(), iface[0], iface[1], ifaceV[0], ifaceV[1]));
				}
				j = null;
				break;
			}
		}

	}

	public void parseBasicConfig() {
		basicConfig = new BasicConfig();
		Iterator<JsonNode> elements = node.get(TESTCASE).elements();
		while (elements.hasNext()) {
			JsonNode jsonNode = elements.next();
			basicConfig.newTestCase();

			Iterator<JsonNode> field = jsonNode.path(FIELD).elements();
			while (field.hasNext()) {
				basicConfig.addNewMatchField(field.next().asText());
			}

			Iterator<JsonNode> act = jsonNode.path(TREATMENT).elements();
			while (act.hasNext()) {
				basicConfig.addNewTreatment(act.next().asText());
			}

			basicConfig.finishTestCase();
		}
		case_num = basicConfig.getTestCaseSize();
	}

	public void parseGroupConfig() {
		groupConfig = new GroupConfig();
		Iterator<JsonNode> elements = node.get(TESTCASE).elements();
		while(elements.hasNext()) {
			JsonNode jsonNode = elements.next();
			groupConfig.newTestCase();

			Iterator<JsonNode> fIterator = jsonNode.path(FIELD).elements();
			while(fIterator.hasNext()) {
				groupConfig.AddMatchField(fIterator.next().asText());
			}

			Iterator<JsonNode> pIterator = jsonNode.path(PIPELINE).elements();
			while(pIterator.hasNext()) {
				JsonNode ppln = pIterator.next();
				groupConfig.AddPipeLine(
					ppln.path("tableId").asInt(), 
					ppln.path("vlanId").isMissingNode() ? 0 : ppln.path("vlanId").asInt(),
					ppln.path("pipeType").asText(),
					ppln.path("treatment").asText(),
					ppln.path("destination").isMissingNode() ? 0 : ppln.path("destination").asInt()
				);
			}

			groupConfig.caseFinish();
		}
		case_num = groupConfig.getTestCaseSize();
	}

	public void parseGroupConfig_5000() {
		groupConfig_5000 = new GroupConfig_5000();
		Iterator<JsonNode> elements = node.get(TESTCASE).elements();
		while(elements.hasNext()) { // testcase
			JsonNode jsonNode = elements.next();
			groupConfig_5000.newTestCase();

			Iterator<JsonNode> tmpIterator = jsonNode.path(FIELD).elements();
			while(tmpIterator.hasNext()) { // matchfield
				groupConfig_5000.AddMatchField(tmpIterator.next().asText());
			}
					// ppln.path("tableId").asInt(), 
					// ppln.path("pipeType").asText(),

			int gid = jsonNode.path(GROUPID).asInt();
			String gtype = jsonNode.path(GROUPTYPE).asText();

			tmpIterator = jsonNode.path(BUCKET).elements();

			ArrayList<String> bucket = new ArrayList<>();
			ArrayList<ArrayList<String> > action_buckets = new ArrayList<>();
			while(tmpIterator.hasNext()) { // buckets
				JsonNode bck = tmpIterator.next();
				Iterator tIterator = bck.path(TREATMENT);
				buckt.clear();
				while(tIterator.hasNext()) { //treatment
					bucket.add(tIterator.next().asText());
				}
				action_buckets.add(bucket);
			}

			groupConfig_5000.AddGroupInfo(gid, gtype, action_buckets);
			groupConfig_5000.caseFinish();
		}

		case_num = groupConfig_5000.getTestCaseSize();
	}

	public void getType() {
		JsonNode jsonNode = node.get("testType");
		type = jsonNode.asText();
	}

	public Ip4Address getIpInfo(int index) {
		return machineInfos.get(index).machineIp;
	}

	public Ip4Address getIpVInfo(int index) {
		return machineInfos.get(index).machineIpV;
	}

	public Host getHostInfo(int index) {
		return machineInfos.get(index).machineHost;
	}

	public void setHostInfo(int index, Host hostInfo) {
		machineInfos.get(index).machineHost = hostInfo;
	}

	public void setHostMac(int index, MacAddress mc) {
		machineInfos.get(index).macAddress = mc;
	}

	//--------------basicTest---------------//
	public Integer basicGetTestCaseMatchFieldSize(int caseCount) {
		return basicConfig.testCases.get(caseCount).matchField.size();
	}

	public String basicGetTestCaseMatchField(int caseCount, int matchCount) {
        return basicConfig.testCases.get(caseCount).getMatchField(matchCount);
    }

	public Integer basicGetTestCaseTreatmentSize(int caseCount) {
		return basicConfig.testCases.get(caseCount).treatment.size();
	}

	public String basicGetTestCaseTreatment(int caseCount, int matchCount) {
        return basicConfig.testCases.get(caseCount).getTreatment(matchCount);
    }
	
	//-------------groupTest----------------//
	// public Integer groupGetTestCaseMatchFieldSize(int caseCount) {
	// 	return groupConfig.getTestCase(caseCount).GetMatchFieldSize();
	// }

	// public String groupGetTestCaseMatchField(int caseCount, int matchCount) {
	// 	return groupConfig.getTestCase(caseCount).GetMatchField(matchCount);
	// }

	// public Integer groupGetPipeLineSize(int caseCount) {
	// 	return groupConfig.getTestCase(caseCount).GetPipeLineSize();
	// }

	// public GroupPipeLine groupGetPipeLine(int caseCount, int pipeCount) {
	// 	return groupConfig.getTestCase(caseCount).GetPipeLine(pipeCount);
	// }

	//-------------groupTest-5000----------------//
	public Integer groupGetTestCaseMatchFieldSize(int caseCount) {
		return groupConfig_5000.getTestCase(caseCount).GetMatchFieldSize();
	}

	public String groupGetTestCaseMatchField(int caseCount, int matchCount) {
		return groupConfig_5000.getTestCase(caseCount).GetMatchField(matchCount);
	}

	public Integer groupGetPipeLineSize(int caseCount) {
		return groupConfig_5000.getTestCase(caseCount).GetGroupInfoSize();
	}

	public GroupPipeLine groupGetPipeLine(int caseCount, int pipeCount) {
		return groupConfig_5000.getTestCase(caseCount).GetGroupInfo(pipeCount);
	}
}
