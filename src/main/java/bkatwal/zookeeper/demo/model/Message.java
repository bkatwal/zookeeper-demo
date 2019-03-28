package bkatwal.zookeeper.demo.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** @author "Bikas Katwal" 26/03/19 */
@Getter
@AllArgsConstructor
public class Message {

  private String message;
  List<Person> persons;
}
