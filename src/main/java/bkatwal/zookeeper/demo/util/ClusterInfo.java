package bkatwal.zookeeper.demo.util;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** @author "Bikas Katwal" 26/03/19 */
@Getter
@Setter
public final class ClusterInfo {

  private static ClusterInfo clusterInfo = new ClusterInfo();

  public static ClusterInfo getClusterInfo() {
    return clusterInfo;
  }

  /*
  these will be ephemeral znodes
   */
  private List<String> liveNodes = new ArrayList<>();

  /*
  these will be persistent znodes
   */
  private List<String> allNodes = new ArrayList<>();

  private String master;
}
