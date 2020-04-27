# inmemorySearch

simple search engine with REST API that stores data in memory in column-oriented way
(~~with ability to make dumps to disk~~ - to be implemented)

## key features
* support of complex requests
* request optimizer (meaningless requests are not executed)
* request results can be added to cache that rebuilds on add to index of new objects
* replication with gossip protocol (on protoBuf)
* sharding

## types of supported requests:
* EQ - equal strings
* GT - greater than
* LT - lower than
* NE - not equals
* STWITH - startsWith
* EDIIT_DIST3 - find all attribute values that have edit distance <= 3
* CONTAINS - find all attribute values that contain specified string
* LENGTH - find all attribute values by length

## request examples
### add document to index:
```
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
```

### query document with attribute1 = "value1":
```
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
```

### query document with attribute1 = "value1" AND attribute2 = "value2":
```
curl --location --request GET 'localhost:8080/testTenant/search?request=((attribute1,EQ,value1)AND(attribute2,EQ,value2))' \
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
```

### get document by id = 1:
```
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
```