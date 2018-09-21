# README #

Cqrs example application

## Rest Api Documentation

Test using http://petstore.swagger.io/ and replace the swagger.json with:
- http://APP_READ_HOST:APP_READ_PORT/api-docs/swagger.json
- http://APP_WRITE_HOST:APP_WRITE_PORT/api-docs/swagger.json

## Configuration

You can specify environment variables via .env file (sample file in main directory)

* **Application**

    `VERSION`= Version from build.sbt (0.1.0)

    `APP_READ_HOST`= Host address for read side application <br/>
    `APP_READ_PORT`= TCP port for read side application

    `ELASTIC_PROTOCOL`= Http or Https <br/>
    `ELASTIC_HOST`= Host address for elasticsearch instance <br/>
    `ELASTIC_PORT`= TCP port for elasticsearch instance
    
    `DB_USER`= Mysql database username <br/>
    `DB_PASSWORD`= Mysql database user password <br/>
    `DB_NAME`= Mysql database name <br/>
    `DB_URL`= Mysql database address
    
    `CLUSTER_EVENT_HOST`= Host address for event sourcing module in akka cluster <br/>
    `CLUSTER_EVENT_PORT`= TCP port for event sourcing module in akka cluster
    
    `APP_WRITE_HOST`= Host address for write side application <br/>
    `APP_WRITE_PORT`= TCP port for write side application

    `CLUSTER_WRITE_HOST`= Host address for write module in akka cluster <br/>
    `CLUSTER_WRITE_PORT`= TCP port for write module in akka cluster
    
    `CLUSTER_SEED_WRITE_ADDRESS`= Full address for write module in akka cluster <br/>
    `CLUSTER_SEED_EVENT_ADDRESS`= Full address for event sourcing module in akka cluster
          
## Running 

#### Locally

```sbt cqrs-write/run``` or/and ```sbt cqrs-read/run```

#### Docker

* **Publish Image**

```sbt docker```

This publish image to local repository.

|       REPOSITORY       |  TAG  |   IMAGE ID   |      CREATED       |  SIZE |
| ---------------------- | ----- | ------------ | ------------------ | ----- |
| com.example/cqrs-event | 0.1.0 | 6b68939a7337 | About a minute ago | 499MB |
| com.example/cqrs-write | 0.1.0 | 6ab19ac09b90 | About a minute ago | 501MB |
| com.example/cqrs-read  | 0.1.0 | d6c7c85c94f8 | About a minute ago | 504MB |
|        openjdk         | 8-jre | 66bf39162ea7 | About a minute ago | 443MB |

* **Run Containers**

```docker-compose up```

```docker ps``` 

| CONTAINER ID |              IMAGE           |          PORTS         |                 NAMES             |
| ------------ | ---------------------------- | ---------------------- | --------------------------------- |
| 17d830c1499a | com.example/cqrs-event:0.1.0 | 0.0.0.0:2552->2552/tcp | cqrs-example_cqrs.event.service_1 |
| 963dc10d6093 | com.example/cqrs-read:0.1.0  | 0.0.0.0:8160->8160/tcp | cqrs-example_cqrs.read.service_1  |
| 963dc10d6093 | com.example/cqrs-write:0.1.0 | 0.0.0.0:8170->8170/tcp | cqrs-example_cqrs.write.service_1 |
| 60202fa008bf | elasticsearch:latest         | 0.0.0.0:9200->9200/tcp | cqrs-example_cqrs.mysql_1         |
| 60202fa008bf | mysql:5.7.22                 | 0.0.0.0:3308->3306/tcp | cqrs-example_cqrs.elasticsearch_1 |
