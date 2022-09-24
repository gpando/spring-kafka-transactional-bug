## What this project needs to run:
1. Kafka on localhost:9092
2. postgresql on localhost:5432   

(docker compose in /docker)


## How to reproduce the issue:
1. Start the project   
2. ```curl http://localhost:8080/test-ok```   
(new record in DB y kafka -> ok)   

3. STOP kafka  
4. ```curl http://localhost:8080/test-ok```   
(new record in DB!! -> not ok)

- Expected result after step 3: is no records to be found in DB (and Kafka). Everything should be rollback.
- Actual result: there are new record in DB.