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


import org.onosproject.net.host.HostService;

import static org.onosproject.net.config.NetworkConfigEvent.Type.CONFIG_ADDED;
import static org.onosproject.net.config.NetworkConfigEvent.Type.CONFIG_UPDATED;
import static org.onosproject.net.config.basics.SubjectFactories.APP_SUBJECT_FACTORY;
import org.onosproject.net.config.ConfigFactory;
import org.onosproject.net.config.NetworkConfigEvent;
import org.onosproject.net.config.NetworkConfigListener;
import org.onosproject.net.config.NetworkConfigRegistry;

import org.onosproject.net.device.DeviceService;

import org.onosproject.core.ApplicationId;

import org.onosproject.net.flowobjective.*;
import org.onosproject.net.group.GroupService;
import org.onosproject.net.flow.*;

import org.onosproject.net.topology.TopologyService;


import org.onosproject.core.CoreService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Skeletal ONOS application component.
 */
@Component(immediate = true,
           service = {AutoTesting.class},
           property = {

           })
public class AutoTesting {

	public TestConfig config;
	public BasicTest basic_test = new BasicTest();
	public GroupTest group_test = new GroupTest();
    private final Logger log = LoggerFactory.getLogger(getClass());
	private final TestConfigListener cfgListener = new TestConfigListener();
  	public ConfigFactory<ApplicationId, TestConfig> testFactory =
      new ConfigFactory<ApplicationId, TestConfig>(
          APP_SUBJECT_FACTORY, TestConfig.class, "testInfo") {
        @Override
        public TestConfig createConfig() {
          return new TestConfig();
        }
      };

	private String type;

    /** Some configurable property. */
    //private String someProperty;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
  	protected NetworkConfigRegistry cfgService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected FlowObjectiveService flowObjectiveService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected PacketService packetService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected HostService hostService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected FlowRuleService flowRuleService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected DeviceService deviceService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected CoreService coreService;

	@Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected TopologyService topologyService;

	@Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected GroupService groupService;

	protected ApplicationId appId;

    @Activate
    protected void activate() {
		appId = coreService.registerApplication("nctu.winlab.test");
		cfgService.addListener(cfgListener);
		cfgService.registerConfigFactory(testFactory);

		log.info("Started");
		log.info("host:{}",hostService.getHostCount());

    }

    @Deactivate
    protected void deactivate() {
		cfgService.removeListener(cfgListener);
		cfgService.unregisterConfigFactory(testFactory);
        log.info("Stopped");
	}

	public class TestConfigListener implements NetworkConfigListener {
		@Override
		public void event(NetworkConfigEvent event) {
		  //log.info("one");
		  if ((event.type() == CONFIG_ADDED || event.type() == CONFIG_UPDATED)
		      && event.configClass().equals(TestConfig.class)) {
			config = cfgService.getConfig(appId, TestConfig.class);
			//log.info("two");
		    if (config != null) {
				//log.info("three");
				config.getType();
				type = config.type;
				log.info("Type:{}",type);
				switch(type){
					case "BasicTest":
						basic_test.getService(
							hostService, 
							flowRuleService, 
							topologyService, 
							flowObjectiveService, 
							packetService, 
							appId);
						basic_test.getConfig(config);
						basic_test.InstallTestingRules();
						break;
					case "GroupTest":
						group_test.getService(
							hostService, 
							flowRuleService, 
							topologyService, 
							flowObjectiveService, 
							groupService, 
							appId);
						group_test.getConfig(config);
						group_test.InstallTestingRules();
						break;
				}
				cfgService.addListener(cfgListener);
		    }
		  }
		}
	}

}
