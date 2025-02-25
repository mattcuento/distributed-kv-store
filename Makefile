startApp:
	./gradlew build & ./gradlew run

getKey:
	curl http://127.0.0.1:$(PORT)/get?key=$(KEY)

putKeyValue:
	curl -X POSThttp://127.0.0.1:$(PORT)/put?key=$(KEY)%value=$(VALUE)
