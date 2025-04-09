docker run --rm --name bookstore \
  -p 5432:5432 \
  -e POSTGRES_PASSWORD=admin \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_DB=bookstore_db \
  docker.io/library/postgres