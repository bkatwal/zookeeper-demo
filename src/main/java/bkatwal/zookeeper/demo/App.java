package bkatwal.zookeeper.demo;

import bkatwal.zookeeper.demo.api.ZkService;
import bkatwal.zookeeper.demo.impl.ZkServiceImpl;
import org.apache.log4j.BasicConfigurator;

/** @author "Bikas Katwal" 26/03/19 */
public class App {

  public static void main(String[] args) throws Exception {
    BasicConfigurator.configure();
    ZkService zkService = new ZkServiceImpl("localhost:2181");
    Thread.sleep(600000);
    System.out.println();
  }

  // print I am a master/follower, write should always be in master
  // spring boot keeps master
}
