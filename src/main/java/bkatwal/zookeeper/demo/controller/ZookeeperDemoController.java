package bkatwal.zookeeper.demo.controller;

import static bkatwal.zookeeper.demo.util.ZkDemoUtil.getHostPostOfServer;
import static bkatwal.zookeeper.demo.util.ZkDemoUtil.isEmpty;

import bkatwal.zookeeper.demo.model.Person;
import bkatwal.zookeeper.demo.util.ClusterInfo;
import bkatwal.zookeeper.demo.util.DataStorage;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
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

  @PutMapping("/person/{id}/{name}")
  public ResponseEntity<String> savePerson(
      HttpServletRequest request,
      @PathVariable("id") Integer id,
      @PathVariable("name") String name) {

    String requestFrom = request.getHeader("request_from");
    String leader = ClusterInfo.getClusterInfo().getMaster();
    if (!isEmpty(requestFrom) && requestFrom.equalsIgnoreCase(leader)) {
      Person person = new Person(id, name);
      DataStorage.setPerson(person);
      return ResponseEntity.ok("SUCCESS");
    }
    // If I am leader I will broadcast data to all live node, else forward request to leader
    if (amILeader()) {
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
                  .concat("person")
                  .concat("/")
                  .concat(String.valueOf(id))
                  .concat("/")
                  .concat(name);
          HttpHeaders headers = new HttpHeaders();
          headers.add("request_from", leader);
          headers.setContentType(MediaType.APPLICATION_JSON);

          HttpEntity<String> entity = new HttpEntity<>(headers);
          restTemplate.exchange(requestUrl, HttpMethod.PUT, entity, String.class).getBody();
          successCount++;
        }
      }

      return ResponseEntity.ok()
          .body("Successfully update ".concat(String.valueOf(successCount)).concat(" nodes"));
    } else {
      String requestUrl =
          "http://"
              .concat(leader)
              .concat("person")
              .concat("/")
              .concat(String.valueOf(id))
              .concat("/")
              .concat(name);
      HttpHeaders headers = new HttpHeaders();

      headers.setContentType(MediaType.APPLICATION_JSON);

      HttpEntity<String> entity = new HttpEntity<>(headers);
      return restTemplate.exchange(requestUrl, HttpMethod.PUT, entity, String.class);
    }
  }

  private boolean amILeader() {
    String leader = ClusterInfo.getClusterInfo().getMaster();
    return getHostPostOfServer().equals(leader);
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
