startApp:
	./gradlew build & ./gradlew run

getKey:
	curl http://127.0.0.1:$(PORT)/get?key=$(KEY)

putKeyValue:
	curl -X POST http://127.0.0.1:$(PORT)/put?key=$(KEY)%value=$(VALUE)

deleteKey:
	curl -X DELETE http://127.0.0.1:$(PORT)/delete?key=$(KEY)