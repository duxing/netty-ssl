# netty-SSL

## Run

### Environment Variables

 - `URL`: URL to run test with. Default to `https://google.com`.
 - `JAVA11`: Set to `true` to use `java 11`, otherwise use `java 8`. Default to `false`.
 - `CERT`: Set to `false` to bypass certificate chain check. Default to `true`.

### Make Commands

Run with java11
 - `JAVA11=true make run`

Run with java8
 - `make run`

## Steps to Reproduce SSL Exception

 - `SSL` works on `java 8`: `make run`
 - `SSL` works on `java 11` without checking certificate: `JAVA11=true CERT=false make run`
 - `SSL` fails on `java 11` with certificate checks: `JAVA11=true make run`
