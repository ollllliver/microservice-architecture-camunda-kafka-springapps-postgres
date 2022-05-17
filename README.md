# External Task Client Spring Boot Starter: Order Handling Example

This example demonstrates how to use the Spring Boot Starter to ...
* ... configure the External Task Client and topic subscriptions
* ... subscribe to topics so the Client can fetch and lock for External Tasks
* ... execute custom business logic defined in a handler bean for each fetched External Task

## Why is this example interesting?

This example shows how to annotate a `@EnableExternalTaskClient` class to bootstrap an External Task Client 
and subscribe to topics to execute custom business logic using the `@ExternalTaskSubscription` annotation.
Additionally, it shows how to auto-wire a `SpringTopicSubscription` bean as well as open and close a
subscription after the application has been started.

> This example is based on the [External Task Client Spring Boot Starter: Order Handling Example](https://github.com/camunda/camunda-bpm-examples/tree/master/spring-boot-starter/external-task-client/order-handling-spring-boot).

## Please show me the important parts!

Let's first add the dependency to the project's `pom.xml` file:
```xml
<!--...-->
<dependency>
  <groupId>org.camunda.bpm.springboot</groupId>
  <artifactId>camunda-bpm-spring-boot-starter-external-task-client</artifactId>
  <version>7.17.0</version>
</dependency>
<!--...-->
```

Second, we create an `Application` class and annotate it with `@SpringBootApplication` to bootstrap 
the External Task Client:

```java
@SpringBootApplication
public class Application {

  public static void main(String... args) {
    SpringApplication.run(Application.class, args);
  }

}
```

To configure the External Task Client, we create an `application.yml` file:
```yaml
camunda.bpm.client:
  base-url: http://localhost:8080/engine-rest # The URL pointing to the Camunda Platform Runtime REST API
  async-response-timeout: 1000 # Defines the maximum duration of the long-polling request
  worker-id: spring-boot-client # Identifies the worker towards the Engine
```

Next, we create a `FirstTask` class to subscribe to topics and add our custom 
business logic by defining a bean with the return type `ExternalTaskHandler` and add the 
`@ExternalTaskSubscription` annotation to the bean method. The lambda function's body contains 
our custom business logic that is executed when an External Task is fetched:

```java
@Configuration
public class FirstTask {

  protected static final Logger LOG = LoggerFactory.getLogger(FirstTask.class);

  @Bean
  @ExternalTaskSubscription("myFirstServiceTask")
  public ExternalTaskHandler firstTaskHandler() {
    return (externalTask, externalTaskService) -> {

      LOG.info("HandlerConfiguration");


      Map<String, Object> variables = new HashMap<>();

      // select the scope of the variables
      boolean isRandomSample = Math.random() <= 0.5;
      if (isRandomSample) {
        variables.put("test", "lalala");
      } else {
        variables.put("test", "blablabla");
      }
      
      externalTaskService.complete(externalTask, variables);

      LOG.info("The External Task {} has been completed!", externalTask.getId());

    };
  }

  // There could be multiple ExternalTaskSubscription in this one file,
  // but it might be clearer to seperate it to different classes.

  // @Bean
  // @ExternalTaskSubscription("mySecondServiceTask")
  // public ExternalTaskHandler secondTaskHandler() {
  //   return (externalTask, externalTaskService) -> {

  //     [...]

  //   };
  // }

}
```

In the `Subscriptions` class we auto-wire the subscription beans and can access the configuration
of the beans. 

## How to use it?

1. Make sure to have an up and running Camunda Platform Runtime REST API on 8080
2. Deploy the process [demo-process.bpmn](./demo-process.bpmn) to the Camunda Platform Runtime (e.g., via Camunda Modeler)
3. Start the main class in your IDE
4. Start a process in [Camunda Web Interface](http://localhost:8080/camunda/app/tasklist/default/)
5. Watch out for the following log output:

```
   ____                                           _             ____    _           _      __
  / ___|   __ _   _ __ ___    _   _   _ __     __| |   __ _    |  _ \  | |   __ _  | |_   / _|   ___    _ __   _ __ ___      
 | |      / _` | | '_ ` _ \  | | | | | '_ \   / _` |  / _` |   | |_) | | |  / _` | | __| | |_   / _ \  | '__| | '_ ` _ \     
 | |___  | (_| | | | | | | | | |_| | | | | | | (_| | | (_| |   |  __/  | | | (_| | | |_  |  _| | (_) | | |    | | | | | |    
  \____|  \__,_| |_| |_| |_|  \__,_| |_| |_|  \__,_|  \__,_|   |_|     |_|  \__,_|  \__| |_|    \___/  |_|    |_| |_| |_|    

  _____          _                                   _     _____                 _         ____   _   _                  _   
 | ____| __  __ | |_    ___   _ __   _ __     __ _  | |   |_   _|   __ _   ___  | | __    / ___| | | (_)   ___   _ __   | |_ 
 |  _|   \ \/ / | __|  / _ \ | '__| | '_ \   / _` | | |     | |    / _` | / __| | |/ /   | |     | | | |  / _ \ | '_ \  | __|
 | |___   >  <  | |_  |  __/ | |    | | | | | (_| | | |     | |   | (_| | \__ \ |   <    | |___  | | | | |  __/ | | | | | |_ 
 |_____| /_/\_\  \__|  \___| |_|    |_| |_|  \__,_| |_|     |_|    \__,_| |___/ |_|\_\    \____| |_| |_|  \___| |_| |_|  \__|

  Spring-Boot:  (v2.6.4)
  Camunda Platform: (v7.17.0)
2022-05-17 13:32:26.077  INFO 38932 --- [           main] o.c.bpm.spring.boot.example.Application  : Starting Application using Java 11.0.15.1 

[...]

2022-05-17 13:32:27.188  INFO 38932 --- [           main] o.c.b.spring.boot.example.Subscriptions  : Subscription bean 'firstTaskHandlerSubscription' has topic name: myFirstServiceTask 
2022-05-17 13:32:27.188  INFO 38932 --- [           main] o.c.b.spring.boot.example.Subscriptions  : Subscription bean 'secondTaskHandlerSubscription' has topic name: mySecondServiceTask 
2022-05-17 13:32:27.255  INFO 38932 --- [           main] o.c.bpm.spring.boot.example.Application  : Started Application in 1.829 seconds (JVM running for 2.713)
2022-05-17 13:32:27.260  INFO 38932 --- [           main] o.c.b.spring.boot.example.Subscriptions  : Subscription with topic name 'myFirstServiceTask' initialized
2022-05-17 13:32:27.260  INFO 38932 --- [           main] o.c.b.spring.boot.example.Subscriptions  : Subscription with topic name 'mySecondServiceTask' initialized
2022-05-17 13:32:34.043  INFO 38932 --- [criptionManager] o.c.b.s.b.example.servietasks.FirstTask  : HandlerConfiguration
2022-05-17 13:32:34.065  INFO 38932 --- [criptionManager] o.c.b.s.b.example.servietasks.FirstTask  : The External Task 0b82e243-d5d5-11ec-9353-0242ac110002 has been completed!
2022-05-17 13:32:34.078  INFO 38932 --- [criptionManager] o.c.b.s.b.e.servietasks.SecondTask       : SecondTask
2022-05-17 13:32:34.091  INFO 38932 --- [criptionManager] o.c.b.s.b.e.servietasks.SecondTask       : The External Task 0b93841a-d5d5-11ec-9353-0242ac110002 has been completed!

```