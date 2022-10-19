# MVP Spring Boot + Kotlin + Kafka Connect

https://confluence.jsa-group.ru/pages/viewpage.action?pageId=79692600

## Prerequisites
### PostgreSQL
#### 1. Postgres' user should have database rights:
```
GRANT SELECT, RELOAD, SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'user' IDENTIFIED BY 'password';

```
#### 2. WAL level of database ppm-db should be 'logical'.

About logical replication in Postgres see: https://habr.com/ru/company/postgrespro/blog/489308

To change WAL level:

1) Execute PostgreSQL query:
```
ALTER SYSTEM SET wal_level = 'logical'
```
2) Restart Postgres (docker container).

Examples:

a) using existent indexes:
- Only not nullable field should be indexed
- Indexes should be unique
```
ALTER TABLE table_name REPLICA IDENTITY DEFAULT;
ALTER TABLE table_name REPLICA IDENTITY USING INDEX table_name_field_one_field_two_key;
```
b) adding column with replica identity
```
ALTER TABLE table_name ADD COLUMN replica_id bigint GENERATED ALWAYS AS IDENTITY;
```

### Ports should be free:

#### For Kafka Broker

- port: 9092

  onOpen: ignore

#### For Kafka Connect

- port: 8083

  onOpen: ignore

### Deployment of Debezium PostgreSQL connector

https://debezium.io/documentation/reference/1.9/connectors/postgresql.html#postgresql-deployment

### Connector configuration rules:

Prefix with CONNECT_.

Convert to upper-case.

Separate each word with _.

Replace a period (.) with a single underscore (_).

Replace a dash (-) with double underscores (__).

Replace an underscore (_) with triple underscores (___).


## How to start Application

#### 1. Run docker compose with -d option to run in detached mode:

```
docker compose up -d

```
#### 2. Verify that the services are up and running:
```
docker compose ps

```
#### 3. After a few minutes, if the state of any component isn’t Up, run the docker compose up -d command again, or try:
```
docker compose restart <image-name>

```

If you start the MVP first time you also need restart docker compose for apply changes of database log level.

#### 4. Check connectors list
```
curl  -X GET http://localhost:8083/connectors | jq
```
#### 5. Check connector status
```
curl  -X GET http://localhost:8083/connectors/postgresql-source-connector/status | jq
```
#### 6. Connect to the broker bash console
```
docker exec -it broker bash 
```
#### 8. Insert/update/delete entry in the table 'app_user' of database user_db
#### 9. In the broker bash console check broker topics
```
[appuser@broker ~]$ kafka-topics --bootstrap-server broker:9092 --list
__consumer_offsets
_confluent-monitoring
app_user_db.public.app_user
docker-connect-configs
docker-connect-offsets
docker-connect-status

```
Broker should return topic created by Connector:
```app_ppm_db.public.project```
#### 10. In the broker bash console check messages in the topic app_user_db.public.app_user
``` 
[appuser@broker ~]$ kafka-console-consumer --topic app_user_db.public.app_user --from-beginning --bootstrap-server localhost:9092
```
Broker should return messages created by Connector:
```
{
  "before": null,
  "after": {
    "id": 2
  },
  "source": {
    "version": "1.9.3.Final",
    "connector": "postgresql",
    "name": "app_user_db",
    "ts_ms": 1666157861373,
    "snapshot": "true",
    "db": "user_db",
    "sequence": "[null,\"23051216\"]",
    "schema": "public",
    "table": "app_user",
    "txId": 501,
    "lsn": 23051216,
    "xmin": null
  },
  "op": "r",
  "ts_ms": 1666157861374,
  "transaction": null
}
```
#### 12. Start Application
After start application will read Kafka topic app_user_db.public.app_user, and print messages to the console.
