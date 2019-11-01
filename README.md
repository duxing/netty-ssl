# netty-http-server

## Run

 - `make docker_build_web` to build the image
 - `make docker_up` to stand up the http server
 - `make docker_port` to find out what port is used
 - `make docker_logs` to `tail` the container `stdout`

## Test

create a `body.gz` file:
```
echo '{ "key" : "value" }' | gzip > body.gz
```

test http compression and decompression with:
```
curl -s -X POST 'localhost:<port>' -H 'Accept-Encoding: gzip' -H 'Content-Encoding: gzip' --data-binary @body.gz --compressed
```
