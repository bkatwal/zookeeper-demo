### GitLab [![build status](https://gitlab.com/bikas.katwal10/zookeeper-demo/badges/master/build.svg)](https://gitlab.com/bikas.katwal10/zookeeper-demo/pipelines)

![alt text](https://github.com/bkatwal/zookeeper-demo/blob/master/ZookeeperDemo.png)

## Setup and Usage

1. Install and start Apache Zookeeper in any port. Follow guide: https://zookeeper.apache.org/doc/r3.1.2/zookeeperStarted.html
2. Use below command to start the application in 3 ports:
```
java -Dserver.port=8081 -Dzk.url=localhost:2181 -Dleader.algo=2 -jar target/bkatwal-zookeeper-demo-1.0-SNAPSHOT.jar
java -Dserver.port=8082 -Dzk.url=localhost:2181 -Dleader.algo=2 -jar target/bkatwal-zookeeper-demo-1.0-SNAPSHOT.jar
java -Dserver.port=8081 -Dzk.url=localhost:2181 -Dleader.algo=2 -jar target/bkatwal-zookeeper-demo-1.0-SNAPSHOT.jar
```
`server.port` is spring app server port, `zk.url` is your zookeeper connection string and `leader.algo` if passed `2`, application will use ephemeral sequential znodes for leader election else will use ephemeral znodes.


#### Look at swagger for API documentation for API details, Access swagger UI at `/swagger-ui.html`
`GET /clusterInfo` - This API will display all the nodes in cluster, current list of live nodes and current master.

`GET /persons` - This API will display all the saved Person.

`PUT /person/{id}/{name}` - Use this to save person data.
