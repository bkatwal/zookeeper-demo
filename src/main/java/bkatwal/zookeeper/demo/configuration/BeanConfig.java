package bkatwal.zookeeper.demo.configuration;

import bkatwal.zookeeper.demo.api.ZkService;
import bkatwal.zookeeper.demo.impl.ZkServiceImpl;
import bkatwal.zookeeper.demo.zkwatchers.AllNodesChangeListener;
import bkatwal.zookeeper.demo.zkwatchers.ConnectStateChangeListener;
import bkatwal.zookeeper.demo.zkwatchers.LiveNodeChangeListener;
import bkatwal.zookeeper.demo.zkwatchers.MasterChangeListener;
import bkatwal.zookeeper.demo.zkwatchers.MasterChangeListenerApproach2;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/** @author "Bikas Katwal" 26/03/19 */
@Configuration
public class BeanConfig {

  @Bean(name = "zkService")
  @Scope("singleton")
  public ZkService zkService() {
    String zkHostPort = System.getProperty("zk.url");
    return new ZkServiceImpl(zkHostPort);
  }

  @Bean(name = "allNodesChangeListener")
  @Scope("singleton")
  public IZkChildListener allNodesChangeListener() {
    return new AllNodesChangeListener();
  }

  @Bean(name = "liveNodeChangeListener")
  @Scope("singleton")
  public IZkChildListener liveNodeChangeListener() {
    return new LiveNodeChangeListener();
  }

  @Bean(name = "masterChangeListener")
  @ConditionalOnProperty(name = "leader.algo", havingValue = "1")
  @Scope("singleton")
  public IZkChildListener masterChangeListener() {
    MasterChangeListener masterChangeListener = new MasterChangeListener();
    masterChangeListener.setZkService(zkService());
    return masterChangeListener;
  }

  @Bean(name = "masterChangeListener")
  @ConditionalOnProperty(name = "leader.algo", havingValue = "2", matchIfMissing = true)
  @Scope("singleton")
  public IZkChildListener masterChangeListener2() {
    MasterChangeListenerApproach2 masterChangeListener = new MasterChangeListenerApproach2();
    masterChangeListener.setZkService(zkService());
    return masterChangeListener;
  }

  @Bean(name = "connectStateChangeListener")
  @Scope("singleton")
  public IZkStateListener connectStateChangeListener() {
    ConnectStateChangeListener connectStateChangeListener = new ConnectStateChangeListener();
    connectStateChangeListener.setZkService(zkService());
    return connectStateChangeListener;
  }
}
