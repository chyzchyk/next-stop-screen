
# Next stop screen for public transport

Repository of the application for displaying information about route to passengers on Android.

This application is created to inform passengers about the stop and the next stops, as well as display the route number and direction. Data is received from the API.

Language of the application - Ukrainian

Translation:\
Наступна зупинка – Next stop\
Зупинка – Stop\
Визанчення маршруту – Defining the route

## Screenshots

![App Screenshot](https://github.com/chyzchyk/next-stop-screen/assets/125468919/7d3aa366-2604-43b0-adcc-911f635747ef)

![App Screenshot](https://github.com/chyzchyk/next-stop-screen/assets/125468919/6a497c5d-a805-4b3d-bc06-1cccaa9bb120)

## REST API

REST API used to get data about route number assigned for vehicle and it`s current position

#### Get info from tracker

```http
  GET /vehs/5

```
#### Response

```
HTTP/1.1 200 OK
Server: nginx/1.2.1
Date: Thu, 28 Sep 2023 14:06:07 GMT
Content-Type: application/json
Content-Length: 12901
Connection: close
Strict-Transport-Security: max-age=604800

   "90": {
       "54099": {
           "name": "Bus 1",
           "time": false,
           "segId": false,
           "pos": false,
           "azimuth": false,
           "loa": false
       },
       "54105": {
           "name": "Bus 2",
           "time": 1675845924,
           "segId": false,
           "pos": [
               34.54640166666667,
               49.59315166666666
           ],
           "azimuth": false,
           "loa": false
       },
       "54152": {
           "name": "Bus 3",
           "time": 1675845926,
           "segId": 1278,
           "pos": [
               34.538925,
               49.595396666666666
           ],
           "azimuth": 115.492312167581,
           "loa": false
```

| Data      | Description                |
| :-------- | :------------------------- |
| `90`      | Route ID                   |
| `54099`   | Tracker ID                 |
| `name`    | Vehicle name               |
| `time`    | Unixtime time on tracker   |
| `segId`   | ID of route segment        |
| `pos`     | Position of vehicle        |
| `azimuth` | Azimuth                    |
| `loa`     | Vehicle out of route or not|


## Info

More information can be found on the Wiki tab