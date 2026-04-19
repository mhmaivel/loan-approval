# Coop Panga Tarkvaraarendaja Praktika Kodutöö, laenu kinnitamise protsess
**Marii-Heleen Maivel**
## Lühikirjeldus
Rakendus võimaldab kliendil esitada taotluse laenu saamiseks. Süsteem hindab, kas väljad on nõuetekohaselt täidetud ja kas klient pole liiga vana. Kui kõik on korras, saab genereerida maksegraafiku vastavalt taotlusele. Selle põhjal saab teha otsuse, kas taotlus vastu võtta või tagasi lükata.
## Tehnoloogiad
- Java 21
- Spring Boot 3.5.13
- PostgreSQL 16
- Maven
- Flyway
- Docker
## Sõltuvused
- Spring Web
- Spring Data JPA
- Lombok
- Validation
- SpringDoc OpenAPI (Swagger)
## Käivitamine
```bash
docker-compose up --build
```
Rakendus käivitub pordil 8080
## Swagger UI
http://localhost:8080/swagger-ui/index.html
## Kasutajaliides
http://localhost:8080/index.html
Kasutajaliides on loodud täiesti AI-ga. See pole lisatud lisapunkti saamiseks, vaid et kodutöö hindamisel oleks lisa viis mugavaks testimiseks.
## Andmebaas
Andmebaasi struktuur koosneb neljast põhitabelist:

`customer` - kliendi andmed

`loan_application` - laenutaotlus, igal kliendil saab olla ainult üks aktiivne taotlus

`payment_schedule` - maksegraafik, igal taotlusel saab olla ainult üks graafik.

`payment_schedule_item` - maksegraafiku kuumaksed

Lisaks on table `system_parameter`, kus saab muuta maksimaalset kliendi
vanust ja 6 kuu Euribori väärtust
Tabelitel on andmebaasi tasemel kontrollid, näiteks unikaalne indeks, mis lubab kilendi kohta ühte aktiivset taotlust korraga.

## API ülevaade
POST `/loans` - Laenutaotluse esitamine

POST `/schedules/loan/{id}` - Genereeritakse maksegraafik

GET `/schedules/loan/{id}` - Saab vaadata maksegraafikut

POST `/schedules/{id}/approve` - Maksegraafiku kinnitamine

POST `/schedules/{id}/reject` - Maksegraafiku tagasilükkamine
