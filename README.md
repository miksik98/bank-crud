# BANK CRUD

Simple HttpServer app to perform CRUD operations on the clients of bank.

## Endpoints
- GET /clients | get clients with pagination, filters and sorts | parameters: 
    - (required) size = page size
    - (required) page = number of page
    - adult = flag indicating if we want to get only adults or only children
    - sortBy = indication of sorting, possibilities: name, surname, birthYear, money
    - sort = asc (default) or desc
- POST /clients | create client with money=0 | parameters:
    - (required) name
    - (required) surname
    - (required) birthYear
- GET /clients/:id | get client with id=:id
- POST /clients/:id | update client with additional money | parameters:
    - (required) money = money to add, can be negative also
- DELETE /clients/:id | delete client with id=:id
- GET /metrics | returns metrics in text format for Prometheus


## Modules
1. api - provides basic, abstract traits
2. bank - implements api
3. rest - provides rest api using bank implementations

## How to run
docker-compose up -d

sbt run
