# Clickroom-Backend
Backend of a room-lending system in the form of a web application called "Click Room".

## 🛠️ Technology Used
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJIDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)

## Objects
- ### Room
    - id : long
    - capacity : int
    - location : string
- ### Schedule
    - id : long
    - borrowDate : string (2023-08-25)
    - startTime : string (10:30)
    - endTime : string (12:00)
    - lendee : string
    - lender : string
    - detail : string
    - roomId : long
- ### Facility
    - id : long
    - facilityName : string
    - amount : int
    - roomId : long

## Endpoints ("/api/v1/")
### /room

#### GET

* #### Returns all rooms
```
/api/v1/room
```
Response Status Code : 200
> [  
> {"id": 100, "capacity": 50, "location": "fmipa"},  
> {"id": 101, "capacity": 40, "location": "fk"},  
> {"id": 102, "capacity": 30, "location": "fh"}  
> ]

* #### Return a single room
```
/api/v1/room/{id}
```
Response Status Code : 200
> {  
> "id": 100,   
> "capacity": 50,   
> "location": "fmipa"   
> }

Response Status Code : 404
> No room id matches request

#### POST

* #### Add new room data
    - Requires 'ADMIN' role
    - Requires Room object in the request
```
/api/v1/room/admin
```
Response Status Code : 201
> URL of the created data

Response Status Code : 400
>  Attribute value of JSON object missing

Response Status Code : 403
>   User does not have 'ADMIN' role

#### PUT

* #### Update a room
    - Requires 'ADMIN' role
    - Requires Room object in the request
```
/api/v1/room/admin/{id}
```
Response Status Code : 204
> No Content

Response Status Code : 403
> User does not have 'ADMIN' role

Response Status Code : 404
> No room id matches request

#### DELETE

* #### Delete a room
    - Requires 'ADMIN' role
```
/api/v1/room/admin/{id}
```
Response Status Code : 204
> No Content

Response Status Code : 403
> User does not have 'ADMIN' role

Response Status Code : 404
> No room id matches Request