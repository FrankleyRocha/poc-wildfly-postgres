# poc-wildfly-postgres

A wildfly postgres example with maven plugin

Based and adpted from: https://www.wildfly.org/guides/database-integrating-with-postgresql

1) Start database with docker/podman
* You can create a `startdb.sh` script if you want

```bash
docker run --rm --name bookstore \
  -p 5432:5432 \
  -e POSTGRES_PASSWORD=admin \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_DB=bookstore_db \
  docker.io/library/postgres
```

2) Create a bookstore project from archetype `wildfly-getting-started-archetype`
```bash
mvn archetype:generate \
    -DarchetypeGroupId=org.wildfly.archetype \
    -DarchetypeArtifactId=wildfly-getting-started-archetype \
    -DdefaultClassPrefix=BookStorex \
    -DartifactId=bookstorex \
    -Dversion=1.0.0 \
    -DinteractiveMode=false
```

3) Remove the following files from the base project since we are not going to use them
```bash
cd bookstore
rm src/test/java/org/wildfly/examples/BookStoreApplicationIT.java
rm src/test/java/org/wildfly/examples/BookStoreServiceIT.java
```

4) Update your pom.xml, add `jakarta.jakartaee-api` and comments `jakarta.enterprise.cdi-api` and `jakarta.ws.rs-api`
```xml
    <dependencies>

        <!-- https://mvnrepository.com/artifact/jakarta.platform/jakarta.jakartaee-api -->
        <dependency>
            <groupId>jakarta.platform</groupId>
            <artifactId>jakarta.jakartaee-api</artifactId>
            <version>10.0.0</version>
            <scope>provided</scope>
        </dependency>

        <!-- Import the CDI API, we use provided scope as the API is included in WildFly -->
        <!-- <dependency>
            <groupId>jakarta.enterprise</groupId>
            <artifactId>jakarta.enterprise.cdi-api</artifactId>
            <scope>provided</scope>
        </dependency> -->

        <!-- Import the JAX-RS API, we use provided scope as the API is included in WildFly -->
        <!-- <dependency>
            <groupId>jakarta.ws.rs</groupId>
            <artifactId>jakarta.ws.rs-api</artifactId>
            <scope>provided</scope>
        </dependency> -->
```

5) Add debug capability for mvn wildfly:dev

* debug to configuration section in `wildfly-maven-plugin` plugin

```xml
<debug>true</debug>
<debugPort>8787</debugPort>
```

6) Instruct Wildfly Glow to discover automatically the required Galleon Layers for our application

* The discover-provisioning-info configuration tells the plugin to discover the required layers by inspecting our application code. By using the postgresql:default addon, we are specifying we want to use a PostgreSQL database, and we want to configure it as the default datasource for the server.

```xml
<discover-provisioning-info>
    <addOns>
        <addOn>postgresql:default</addOn>
    </addOns>
</discover-provisioning-info>
```

* Your `wildfly-maven-plugin` plugin might look like this
```xml
<!-- The WildFly plugin deploys your war to a local JBoss AS container -->
<plugin>
    <groupId>org.wildfly.plugins</groupId>
    <artifactId>wildfly-maven-plugin</artifactId>
    <version>${version.wildfly.maven.plugin}</version>
    <configuration>

        <discover-provisioning-info>
            <addOns>
                <addOn>postgresql:default</addOn>
            </addOns>
        </discover-provisioning-info>

        <debug>true</debug>
        <debugPort>8787</debugPort>

        <feature-packs>
            <feature-pack>
                <location>org.wildfly:wildfly-galleon-pack:${version.wildfly.bom}</location>
            </feature-pack>
        </feature-packs>
        <layers>
            <!-- layers may be used to customize the server to provision-->
            <layer>cloud-server</layer>
        </layers>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>package</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

7) Create the following `persistence.xml` file in the src/main/resources/META-INF directory:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<persistence xmlns="https://jakarta.ee/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd"
             version="3.0">
    <persistence-unit name="bookstore-PU">
        <properties>
            <property name="jakarta.persistence.schema-generation.database.action" value="drop-and-create"/>
        </properties>
    </persistence-unit>
</persistence>
```

* We don’t need to specify the name of the Datasource by using `<jta-data-source>`. In absence of this property, Jakarta Persistence will use the default datasource configured in the server.

8) The BookStoreApplication class acts as a configuration class for the Jakarta REST application. It essentially tells the WildFly runtime that this is a Jakarta REST application and provides the base path for the application’s RESTful web services.

* Modify it as follows to specify /api as the base URL for our Jakarta REST Web Service:

```java
package org.wildfly.examples;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
public class BookStoreApplication extends Application {
}
```

9) Add Lombok to `dependencies` section of `pom.xml`

```xml
<!-- https://mvnrepository.com/artifact/org.projectlombok/lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.38</version>
    <scope>provided</scope>
</dependency>
```