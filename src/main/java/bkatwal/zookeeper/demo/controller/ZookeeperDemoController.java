package bkatwal.zookeeper.demo.controller;

import static bkatwal.zookeeper.demo.util.ZkDemoUtil.getHostPostOfServer;

import bkatwal.zookeeper.demo.model.Person;
import bkatwal.zookeeper.demo.util.ClusterInfo;
import bkatwal.zookeeper.demo.util.DataStorage;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/** @author "Bikas Katwal" 26/03/19 */
@RestController
public class ZookeeperDemoController {

  private RestTemplate restTemplate = new RestTemplate();

  @PutMapping("/leaderUpdate/{id}/{name}")
  public ResponseEntity<String> savePersonFromLeader(
      @PathVariable("id") Integer id, @PathVariable("name") String name) {

    Person person = new Person(id, name);
    DataStorage.setPerson(person);
    return ResponseEntity.ok("SUCCESS");
  }

  @PutMapping("/person/{id}/{name}")
  public ResponseEntity<String> savePerson(
      @PathVariable("id") Integer id, @PathVariable("name") String name) {

    List<String> liveNodes = ClusterInfo.getClusterInfo().getLiveNodes();

    int successCount = 0;
    for (String node : liveNodes) {

      if (getHostPostOfServer().equals(node)) {
        Person person = new Person(id, name);
        DataStorage.setPerson(person);
        successCount++;
      } else {
        String requestUrl =
            "http://"
                .concat(node)
                .concat("leaderUpdate")
                .concat("/")
                .concat(String.valueOf(id))
                .concat("/")
                .concat(name);
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        restTemplate.exchange(requestUrl, HttpMethod.PUT, entity, String.class).getBody();
        successCount++;
      }
    }

    return ResponseEntity.ok()
        .body("Successfully update ".concat(String.valueOf(successCount)).concat(" nodes"));
  }

  @GetMapping("/persons")
  public ResponseEntity<List<Person>> getPerson() {

    return ResponseEntity.ok(DataStorage.getPersonListFromStorage());
  }

  @GetMapping("/clusterInfo")
  public ResponseEntity<ClusterInfo> getClusterinfo() {

    return ResponseEntity.ok(ClusterInfo.getClusterInfo());
  }
}
