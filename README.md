# README #

Cqrs example application

## Configuration

You can specify environment variables via .env file (sample file in main directory)

* **Application** <br/> 
    `APP_HOST`=[string]'Host address for application' <br/>
    `APP_PORT`=[int]'TCP port for application' 

    `ELASTIC_PROTOCOL`=[string]'Http or Https' <br/>
    `ELASTIC_HOST`=[string]'Host address for elasticsearch instance' 
    `ELASTIC_PORT`=[int]'TCP port for elasticsearch instance' <br/>
    
    `$DB_USER`=[string]'Database username'   
    `$DB_PASSWORD`=[string]'Database user password'   
    `$DB_NAME`=[string]'Database name'   
    `$DB_URL`=[string]'Database address' 
          
## Running 

#### Locally

`sbt run`

#### on Docker

`sbt dockerComposeUp`

This run container in docker.

`docker ps` 

| CONTAINER ID |           IMAGE        |       STATUS      |          PORTS         |               NAMES           |
| :----------: | :------------------- : | :---------------: | :--------------------: | :---------------------------: |
| 963dc10d6093 | cqrs-example:latest    | Up About a minute | 0.0.0.0:8160->8160/tcp | 428518_cqrs.service_1         |
| 60202fa008bf | elasticsearch:latest   | Up About a minute | 0.0.0.0:9200->9200/tcp | 428518_cqrs.elasticsearch_1   |
| 60202fa008bf | mysql:5.7.22           | Up About a minute | 0.0.0.0:3308->3306/tcp | 428518_cqrs.mysql_1           |

## Stopping

#### Locally

`ctrl + c`

#### on Docker

`sbt dockerComposeStop`