# Financial statistics collector

## Requirements

You should have running Zookeeper and Kafka servers on your enviroment. I have test it with Kakfa 0.9.0.0, but It should work under 0.8.2.1 version also.

You will need also a mondodb instance runnin. I have test it on version 3.2.4. 

You will find a resources folder in main and test, inside of it there is an application.conf file. This is the place where you will change the configurations to ajust them to your enviroment. An example would be the kafka host and port.

Make sure to create the topic that you configured. You can find some related documentation [here]( http://kafka.apache.org/documentation.html#quickstart_createtopic)

## Note
You will see that SimpleKafkaConsumerTest test is commented, It is because was hanging when is ran with DataCollectorSpec
