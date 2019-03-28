package bkatwal.zookeeper.demo.util;

import bkatwal.zookeeper.demo.model.Person;
import java.util.ArrayList;
import java.util.List;

/** @author "Bikas Katwal" 26/03/19 */
public final class DataStorage {

  private static List<Person> personList = new ArrayList<>();

  public static List<Person> getPersonListFromStorage() {
    return personList;
  }

  public static void setPerson(Person person) {
    personList.add(person);
  }

  private DataStorage() {}
}
