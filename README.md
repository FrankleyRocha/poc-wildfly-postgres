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
    -DdefaultClassPrefix=BookStore \
    -DartifactId=bookstore \
    -Dversion=1.0.0 \
    -DinteractiveMode=false
```

3) Remove the following files from the base project since we are not going to use them
```bash
cd bookstore
rm src/main/java/org/wildfly/examples/BookStoreService.java
rm src/main/java/org/wildfly/examples/BookStoreEndpoint.java
rm src/test/java/org/wildfly/examples/BookStoreApplicationIT.java
rm src/test/java/org/wildfly/examples/BookStoreServiceIT.java
```