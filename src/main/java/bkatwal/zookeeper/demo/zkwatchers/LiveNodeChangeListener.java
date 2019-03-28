package bkatwal.zookeeper.demo.zkwatchers;

import bkatwal.zookeeper.demo.util.ClusterInfo;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkChildListener;

/** @author "Bikas Katwal" 26/03/19 */
@Slf4j
public class LiveNodeChangeListener implements IZkChildListener {

  /**
   * This will listen to ephemeral znode This will be triggered if any existing node is down/dead or
   * comes alive
   *
   * @param parentPath
   * @param currentChildren
   */
  @Override
  public void handleChildChange(String parentPath, List<String> currentChildren) {
    log.info("current live size: {}", currentChildren.size());
    ClusterInfo.getClusterInfo().getLiveNodes().clear();
    ClusterInfo.getClusterInfo().getLiveNodes().addAll(currentChildren);
  }
}
