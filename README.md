## Trip Planning Application

This is an assignment completion of the written Programming & Technical exam 2024

---

### Task 2.4.2

I have chosen to make my TripDAO and my GuideDAO return entities as I personally find that it's easier to work with and
makes more sense.
This also has the benefit of moving the conversion from DTO to entity and vise versa out of the persistence layer of the
application. I wish to handle
the conversion between DTO and entity in a designated Mapper class that can be used directly in the Controllers.

### Task 2.4.4

I have chosen to change the id from being integers to being long as it seems to be a common standard when working
with JPA and databases in general. I am of course aware of the fact that the extra data storage space that long offers
is not always needed.
Especially not in an exam scenario. But it makes more sense to me so I've chosen to use it.

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