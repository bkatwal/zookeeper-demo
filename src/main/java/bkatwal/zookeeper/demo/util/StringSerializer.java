package bkatwal.zookeeper.demo.util;

import java.nio.charset.StandardCharsets;
import org.I0Itec.zkclient.serialize.ZkSerializer;

/** @author "Bikas Katwal" 26/03/19 */
public class StringSerializer implements ZkSerializer {

  @Override
  public byte[] serialize(Object data) {
    return ((String) data).getBytes();
  }

  @Override
  public Object deserialize(byte[] bytes) {
    return new String(bytes, StandardCharsets.UTF_8);
  }
}
