# play-java-session-demo

This is a starter application that shows how Play works.  Please see the documentation at https://www.playframework.com/documentation/latest/Home for more details.

## Run cassandra script

We need to execute cassandra cql query which we are using in this project.
1. Download Cassandra.
2. Go to play-java-session-demo\conf\script.cql and exceute query on cassandra.

## Running

Run this using [sbt](http://www.scala-sbt.org/).

```
sbt run
```

And then go to http://localhost:9000 to see the running web application.

## Controllers

There are several demonstration files available in this template.

- HomeController.java:
  Shows how to handle simple HTTP requests.

- ProductController.java
  Shows how to integrate with scala template.
  Shows how to inject a component into a controller and use the component when
    handling requests.
  Show how to integrate web service using WS

- ResultsController.java
  Shows how to integarte multiple Result.

-Validator.java
 Show how to integarte Action composition

-ValidationController.java
  Show how to integrate

