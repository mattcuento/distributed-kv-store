startKVServer:
	./gradlew build & ./gradlew run

startMaster:
	java -cp myapp.jar com.kvstore.app.MasterCoordinator

startWorkers:
	java -cp myapp.jar com.kvstore.server.ExternalHttpServer &
	java -cp myapp.jar com.kvstore.server.InternalTcpServer &

monitorSystem:
	curl http://127.0.0.1:8080/status