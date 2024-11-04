## Trip Planning Application

This is an assignment completion of the written Programming & Technical exam 2024

---

### Task 2.4.2

I have chosen to make my TripDAO and my GuideDAO return entities as I personally find that it's easier to work with and
makes more sense.
This also has the benefit of moving the conversion from DTO to entity and vise versa out of the persistence layer of the
application. I wish to handle
the conversion between DTO and entity in a designated Mapper class that can be used directly in the Controllers.

---

### Task 2.4.4

I have chosen to change the id from being integers to being long as it seems to be a common standard when working
with JPA and databases in general. I am of course aware of the fact that the extra data storage space that long offers
is not always needed.
Especially not in an exam scenario. But it makes more sense to me so I've chosen to use it.

---

### Task 3.3.2

#### GET `/trips`

```json
[
  {
    "id": 1,
    "startTime": [
      9,
      0
    ],
    "endTime": [
      12,
      0
    ],
    "startPosition": "Central Station",
    "name": "Morning City Tour",
    "price": 49.99,
    "category": "SIGHTSEEING",
    "guide": {
      "id": 1,
      "firstName": "John",
      "lastName": "Smith",
      "email": "john.smith@example.com",
      "phone": "+1-555-123-4567",
      "yearsOfExperience": 5
    }
  },
  {
    "id": 2,
    "startTime": [
      14,
      30
    ],
    "endTime": [
      18,
      0
    ],
    "startPosition": "Harbor",
    "name": "Afternoon Boat Cruise",
    "price": 79.99,
    "category": "CRUISE",
    "guide": {
      "id": 1,
      "firstName": "John",
      "lastName": "Smith",
      "email": "john.smith@example.com",
      "phone": "+1-555-123-4567",
      "yearsOfExperience": 5
    }
  },
  {
    "id": 3,
    "startTime": [
      20,
      0
    ],
    "endTime": [
      23,
      0
    ],
    "startPosition": "Old Town Square",
    "name": "Evening Food Tour",
    "price": 59.99,
    "category": "CULINARY",
    "guide": {
      "id": 2,
      "firstName": "Emma",
      "lastName": "Johnson",
      "email": "emma.johnson@example.com",
      "phone": "+1-555-987-6543",
      "yearsOfExperience": 8
    }
  },
  {
    "id": 4,
    "startTime": [
      10,
      0
    ],
    "endTime": [
      16,
      0
    ],
    "startPosition": "National Park Entrance",
    "name": "Hiking Adventure",
    "price": 39.99,
    "category": "ADVENTURE",
    "guide": null
  }
]
```

#### GET `/trips/1`

```json
{
  "id": 1,
  "startTime": [
    9,
    0
  ],
  "endTime": [
    12,
    0
  ],
  "startPosition": "Central Station",
  "name": "Morning City Tour",
  "price": 49.99,
  "category": "SIGHTSEEING",
  "guide": {
    "id": 1,
    "firstName": "John",
    "lastName": "Smith",
    "email": "john.smith@example.com",
    "phone": "+1-555-123-4567",
    "yearsOfExperience": 5
  }
}
```

#### POST `/trips`

```json
{
  "id": 5,
  "startTime": [
    11,
    30
  ],
  "endTime": [
    14,
    30
  ],
  "startPosition": "Vineyard Entrance",
  "name": "Wine Tasting Tour",
  "price": 89.99,
  "category": "CULINARY",
  "guide": null
}
```

#### PUT `/trips/6`

```json
{
  "id": 5,
  "startTime": "11:30:00",
  "endTime": "14:30:00",
  "startPosition": "Vineyard Entrance",
  "name": "Updated trip name",
  "price": 59.99,
  "category": "CULINARY",
  "guide": null
}
```

#### DELETE `/trips/6`

```http request
HTTP/1.1 204 No Content
Date: Mon, 04 Nov 2024 10:07:09 GMT
Content-Type: application/json

<Response body is empty>
```

#### PUT `/trips/6/guides/3`

```http request
HTTP/1.1 204 No Content
Date: Mon, 04 Nov 2024 10:06:00 GMT
Content-Type: application/json

<Response body is empty>
```

#### POST `/trips/populate`

```http request
HTTP/1.1 204 No Content
Date: Mon, 04 Nov 2024 10:08:16 GMT
Content-Type: application/json

<Response body is empty>
```

---

### 3.3.5

The reason for using a PUT method instead of a POST method is because the POST method is used for creating new objects.
The assignment states to add an existing guide to an existing trip, meaning we do not wish to create a new guide.
In this assignment it is clear that we only wish update an existing trip by assigning an existing guide to that trip.
For this purpose, the PUT method is the logical choice as it is update an existing resource.

---

### Task 4.1

The system is robust to handle many errors using its ExceptionController. This controller can handle APIException errors
thrown around in the program from different controllers. It can even handle unhandled exceptions which means exceptions
that were not correctly thrown as APIExceptions.
However if an unhandled exception is caught it will be treated as an Internal Server Error and return that as the
response.

Bellow are two examples of the errors the application can currently handle. A 404 Not Found and a 400 Bad Request.

#### GET `/trips/25`

```json
{
  "status": 404,
  "message": "Trip with id 25 could not be found"
}
```

#### PUT `/trips/1`

```json
{
  "status": 400,
  "message": "{REQUEST_BODY=[ValidationError(message=DESERIALIZATION_FAILED, args={}, value={\n  \"this field does not exist\": \"Hello\"\n}, exception=com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException: Unrecognized field \"this field does not exist\" (class app.dtos.TripDTO), not marked as ignorable (8 known properties: \"endTime\", \"price\", \"id\", \"guide\", \"category\", \"startTime\", \"startPosition\", \"name\"])\n at [Source: REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled); line: 2, column: 33] (through reference chain: app.dtos.TripDTO[\"this field does not exist\"]))]}"
}
```

---

### Task 5.1

#### GET `/trips/categories/SIGHTSEEING`

```json
[
  {
    "id": 1,
    "startTime": "09:00:00",
    "endTime": "12:00:00",
    "startPosition": "Central Station",
    "name": "Morning City Tour",
    "price": 49.99,
    "category": "SIGHTSEEING",
    "guide": {
      "id": 1,
      "firstName": "John",
      "lastName": "Smith",
      "email": "john.smith@example.com",
      "phone": "+1-555-123-4567",
      "yearsOfExperience": 5
    }
  }
]
```

---

### Task 5.2

#### GET `/trips/guides/totalprice`

```json
[
  {
    "guideId": 1,
    "totalPrice": 129.98000000000002
  },
  {
    "guideId": 2,
    "totalPrice": 59.99
  }
]
```

---

### Task 6.2

I choose to create a TripService as I see it more fit for a service class to fetch from an external API and then use
that service class in the TripController class.
This helps to organize code and prevents TripController from becoming too overwhelming.

---

### Task 6.3

Because the mentioned categories in the task were not mentioned sooner, I was forced to write some conversion code from
my existing trip categories to the packing item list categories.
The conversion simply takes my trip category, converts its ordinal and uses that as the index for a String array of
packing list categories.
I could have tried to match my trip categories to the packing categories or even have converted all my trip categories
to packing categories, but that would have been too much work for a single task.

---

### Includes:

- Jackson
- Lombok
- Hibernate
- Javalin
- Nimbus
- JBCrypt
- Logback
- JUnit5
- Hamcrest
- RestAssured
- TestContainers