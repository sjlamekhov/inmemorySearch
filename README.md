inmemorySearch
simple search engine with REST API that stores data in memory in column-oriented way
(with ability to make dumps to disk - to be implemented)

types of requests:
    EQ - equal strings
    GT - greater than
    LT - lower than
    NE - not equals
    STWITH - startsWith
    CLOSE_TO - find all attribute values that have edit distance <= 3
    CONTAINS - find all attribute values that contain specified string
    LENGTH - find all attribute values by length

add document to index:
curl --location --request POST 'localhost:8080/testTenant/search/index' \
--header 'Content-Type: application/json' \
--data-raw '{
	"uri": {
		"id": "1",
		"tenantId": "testTenant"
	},
	"attributes": {
		"attribute1": "value1"
	}
}'

query document:
curl --location --request GET 'localhost:8080/testTenant/search?request=(attribute1,EQ,value1)' \
--header 'Content-Type: application/json' \
--data-raw '{
	"uri": {
		"id": "1",
		"tenantId": "testTenant"
	},
	"attributes": {
		"attribute1": "value1"
	}
}'

get document by id = 1:
curl --location --request GET 'localhost:8080/testTenant/search/getById/1' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=BB19B70FFBAB863B5DC6F100D43E7AF6' \
--data-raw '{
	"uri": {
		"id": "1",
		"tenantId": "testTenant"
	},
	"attributes": {
		"attribute1": "value1"
	}
}'