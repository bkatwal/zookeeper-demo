package bkatwal.zookeeper.demo.impl;

import static bkatwal.zookeeper.demo.util.ZkDemoUtil.ALL_NODES;
import static bkatwal.zookeeper.demo.util.ZkDemoUtil.ELECTION_MASTER;
import static bkatwal.zookeeper.demo.util.ZkDemoUtil.ELECTION_NODE;
import static bkatwal.zookeeper.demo.util.ZkDemoUtil.ELECTION_NODE_2;
import static bkatwal.zookeeper.demo.util.ZkDemoUtil.LIVE_NODES;
import static bkatwal.zookeeper.demo.util.ZkDemoUtil.getHostPostOfServer;

import bkatwal.zookeeper.demo.api.ZkService;
import bkatwal.zookeeper.demo.util.StringSerializer;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;

/** @author "Bikas Katwal" 26/03/19 */
@Slf4j
public class ZkServiceImpl implements ZkService {

  private ZkClient zkClient;

  
  public ZkServiceImpl(String hostPort) {
    zkClient = new ZkClient(hostPort, 12000, 3000, new StringSerializer());
  }

  public void closeConnection() {
    zkClient.close();
  }

  @Override
  public String getLeaderNodeData() {

    return zkClient.readData(ELECTION_MASTER, true);
  }

  @Override
  public void electForMaster() {
    if (!zkClient.exists(ELECTION_NODE)) {
      zkClient.create(ELECTION_MASTER, "election node", CreateMode.PERSISTENT);
    }
    try {
      zkClient.create(
          ELECTION_MASTER,
          getHostPostOfServer(),
          ZooDefs.Ids.OPEN_ACL_UNSAFE,
          CreateMode.EPHEMERAL);
    } catch (ZkNodeExistsException e) {
      log.error("Master already created!!, {}", e);
    }
  }

  @Override
  public boolean masterExists() {
    return zkClient.exists(ELECTION_MASTER);
  }

  @Override
  public void addToLiveNodes(String nodeName, String data) {
    if (!zkClient.exists(LIVE_NODES)) {
      zkClient.create(LIVE_NODES, "all live nodes are displayed here", CreateMode.PERSISTENT);
    }
    String childNode = LIVE_NODES.concat("/").concat(nodeName);
    if (zkClient.exists(childNode)) {
      return;
    }
    zkClient.create(childNode, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
  }

  @Override
  public List<String> getLiveNodes() {
    if (!zkClient.exists(LIVE_NODES)) {
      throw new RuntimeException("No node /liveNodes exists");
    }
    return zkClient.getChildren(LIVE_NODES);
  }

  @Override
  public void addToAllNodes(String nodeName, String data) {
    if (!zkClient.exists(ALL_NODES)) {
      zkClient.create(ALL_NODES, "all live nodes are displayed here", CreateMode.PERSISTENT);
    }
    String childNode = ALL_NODES.concat("/").concat(nodeName);
    if (zkClient.exists(childNode)) {
      return;
    }
    zkClient.create(childNode, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
  }

  @Override
  public List<String> getAllNodes() {
    if (!zkClient.exists(ALL_NODES)) {
      throw new RuntimeException("No node /allNodes exists");
    }
    return zkClient.getChildren(ALL_NODES);
  }

  @Override
  public void deleteNodeFromCluster(String node) {
    zkClient.delete(ALL_NODES.concat("/").concat(node));
    zkClient.delete(LIVE_NODES.concat("/").concat(node));
  }

  @Override
  public void createAllParentNodes() {
    if (!zkClient.exists(ALL_NODES)) {
      zkClient.create(ALL_NODES, "all live nodes are displayed here", CreateMode.PERSISTENT);
    }
    if (!zkClient.exists(LIVE_NODES)) {
      zkClient.create(LIVE_NODES, "all live nodes are displayed here", CreateMode.PERSISTENT);
    }
    if (!zkClient.exists(ELECTION_NODE)) {
      zkClient.create(ELECTION_NODE, "election node", CreateMode.PERSISTENT);
    }
  }

  @Override
  public String getLeaderNodeData2() {
    if (!zkClient.exists(ELECTION_NODE_2)) {
      throw new RuntimeException("No node /election2 exists");
    }
    List<String> nodesInElection = zkClient.getChildren(ELECTION_NODE_2);
    Collections.sort(nodesInElection);
    String masterZNode = nodesInElection.get(0);
    return getZNodeData(ELECTION_NODE_2.concat("/").concat(masterZNode));
  }

  @Override
  public String getZNodeData(String path) {
    return zkClient.readData(path, null);
  }

  @Override
  public void createNodeInElectionZnode(String data) {
    if (!zkClient.exists(ELECTION_NODE_2)) {
      zkClient.create(ELECTION_NODE_2, "election node", CreateMode.PERSISTENT);
    }
    zkClient.create(ELECTION_NODE_2.concat("/node"), data, CreateMode.EPHEMERAL_SEQUENTIAL);
  }

  @Override
  public void registerChildrenChangeWatcher(String path, IZkChildListener iZkChildListener) {
    zkClient.subscribeChildChanges(path, iZkChildListener);
  }

  @Override
  public void registerZkSessionStateListener(IZkStateListener iZkStateListener) {
    zkClient.subscribeStateChanges(iZkStateListener);
  }
}
