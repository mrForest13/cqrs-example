# README #

Cqrs example application

## Rest Api Documentation

Test using http://petstore.swagger.io/ and replace the swagger.json with:
- http://APP_READ_HOST:APP_READ_PORT/api-docs/swagger.json
- http://APP_WRITE_HOST:APP_WRITE_PORT/api-docs/swagger.json

## Configuration

You can specify environment variables via .env file (sample file in main directory)

* **Application**

    `VERSION`='Version from build.sbt (0.1.0)'

    `APP_READ_HOST`='Host address for read side application'
    `APP_READ_PORT`='TCP port for read side application'

    `ELASTIC_PROTOCOL`='Http or Https' <br/>
    `ELASTIC_HOST`='Host address for elasticsearch instance'
    `ELASTIC_PORT`='TCP port for elasticsearch instance'
    
    `DB_USER`='Mysql database username'   
    `DB_PASSWORD`='Mysql database user password'   
    `DB_NAME`='Mysql database name'   
    `DB_URL`='Mysql database address' 
    
    `CLUSTER_READ_APP_PORT`='TCP port for read side application in akka cluster'
    
    `APP_WRITE_HOST`='Host address for write side application'
    `APP_WRITE_PORT`='TCP port for write side application'
    
    `CLUSTER_WRITE_APP_PORT`='TCP port for write side application in akka cluster'
    
    `CLUSTER_SEED_WRITE_ADDRESS`='Full address for write side application in akka cluster'
    `CLUSTER_SEED_READ_ADDRESS`='Full address for read side application in akka cluster'
          
## Running 

#### Locally

`sbt cqrs-write/run` or/and `sbt cqrs-read/run`

#### Docker

* **Publish Image**

`sbt docker`

This publish image to local repository.

|       REPOSITORY       |  TAG  |   IMAGE ID   |      CREATED       |  SIZE |
| ---------------------- | ----- | ------------ | ------------------ | ----- |
| com.example/cqrs-write | 0.1.0 | 6ab19ac09b90 | About a minute ago | 501MB |
| com.example/cqrs-read  | 0.1.0 | d6c7c85c94f8 | About a minute ago | 504MB |
|        openjdk         | 8-jre | 66bf39162ea7 | About a minute ago | 443MB |

* **Run Containers**

`docker-compose up` 

`docker ps` 

| CONTAINER ID |           IMAGE        |       STATUS      |          PORTS         |               NAMES           |
| ------------ | ---------------------- | ----------------- | ---------------------- | ----------------------------- |
| 963dc10d6093 | cqrs-example:latest    | Up About a minute | 0.0.0.0:8160->8160/tcp | 428518_cqrs.service_1         |
| 963dc10d6093 | cqrs-example:latest    | Up About a minute | 0.0.0.0:8160->8160/tcp | 428518_cqrs.service_1         |
| 60202fa008bf | elasticsearch:latest   | Up About a minute | 0.0.0.0:9200->9200/tcp | 428518_cqrs.elasticsearch_1   |
| 60202fa008bf | mysql:5.7.22           | Up About a minute | 0.0.0.0:3308->3306/tcp | 428518_cqrs.mysql_1           |

## Stopping

#### Locally

`ctrl + c`

#### on Docker

`ctrl + c`