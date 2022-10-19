# Launch Kafka Connect
echo '----> Launching Kafka Connect worker...' &&
/etc/confluent/docker/run &
#
# Wait for Kafka Connect listener
while [ $(curl -s -o /dev/null -w %{http_code} http://localhost:8083/connectors) -eq 000 ] ; do
  echo -e $(date) " Kafka Connect listener HTTP state: " $(curl -s -o /dev/null -w %{http_code} http://localhost:8083/connectors) " (waiting for 200)"
  sleep 5
done
#
# Create source connector
curl -X POST http://localhost:8083/connectors  -H "Content-Type: application/json" -d '{
 "name": "postgresql-source-connector",
   "config": {
     "name": "postgresql-source-connector",
     "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
     "database.hostname": "host.docker.internal",
     "database.port": "5433",
     "database.user": "postgres",
     "database.password": "postgres",
     "database.dbname" : "user_db",
     "database.server.name": "app_user_db",
     "snapshot.mode": "initial",
     "table.include.list": "public.app_user",
     "time.precision.mode": "connect",
     "tasks.max": "1",
     "plugin.name": "pgoutput",
     "column.include.list": "public.app_user.id"
   }
}'
#
# We no need to stop container
sleep infinity
