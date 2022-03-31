# AutoTesting README.md

## 不要問，問就是漬鯊
--胡適(沒有說過) (應該)

---

### 前言
不知道妳各位有沒有聽過一個故事，工程師會想殺了兩個月前的自己，因為code寫太爛

這份專題目前分為兩大部分，Basic Test & Group Test，其中Basic就是會讓我想漬鯊的部分。如果你各位在trace的時候看到想宰了我，請email到以下地址

hisaishikanade.mg06@nycu.edu.tw

留下你的謾罵之後，在腦中想一下這個事實: 本姆咪只是個工管跑來雙主修資工的岩壁仔，你真的要對我這麼嚴苛嗎 :weary:

---

### function命名邏輯:
只在同一個class中呼叫的function小寫開頭，讓其他地方呼叫的用大寫開頭(對，就是Golang那套)，同時用駝峰方式命名。但基本上這命名邏輯是我寫到一半才開始apply的，所以你會看到滿山滿谷的例外。如果trace到想殺了我，一樣請email到上面的地址。

---

### 這部分其實可以刪掉
The Idolm@ster ShinyColors是我的心靈支柱

---

### 注意事項
1. 不管src還是dst機器，請務必記得設定讓該使用者都可以無密碼使用sudo!!

`sudo visudo`<br >
`%sudo   ALL=(ALL:ALL) NOPASSWD:ALL`

不然你就會看到程式吐了滿滿的failure給你 :)

2. 請記得將ping.py放置到src machine家目錄中，方便機器打封包出去。

### 操作流程
打開ONOS -> install sshctl & config -> 讓所有host互ping一下，向onos刷一下存在感 -> install autotesting & config -> suffer

## 專案架構

├── ANSI.java<br />
├── AutoTestFlag.java<br />
├── AutoTesting.java<br />
├── BasicConfig.java<br />
├── BasicReport.java<br />
├── BasicTest.java<br />
├── GroupConfig.java<br />
├── GroupReport.java<br />
├── GroupTest.java<br />
├── MachineInfo.java<br />
├── MatchField.java<br /> 
├── SSHComponent.java<br />
├── StoreResult.java<br />
├── TestConfig.java<br />
├── TopoChecker.java<br />
└── Treatment.java<br />

此處本姆咪會一一解釋每個檔案的功用，依照字母順序:)

### `ANSI.java`
---
基本上就是拿來給slf4j logger上色用而已，不然一片白色的log夠你看到眼睛脫窗

### `AutoTestFlag.java`
---
挺重要的一個部分。這邊因該會很臭長，看完要一點耐心:)

```java
enum PingType
```
根據testcase使用的matchfield & treatment決定最後測試時，要從src機器打出去的封包要是何種類型。

判斷邏輯: 預設使用icmp，若是treatment/matchfield牽涉到tcp/udp/sctp/arp則會更新進`currentMatchFieldFlag`這個變數，之後生成ping&dump command的時候會用到。

看到這裡你因該會有一個疑惑: 如果有USER在matchfield/treatment把兩種protocol混用怎辦?像是match udp port 961又match tcp port 283

請把那個USER扁一頓，看你要順著網路線過去還是怎樣

會用這鬼東西的人如果是這種等級的網路白癡，建議漬鯊

主要就icmp tcp udp sctp arp這五種。預設使用icmp。

see also: `ping.py`

```java
enum TreatmentType
```
儲存使用者要測試的treatment，使用者每用一個treatment就會將資料儲存進`treatmentFlags`這個araylist，一樣之後要生成ping&dump command的時候會用到。

see also: `ping.py`

```java
public void SetUseXXX()
public void GetFlagsXXX()
```

字面上的意思，不解釋

see also: `Matchfield.java`/`Treatment.java`

```java
public void MatchXXX()
```
因為MatchField裡面會自動幫一些matchfield上prerequist，這邊是防止多次match同一個pre-req而設定的flag。

```java
public void SetTreatmentFlag()
```
將使用者用到的treatment儲存起來以供DumpCommand使用。

```java
public void ReverseArray()
```
給groupTest用的，因為grouptest是使用倒序install group，在生成ping&dump command的時候需要倒過來才能正常運作。

```java
public String GetDumpCommand()
```
重頭戲，這邊會根據treatmentFlags這個arraylist來決定該在dst機器儲存啟動何種tcpdump的filter

兩種情況

情況A: 使用者只要一個單純的OUTPUT

僅僅根據matchfield來決定(`enum PingType`)(`generateByProtocol`)
```java
return "\"sudo -S tcpdump -n src " + srcIp + " and arp -c 1 -i " + infos.get(paramDstId).machineIface + "\"";
```

情況B: 使用者有兩種(含)以上的treatment

murmur: 其實我寫到後面已經有點錯亂了，但我一開始的想法是這樣沒錯，如果妳各位trace到後面有發現跟我說的不一樣，一樣請參見上方電子郵件。

那我們就會需要根據使用者所下的treatment來決定。

這邊先解釋我的方法，聽不懂再看後面例子

Layer1: 不存在:)

Layer2:<br>
`SetEthSrc`: 抓取從Src機器的Mac送來的封包，在使用ping.py送出封包時會使用srcMac `98:E7:9A:27:FE:49`，待SetEthSrc改寫成正確SrcMac以驗證功能正常。

`SetEthDst`: 抓取送往Dst1機器的Mac的封包，在使用ping.py送出封包時會使用dstMac `98:E7:9A:27:FE:49`，待SetEthDst改寫成正確DstMac以驗證功能正常。

為甚麼是這個mac呢，這要從我高中說起
本竹園崗肥宅高中時有一個wifi使用mac認證，不知道為甚麼本肥肥朋友手機的MAC可以連上，所以我就把它背起來了:)

Layer3:(我懷疑我這邊是寫錯的，但是我也沒機器可以驗證。幹你娘DGS3630)<br>
`SetIpSrc`: 抓取從Src機器的Ip送來的封包，在使用ping.py送出封包時會使用Ip `10.28.3.83`，待SetIpSrc改寫成正確SrcIp以驗證功能正常

`SetIpDst`: 抓取送到Dst1機器的IP的封包，在使用ping.py送出封包時會送至Ip `10.28.3.83`，待SetIpDst改寫成正確DstIp以驗證功能正常

如果你好奇為甚麼Ip不是`10.283.283.283`，因為IPv4只到255:)<br>
附帶一提，此處的資訊都儲存在testconfig.machineInfos中，會在每個測試開頭檢查完拓樸之後將假的host資訊塞進去，如果你想改成你喜歡的IP可以去那邊改。以BasicTest來說，第132行。

Layer4:<br>
`SetXXXSrc`:
抓取port `7650`來的封包，在使用ping.py送出封包時會使用port `9610`送出，待setXXXSrc改寫成`7650`以驗證功能正常<br>
`SetXXXDst`:
抓取送到port `2830`的封包，在使用ping.py送出封包時會使用port `3460`送出，待setXXXDst改寫成`2830`以驗證功能正常

如果你好奇為甚麼不是283跟765，單純是因為這兩個數字都小於1000，我怕有哪個北七服務佔據了他。這邊是寫死的，要改請重新compile。

舉以下範例
```json
{
	"matchfield":[
		"TCP_SRC"
	],
	"treatment":[
        "SetTcpSrc",
		"OUTPUT"
	]
}
```
這邊的測資理論上會串出
```bash
sudo -S tcpdump -n tcp and src port 7650 -c 1 -i ens11f1
                protocol       tcp port   1pkt   listen ens11f
```
的dumpcommand給dst機器抓封包。

如果你在思考為甚麼`GetDumpCommand`這函數有多載，這是給grouptest用的。因為groupTest的時候會需要測試all bucket，dst可能不只一個。所以需要指定是哪台機器要接收以方便取得dst iface給dumpcommand使用。

see also: `ping.py`

```java
public String GetPingCommand()
```

專門拿來產出給src機器用的指令。

幾個參數解釋一下

srcPort: 可參見`GetDumpCommand()` L4部分。判斷依據為`flagsUseDummySrcPort`

dstPort: 同上，判斷依據為`flagsUseDummyDstPort`

srcMac: 同上，L2部分，`flagsUseDummySrcMac`

dstMac: 同上，`flagsUseDummyDstMac`

srcIp: 同上，L3部分。此處判斷依據比較複雜，因為牽涉到了vlan的interface。<br>
對，想不到吧，還有這東西來參一腳。

然後一樣，我覺得這邊很有問題，但我沒實際跑過這部分所以我也不知道vlan 部分會跑成甚麼鬼樣。

`getDstIpToUse` & `getDstIpToUse` 我就在這邊一起講吧，各自牽涉到
`flagsUseVlanDst`, `flagsUseDummyDstIp`, `flagsUseVlanSrc`, `flagsUseDummySrcIp`，兩兩一組，各自四個case

先講vlanId部分

Treatment中，使用者若是有使用到PushVlanId，則`flagsUseVlanDst`會被設定為`True`，若使用到PopVlanId，則`flagsUseVlanSrc`為`True`

Matchfield中，使用者若使用到MatchVlanId，則`flagsUseVlanSrc`為`True`

再講Ip部分

Treatment中，使用到SetIpSrc，`flagsUseDummySrcIp` = `True`
;使用到SetIpDst `flagsUseDummyDstIp` = `True`

搭配以下範例code服用

fact: `List<MachineInfo> infos`這東西的最後一個element一定是假的host Info

```java
private String getSrcIpToUse(List<MachineInfo> infos) {
    if (flagsUseVlanSrc && flagsUseDummySrcIp) {
        return infos.get(infos.size()-1).machineIpV.toString();
    }
    else if (flagsUseVlanSrc && !flagsUseDummySrcIp) {
        return infos.get(0).machineIpV.toString();
    }
    else if (!flagsUseVlanSrc && flagsUseDummySrcIp) {
        return infos.get(infos.size()-1).machineIp.toString();
    }
    else if (!flagsUseVlanSrc && !flagsUseDummySrcIp) {
        return infos.get(0).machineIp.toString();
    }
    else {
        return "";
    }
}
```

理論上是這樣，但是我打這部分的時候才想到，連同處理vlan的部份的話，在matchfield那邊應該要讓他match假的vlanip才對，但是那部份我沒寫，所以你各位最好是當這邊扯上vlan的通通都不能work比較保險。

AutoTestFlag大概到這邊為止。

### `AutoTesting.java`
---
這邊是整個程式的進入點。onos接收到config file之後會通知listener，然後根據testType來決定要進行BasicTest還是GroupTest

### `BasicConfig.java`
---
這邊用於parse BasicConfig file，並將每個testcase存成一個物件用於之後存取。

see also: `TestConfig.java`

### `BasicReport.java`
---
其實這裡面沒有東西(暫時)

寫完groupTest之後回頭發現之前存basicTest Result的方法超他媽toxic，想要動手改一下，才發現時間不夠了，再不交接研究所要直接落榜了:(

之後有哪位願意修改的可以動手改，我先把框架留在這邊

### `BasicTest.java`
---
流程圖見下方
```java
public void getService()
```
把一些需要用到的service丟進來

```java
public void getConfig()
```
把`TestConfig.java`丟進來。重置teatcaseCount 檢查拓樸 新增假的Host都是在這邊完成的

```java
public class InstallListener implements FlowRuleListener
```
會在`FlowRule`成功安裝在switch上時觸發。觸發後首先停止timeout用的Timer，然後再dst機器開啟`tcpdump`，稍等一下之後再去src機器打出封包。數秒後檢查連線是否還活著用以判斷測試是否成功，並將結果寫入。

結果寫入完成後會清除flowrule。

這邊要注意的事情是，若使用者欲測試的MatchField或是Treatment不被交換機所支援仍然會觸發這個listener。

另一個問題是onos核心有個bug會導致listener用一用自己不見，需要重新`addListener()`，所以建議listener觸發完之後就先下掉，需要使用listener之前再`addListener()`(可參考`GroupTest.java`)

```java
public class DeleteListener implements FlowRuleListener
```
會在`FlowRule`被移除時觸發，並檢查是否還有其他測資要做。若有則進入下一個testcase，若無則產生測試報告並停止。

```java
public class TimerDelay extends TimerTask
```
此處的作用是防止使用者下了衝突的matchfield或treatment時可以使流程繼續。

若是使用者下達了像是matchArp + matchTcp這種鬼東西，會使得該flowrule直接下不上去(此情況不會觸發`InstallListener`)，所以我們需要一計時器，若是時間內沒有觸發`InstallListener`則判定有失敗，清除flowrule並進入下一個測資。

另再請注意，上述情況會導致一處在`Pending_Add`的`FlowRule`，移除處在`Pending_Add`的flowrule並不會觸發`DeleteListener`

```java
public void InstallTestingRules()
```
測試開始，誒黑！一個testcase就是從這邊開始，建Treatment，建MatchField，啟動TimeoutTimer，Install FlowRule。

```java
public void BuildFlowObjective()
```
用來Install FlowRule。

### `GroupConfig.java`
---
這邊用於parse GroupConfig file，並將每個testcase存成一個物件用於之後存取。

see also: `TestConfig.java`, `grouptest.json`

### `GroupReport.java`
---
用於儲存所有testCase的結果。
```java 
public class GroupCaseReport
```
每一個testcase都有一個自己的casereport，儲存於 `GroupReport.java` 中的`protected List<GroupCaseReport> testcases` 
```java
public class UsingGroups
```
每一個case中可能會用到多個group，此處紀錄一個group所使用的參數，包括grouptype，目標IP，目標group.....

see also: `GroupTest.java`

### `GroupTest.java`
---
用於進行`GroupTest`
```java
public void getService()
public void getConfig()
```
此兩者同`BasicTest.java`

```java
public class GroupInstallListener implements GroupListener
```
`Group`被Install到交換機上時觸發，若還有其他pipeline則繼續Install下一個；若無則進入Install Flowrule的環節

```java
public class GroupDeleteListener implements GroupListener
```
`Group`被從交換機刪除時觸發，確認此次測試的東西砍光之後回到`InstallTestingRules()`以判斷是否有下一個testcase

```java
public class InstallListener implements FlowRuleListener
```
`Flowrule` install成功時觸發，開始連線到dst機器開tcpdump再連線到src機器送出封包。

基本同`Basictest.java`

```java
public class DeleteListener implements FlowRuleListener
```
`Flowrule`被刪除時觸發，注意安裝的時候是倒著安裝group再安裝flowrule，要刪除時則要順序刪回去

```java
public class TimerDelay extends TimerTask
```
沒完成的timeout功能，靜待有緣人

```java
public void InstallTestingRules()
```
一切的開端:))

```java
public void BuildFlowObjective()
```
同`BasicTest.java`

```java
private void installPipeLine()
```
根據使用者提供的資訊來Install OFDPA Pipeline

```java
public void installFlowRule()
```
字面上意思，將封包導向第一個group

```java
private int generateGroupId()
```
感謝DGS-3630死媽計算方式讓我要這樣寫個東西來算groupId。這邊是根據機器的邏輯寫出來的計算流程，想看完整計算方式請去翻文件。

```java
private void installIndirectL2Interface()
```
這個Group只能用`OUTPUT`，在ATFG裡面新增OUTPUT的FLAG之後用預設的`TrafficTreatment`寫個`OUTPUT`完事，將算好的`gid`丟進`lastUsedGid`。

```java
private void installIndirectL3Unicast()
```
這個Group是專門拿來讓你弄其他Treatment的，這邊使用自己寫的`Treatment.java`來生成`TrafficTreatment`，將算好的`gid`丟進`lastUsedGid`。

```java
private void installAllL2Flood()
```
用於`All Group`此處也可以改寫，只是我寫的部分在跑All Group測試時有BUG，所以先當成不能用比較好。將算好的`gid`丟進`lastUsedGid`。

```java
private void printPipeInfo()
```
bj4

### `MachineInfo.java`
---
一台機器基本上就有一個MachineInfo的obj

統一儲存於`TestConfig.java` `machineInfos` 中

儲存格式如下

Array [srcMachine, dst1, dst2, ....., dummyMachine]

沒什麼好講的，就方便儲存而已。唯一需要注意的是 
```java
public Host machineHost
```
這東西會再TopologyChecker的部分才進行assign，所有機器assign完才會把dummyMachine塞進去。

### `MatchField.java`
---
注意!<br>
感謝你的注意。

這個部分就是實際建構`FlowRule`中的`TrafficSelector`的地方了。可以先從檔案的18行開始看，`testcase.json`中使用的就是這些字串。

```java
protected String MatchInfo = "", DependentInfo = "";
```
`MatchInfo`中會以字串的形式將所有使用到的`MatchField`串起來，而DependentInfo則是使用該MatchField時我們程式自動安裝的dependency。<br>
以`TCP_SRC`為例，在使用這dependency之前我們需要Match Ipv4 & TCP

see also: `BasicTest.java`, `GroupTest.java`

### `SSHComponent.java`
---
用於開啟SSH連線用的東西
```java
public void StartUpConnection(String command)
```
這邊丟進來的command直接就是我們要在目標機器上執行的command。以下是命令格式範例

#### src機器
```bash
onos localhost sshctl -t 0 exec \"sudo ./ping.py flagPING srcMac dstMac srcIp dstIp srcIface\"
```

#### dst機器
```bash
onos localhost sshctl -t 1 exec \"sudo tcpdump -n src srcIp and icmp -c 1 -i dstIface\"
```

```java
public Boolean CheckIsStillAlive()
```
這函數用於檢查dst機器有沒有正常收到封包

因為我們的`tcpdump`有指定`-c 1`故機器收到符合條件的一個封包之後該指令就會結束。所以如果我們呼叫這個函數的時候這程序還活著，代表tcpdump還沒收到任何東西，可以用於判斷測試結果是成功還是失敗。

```java
public void KillConnection()
```
bj4

```java
public String Result()
```
用於取得dst機器上tcpdump所列出來的東西，以下範例
```bash
21:21:44.299023 IP 10.28.3.83 > 10.28.3.65: ICMP echo request, id 3, seq 1, length 64
```
要注意的事情是檔案第34行，這東西是blocking的。也就是說如果tcpdump如果還沒結束你就呼叫這東西，整個程式會因此卡住。呼叫這東西之前請務必先用`CheckIsStillAlive()`確認。

### `StoreResult.java`
---
兩個部分
```java
public void Report()
```
這東西是給`BasicTest`用的，不過說實話我覺得這部分其實可以改用`static`就好，但我懶得改了。基本用途就是把之前存下來的testcase結果寫成檔案。個人覺得這寫法其實超級toxic。

沒參考範例，因為我不小心把basictest的log全部砍光了

```java
public static void GroupReport(GroupReport gReport)
```
這部份給`GroupTest`用的。參考範例
![https://i.imgur.com/mzMrTun.png](https://i.imgur.com/mzMrTun.png)

see also: `BasicTest.java`, `GroupTest.java`

### `TestConfig.java`
---
```java
public void parseHostInfo()
```
笑死，其實這裡面根本不用靠switch分的，可以直接並在一起的樣子。因為之前`groupTest`跟`basicTest`的host config格式不太一樣所以才分兩個。後來我直接把兩者的config統一了，所以其實可以合併。

```java
public void parseBasicConfig()
public void parseGroupConfig()
```
這兩個在幹差不多的事情，一起講。基本上就是把丟上來的.json檔案轉換成我要的格式。`Basic`的部分是`BasicConfig.java`，`Group`的部分就是`GroupConfig.java`

下面幾個函數看命名就知道在幹嘛了，這邊就不一一講=ˇ=
反正我打了你大概也沒耐心看完

### `TopoChecker.java`
---
用來檢查拓樸並且把每個host的資料塞進machineInfo這東西裡面。在測試之前請讓host之間互ping一下，沒有通也沒關係，主要要讓onos知道有這台機器的存在以免後續測試卡住。

see also: `BasicTest.java`, `GroupTest.java`

### `Treatment.java`
---
這邊是用於建構`FlowRule`中`TrafficTreatment`的部分。詳細內容我在`AutoTestFlag.java`有講過一遍，可以去看一下我的邏輯。唯一值得提一下的就只有這個
```java
public void ToGroupTable(int gid)
```
這個部分是給`GroupTest`用的，用於將封包導向另一個group。

see also: `BasicTest.java`, `GroupTest.java`

### `ping.py`
---
用於發送封包。這個檔案請記得放在src機器的家目錄底下，不然程式跑不動。

## Config File
---
請注意正常情況下json檔案中是不能有註解的，這邊的範例複製貼上的話請記得把註解移除。
### SSHCconfig
```json
{
	"apps": {
		"project.dlink.sshclient": {
			"SshClientConfig": {
				"clientInfos": [
					{// id 0
						"ip": "140.113.131.172",
						"username": "winlab",
						"password": "winlabisgood",
						"model": "SERVER"
					},
					{// id 1
						"ip": "140.113.131.147",
						"username": "dlinktest2",
						"password": "admin",
						"model": "SERVER"
					}
				]
			}
		}
	}
}
```

### BasicTest Config
```json
{
	"apps": {
		"nctu.winlab.test": {
			"testInfo": {
				"testType": "BasicTest", // 測試類別
				"src_host": { // src機器只有一台
					"id": "0", // 這個ID對應的是sshconfig.json中的機器編號，這邊的情況是140.113.131.172那台
					"iface": "ens11f3:192.168.202.1", // 介面:IP
					"ifaceV": "ens11f3.1:192.168.133.1"
				},
				"dst_host": [ // 為了方便與GroupTest統一，這邊一樣使用陣列形式
					{
						"id": "1", // 對應140.113.131.147
						"iface": "eno2:192.168.201.2",
						"ifaceV": "eno2.1:192.168.203.2"
					}
				],
				"testcase": [					
					{
						"matchfield":[
							"ARP_THA"
						],
						"treatment":[
							"OUTPUT"
						]
					},
					{
						"matchfield": [ // 能用的欄位請參考MatchField.java
							"TCP_SRC" 
						],
						"treatment":[ // 能用的欄位請參考Treatment.java
							"SetTcpSrc",
							"OUTPUT"
						]
					}
				]
			}
		}
	}
}
```

### GroupTest Config
```json
{
	"apps": {
		"nctu.winlab.test": {
			"testInfo": {
				"testType": "GroupTest",
				"src_host": {
					"id": "0", //對應140.113.131.172那台
					"iface": "ens11f3:192.168.202.1",
					"ifaceV": "ens11f3.1:192.168.133.1"
				},
				"dst_host": [
					{
						"id": "1", // 對應140.113.131.147
						"iface": "eno3:192.168.202.2",
						"ifaceV": "eno3.1:192.168.203.2"
					},
					{
						"id": "0", //對應140.113.131.172那台
						"iface": "ens11f1:192.168.202.6",
						"ifaceV": "ens11f1.1:192.168.203.3"
					}
				],
				"testcase": [					
					{
						"matchfield": ["ETH_SRC","ETH_DST"],
						"pipeline": [
							{
								"tableId": "0", // tableId請參考 https://wiki-archive.opencord.org/Fabric-Design-Note_1278478.html
								"vlanId": "1",
								"pipeType": "Indirect",
								"treatment":"OUTPUT", // 這邊是L2 Interface，只能OUTPUT
								"destination": 1
							},
							{
								"tableId": "1",
								"vlanId": "1",
								"pipeType": "Indirect", 
								"treatment": "SetEthSrc" // 暫時只支援單一Treatment，改成陣列然後用迴圈丟進Treatment.java就好
							}
						]
					},
					{
						"matchfield": ["ETH_SRC","ETH_DST"],
						"pipeline": [
							{
								"tableId": "0",
								"vlanId": "1",
								"pipeType": "Indirect",
								"treatment":"OUTPUT",
								"destination": 2
							}
						]
					}
				]
			}
		}
	}
}
```

## Basic Test部分
---
先上流程圖
![https://i.imgur.com/dC6PdyU.png](https://i.imgur.com/dC6PdyU.png)

自己點開放大看

如果用一句話來總結這程式，應該會是Event Driven，而大學生的作業是deadline driven
--浅倉　冬優子(沒有這個人)

因為我也沒寫過甚麼正經的文件，我就用流程圖上每個block對應的function來解釋好了

可搭配上方的`BasicTest.java`服用

### Parse Config & Regist Listener
---
testconfig請直接參考上方

這邊是程式的起點，autotesting一接收到testcase.json就會觸發listener，在這邊判斷完要進行的測試是basic test還是group test之後會進入相對應的部分

### Has Next Testcase?
### Read Next Treatment
### Read Next MatchField
---
參考`InstallTestingRule()`

### Install Flowrule and Start Timeout Timer
---
參考`BuildForwardingObjective()`

### Flowrule Listener
---
Yes -> 參考`InstallListener`
No  -> 參考`TimerDelay`

經歷一連串操作之後回到 `Has Next Testcase?`

一直重複直到所有測資結束為止，最後生成報告。

## Group Test部分
---
一樣上圖
![https://i.imgur.com/vayJrCR.png](https://i.imgur.com/vayJrCR.png)

![https://i.imgur.com/IYX7xuw.jpg](https://i.imgur.com/IYX7xuw.jpg)
我寫的部分基本上支援兩種`Indirect`模式(上途中淡藍色與深紅色路徑。)`All`(橘色路徑)有寫了，只是有BUG

### Parse Config & Regist Listener
---
testconfig請直接參考上方

這邊是程式的起點，autotesting一接收到testcase.json就會觸發listener，在這邊判斷完要進行的測試是basic test還是group test之後會進入相對應的部分

### Read & Install Next Pipeline
---
取得下一個需要Install的OFDPA Pipeline並根據死媽DGS3630的規則計算出groupId，註冊`GroupListener`之後將建立的Group Install到交換機上。

這邊請注意，除了`ALL`的Group以外(`installIndirectL2Interface`, `installIndirectL3Unicast`)，`Treatment`最後一步一定是指向上一個GroupId(儲存在`LastUsedGid`這List中，請自行取用)。`All`的情況是，要新增多個`GroupBucket`，並各自指向一個`L2 Interface`。

### GroupListener Triggered in time?
---
這邊我要坦誠一件事情，`GroupTest`我並沒有弄出Timeout機制，時間因素。但是因該很簡單啦，因該，照抄一下`BasicTest`的架構就好。

另外要注意的是，非`OUTPUT`的`Treatment`只會在`L3 Unicast`或是`L2 Flood Group`出現，`L2 Interface`的`Treatment`只能是`OUTPUT`

參考: [OFDPA PipeLine](https://wiki-archive.opencord.org/Fabric-Design-Note_1278478.html)

所以要注意`ATFG`跟`Treatment`只會在上述前兩者的時後才進行新增。在安裝Install `L2 Interface`的時候記得不要用我寫的`Treatment`，而是直接寫個新的`TrafficTreatment`，ATFG記得新增個`OUTPUT` flag就好

### Read & Install Next Matchfield
---
就，字面上的意思

### Install Flowrule
---
最後要下一條Flowrule，以上面的`Matchfield`為`Selector`並將`Treatment`指向`lastUsedGid`中的最後一個`GroupId`。

要注意<br>
1. 此處需要將Treatment設定為`deferred`，再指向group，所以這邊我沒有使用我自己寫的`Treatment`
2. 記得要把`listener`註冊上去。

後面的部分就跟`BasicTest`差不多了，都是開dst機器的SSH連線後再送出封包。
只要稍微處理一下All Group跟其他兩種情況的不同就好

最後清理完回到 `Has Next Testcase?` 視情況生成報告或是進行下一個case。

## `結尾`
---

2021-12-18 Euphokumiko@112圖書館&家裡
