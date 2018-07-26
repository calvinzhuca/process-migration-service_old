export KIE_CONTEXT_ROOT=kie-server/
export KIESERVER_SERVICE_PORT=8180
export KIESERVER_SERVICE_HOST=localhost
export KIE_SERVER_USER=executionUser
export KIE_SERVER_PWD=password
mvn clean package
java -jar ./target/process-migration-thorntail.jar -Dswarm.port.offset=200

