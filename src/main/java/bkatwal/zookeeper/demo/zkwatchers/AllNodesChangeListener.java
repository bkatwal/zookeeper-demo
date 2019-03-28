package bkatwal.zookeeper.demo.zkwatchers;

import bkatwal.zookeeper.demo.util.ClusterInfo;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkChildListener;

/** @author "Bikas Katwal" 27/03/19 */
@Slf4j
public class AllNodesChangeListener implements IZkChildListener {

  /**
   * This will listen to persistent znode This will be triggered if any new node is added to the
   * cluster or if the node is permanently delete from the cluster
   *
   * @param parentPath
   * @param currentChildren
   */
  @Override
  public void handleChildChange(String parentPath, List<String> currentChildren) {
    log.info("current all node size: {}", currentChildren.size());
    ClusterInfo.getClusterInfo().getAllNodes().clear();
    ClusterInfo.getClusterInfo().getAllNodes().addAll(currentChildren);
  }
}
