mBuild:
	mvn clean -Dmaven.test.skip  package -e
run:
	java -jar target/knowBot-1.0-SNAPSHOT-jar-with-dependencies.jar
doc:
	docker-compose up -d --build