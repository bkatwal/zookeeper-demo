package bkatwal.zookeeper.demo.util;

import static bkatwal.zookeeper.demo.util.ZkDemoUtil.ALL_NODES;
import static bkatwal.zookeeper.demo.util.ZkDemoUtil.ELECTION_NODE;
import static bkatwal.zookeeper.demo.util.ZkDemoUtil.ELECTION_NODE_2;
import static bkatwal.zookeeper.demo.util.ZkDemoUtil.LIVE_NODES;
import static bkatwal.zookeeper.demo.util.ZkDemoUtil.getHostPostOfServer;
import static bkatwal.zookeeper.demo.util.ZkDemoUtil.isEmpty;

import bkatwal.zookeeper.demo.api.ZkService;
import bkatwal.zookeeper.demo.model.Person;
import java.util.List;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/** @author "Bikas Katwal" 26/03/19 */
@Component
public class OnStartUpApplication implements ApplicationListener<ContextRefreshedEvent> {

  private RestTemplate restTemplate = new RestTemplate();
  @Autowired private ZkService zkService;

  @Autowired private IZkChildListener allNodesChangeListener;

  @Autowired private IZkChildListener liveNodeChangeListener;

  @Autowired private IZkChildListener masterChangeListener;

  @Autowired private IZkStateListener connectStateChangeListener;

  @Override
  public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
    try {

      // create all parent nodes /election, /all_nodes, /live_nodes
      zkService.createAllParentNodes();

      // add this server to cluster by creating znode under /all_nodes, with name as "host:port"
      zkService.addToAllNodes(getHostPostOfServer(), "cluster node");
      ClusterInfo.getClusterInfo().getAllNodes().clear();
      ClusterInfo.getClusterInfo().getAllNodes().addAll(zkService.getAllNodes());

      // check which leader election algorithm(1 or 2) need is used
      String leaderElectionAlgo = System.getProperty("leader.algo");

      // if approach 2 - create ephemeral sequential znode in /election
      // then get children of  /election and fetch least sequenced znode, among children znodes
      if (isEmpty(leaderElectionAlgo) || "2".equals(leaderElectionAlgo)) {
        zkService.createNodeInElectionZnode(getHostPostOfServer());
        ClusterInfo.getClusterInfo().setMaster(zkService.getLeaderNodeData2());
      } else {
        if (!zkService.masterExists()) {
          zkService.electForMaster();
        } else {
          ClusterInfo.getClusterInfo().setMaster(zkService.getLeaderNodeData());
        }
      }

      // sync person data from master
      syncDataFromMaster();

      // add child znode under /live_node, to tell other servers that this server is ready to serve
      // read request
      zkService.addToLiveNodes(getHostPostOfServer(), "cluster node");
      ClusterInfo.getClusterInfo().getLiveNodes().clear();
      ClusterInfo.getClusterInfo().getLiveNodes().addAll(zkService.getLiveNodes());

      // register watchers for leader change, live nodes change, all nodes change and zk session
      // state change
      if (isEmpty(leaderElectionAlgo) || "2".equals(leaderElectionAlgo)) {
        zkService.registerChildrenChangeWatcher(ELECTION_NODE_2, masterChangeListener);
      } else {
        zkService.registerChildrenChangeWatcher(ELECTION_NODE, masterChangeListener);
      }
      zkService.registerChildrenChangeWatcher(LIVE_NODES, liveNodeChangeListener);
      zkService.registerChildrenChangeWatcher(ALL_NODES, allNodesChangeListener);
      zkService.registerZkSessionStateListener(connectStateChangeListener);
    } catch (Exception e) {
      throw new RuntimeException("Startup failed!!", e);
    }
  }

  private void syncDataFromMaster() {
    // BKTODO need try catch here for session not found
    if (getHostPostOfServer().equals(ClusterInfo.getClusterInfo().getMaster())) {
      return;
    }
    String requestUrl;
    requestUrl = "http://".concat(ClusterInfo.getClusterInfo().getMaster().concat("/persons"));
    List<Person> persons = restTemplate.getForObject(requestUrl, List.class);
    DataStorage.getPersonListFromStorage().addAll(persons);
  }
}
