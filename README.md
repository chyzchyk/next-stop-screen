
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

## Installation

Download the latest release of the app and install it on your Android device. In the root of your device, there should be created in the root of your device folder "pasinfosc".

Folder "pasinfosc" files tree:

```
pasinfosc
│   config.txt
│   r.txt    
│
└───ad
│   │   ads.mp4
│   │   ads1.mp4
│   │   ...
│   
└───Sound
    │   next_stop_sound.wav
    │   stop_sound.wav
    │   ...
```
Folders "ad" and "Sound" they are not required, but it is recommended to create folders without files inside.

config.txt — coniguration file\
r.txt — a file with information about routes and their stops\
Folder "ad" — folder with advertising files in the .mp4 format (not supported in version 1.0.0)\
Folder "Sound" — folder with audio files of stop announcements
## Config file

The structure of the config.txt file:
```
1-15-(Display time of information about stops between advertisements, in seconds)
2-https://mpt.elsyton.com/-(API URL)
3-40-(Radius of stops, in metres)
4-56022-(Anchor tracker ID, for example 54099)
```
## r.txt file


Example of r.txt file and its description:

Suppose there are two routes in the file. One route has the number 53 (regular route) and the other route has the number 19 (circular routes).
Then the file should contain the following information:
```
|90-53
#(49.5697, 34.4924) вул. Героїв Сталінграда^66n\66
(49.5663, 34.4933) Автовокзал^17n\17
(49.5660, 34.4996) Школа №28^44n\44
(49.5658, 34.5057) вул. 23 Вересня^36n\36
(49.5655, 34.5136) Торговельний центр^43n\43
(49.5653, 34.5191) Мотель^34n\34
(49.5656, 34.5239) 4-та Поліклініка^33n\33
(49.5664, 34.5303) вул. Гетьмана Сагайдачного^6n\6
(49.5692, 34.5328) вул. Чайковського^7n\7
(49.5730, 34.5352) вул. Панаса Мирного^8n\8
(49.5760, 34.5379) Парк Котляревського^9n\9
(49.5786, 34.5418) вул. Патріарха Мстислава^10n\10
(49.5811, 34.5459) Медакадемія   UMSA^11n\11
(49.5838, 34.5489) вул. Шевченка^12n\12
(49.5882, 34.5519) Корпусний Парк^53n\53
(49.5918, 34.5463) майдан Незалежності^42n\42
(49.5942, 34.5417) Університет^41n\41
(49.5965, 34.5368) Школа №5^67n\67
(49.5987, 34.5323) площа Зигіна^27n\27
(49.6029, 34.5300) Турбомеханічний завод^54n\54
(49.6085, 34.5297) вул. Дослідна^55n\55
(49.6138, 34.5297) Інститут зв'язку^56n\56
(49.6173, 34.5295) База Холодильник^57n\57
(49.6221, 34.5340) Автоагрегатний завод^58n\58
(49.6251, 34.5368) вул. 9-го Січня^59n\59
(49.6294, 34.5407) Редути^60n\60
(49.6335, 34.5445) Пам'ятник^61n\61
(49.6366, 34.5471) Обласна Психіатрична лікарня^62n\62
(49.6441, 34.5417) вул. Малобудищанська^63n\63
#(49.6441, 34.5417) вул. Малобудищанська^63n\63
(49.6361, 34.5464) Обласна психіатрична лікарня^62n\62
(49.6321, 34.5429) Пам'ятник^61n\61
(49.6297, 34.5407) Редути^60n\60
(49.6255, 34.5367) вул. 9-го Січня^59n\59
(49.6219, 34.5337) Автоагрегатний завод^58n\58
(49.6167, 34.5290) База Холодильник^57n\57
(49.6137, 34.5296) Інститут зв'язку^56n\56
(49.6076, 34.5297) вул. Дослідна^55n\55
(49.6017, 34.5299) вул. Кондратенка^65n\65
(49.5986, 34.5314) площа Зигіна^27n\27
(49.5963, 34.5363) ОЦЕВУМ^28n\28
(49.5940, 34.5410) вул. Сінна^29n\29
(49.5881, 34.5506) Будинок зв'язку^30n\30
(49.5824, 34.5472) вул. Шевченка^12n\12
(49.5807, 34.5451) вул. Героїв-Чорнобильців^14n\14
(49.5782, 34.5410) Школа №27^15n\15
(49.5732, 34.5352) вул. Героїв АТО^31n\31
(49.5703, 34.5334) вул. Лялі Убийвовк^32n\32
(49.5675, 34.5311) вул. Гетьмана Сагайдачного^6n\6
(49.5655, 34.5220) 4-та Поліклініка^33n\33
(49.5654, 34.5176) Мотель^34n\34
(49.5656, 34.5120) вул. Алмазна^35n\35
(49.5659, 34.5037) вул. 23 Вересня^36n\36
(49.5662, 34.4991) Поліклініка^37n\37
(49.5670, 34.4920) Автовокзал^17n\17
(49.5697, 34.4924) вул. Героїв Сталінграда^66n\66
|6-19
Малий кільцевий
(49.5655, 34.5220) 4-та Поліклініка^33n\33
(49.5654, 34.5176) Мотель^34n\34
(49.5656, 34.5120) вул. Алмазна^35n\35
(49.5659, 34.5037) вул. 23 Вересня^36n\36
(49.5662, 34.4991) Поліклініка^37n\37
(49.5670, 34.4920) Автовокзал^17n\17
(49.5697, 34.4924) вул. Героїв Сталінграда^18n\18
(49.5730, 34.4924) Сади - 1^19n\19
(49.5818, 34.4918) Бібліотека^20n\20
(49.5859, 34.4922) пров. Латишева^21n\21
(49.5897, 34.4921) вул. Великотирнівська^22n\22
(49.5922, 34.5013) Митниця^97n\97
(49.5921, 34.5072) Нафтобаза^98n\98
(49.5921, 34.5131) м/н Юрівка^99n\99
(49.5939, 34.5234) Школа №25^100n\100
(49.5986, 34.5314) площа Зигіна^27n\27
(49.5963, 34.5363) ОЦЕВУМ^28n\28
(49.5940, 34.5410) вул. Сінна^29n\29
(49.5881, 34.5506) Будинок зв'язку^30n\30
(49.5863, 34.5513) вул. 1100-річчя Полтави^52n\52
(49.5824, 34.5472) вул. Шевченка^12n\12
(49.5807, 34.5451) вул. Героїв-Чорнобильців^14n\14
(49.5782, 34.5410) Школа №27^15n\15
(49.5732, 34.5352) вул. Героїв АТО^31n\31
(49.5703, 34.5334) вул. Лялі Убийвовк^32n\32
(49.5675, 34.5311) вул. Гетьмана Сагайдачного^6n\6
```

| Data      | Description                |
| :--------     | :------------------------- |
| `90-53`          | 'route ID'-'route number'                   |
| `#`       | The symbol indicating that this is the final stop                 |
| `(49.5697, 34.4924)`        | Stop coordinates               |
| `вул. Героїв Сталінграда`        | Name of the stop   |
| `^66n\66`       | ^Sound file "Next stop..."\Sound file "Stop..." (Without specifying a file extension)        |
| `Малий кільцевий`         | A static inscription in the line of the final stop, which is used for circular routes, because they do not have a final stop        |

If it is a regular route, the name of the final stop should be written twice. First: with the sign "#" to indicate that this particular stop is the final stop. Second: before the entry with the "#" sign, or on the last line of the route. Every regular route must start with a final stop with a "#" sign.

If it's a circular route, it doesn't specify the final stops
## Where the app receives data from

An example of data sent from the server:

REST API used to get data about route number assigned for vehicle and it`s current position

#### Get info from tracker

```https
  GET mpt.elsyton.com/vehs/5

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

| Data/Key      | Description                |
| :--------     | :------------------------- |
| `90`          | Route ID                   |
| `54099`       | Tracker ID                 |
| `name`        | Vehicle name               |
| `time`        | Unixtime time on tracker   |
| `segId`       | ID of route segment        |
| `pos`         | Position of vehicle        |
| `azimuth`     | Azimuth                    |
| `loa`         | Vehicle out of route or not|


## Developer

- [Borys Sanin](https://freelancehunt.com/ua/freelancer/SousaSoft.html)

## License

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

[![GPLv3 License](https://img.shields.io/badge/License-GPL%20v3-yellow.svg)](https://www.gnu.org/licenses/gpl-3.0.txt)
