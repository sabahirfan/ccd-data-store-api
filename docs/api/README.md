## API Documentation

### Generation

The documentation is generated using [swagger-codegen](https://github.com/swagger-api/swagger-codegen).

Installation on MacOS: `brew install swagger-codegen`

#### Pre-requisite

`case-data-store-app` application must be running locally.

#### Generate latest

Run:
```
swagger-codegen generate -i http://localhost:4452/v2/api-docs.json -l html2 -o docs/api/latest/
```
