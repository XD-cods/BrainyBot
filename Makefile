dBuild:
	docker buildx build --build-arg MONGO_URL="mongodb+srv://vkuzir7:Y!K8WELgPupmYd@quizbot.sintcow.mongodb.net/?retryWrites=true&w=majority&appName=QuizBot" \
                        --build-arg TELEGRAM_USER_TOKEN="6683005363:AAFHknGfItPK9EeiiwQmeHMb5t5M_lgh-LM" \
                        --build-arg TELEGRAM_ADMIN_TOKEN="6982500112:AAFBoXfRd1XzXsFncJgrEUoJ-Bis8m2Mq7k" \
                        --build-arg DATABASE_NAME="QuizBot" \
                        -t bot .
mBuild:
	mvn clean package -e
run:
	java -jar target/knowBot-1.0-SNAPSHOT-jar-with-dependencies.jar
