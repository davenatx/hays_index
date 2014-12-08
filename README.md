Hays Index
===============================

This project contains code to parse the Hays County Grantor/Grantee index and store it in a H2 Database.

Working with the H2 Database
----------------------------
To access the H2 Database, start the H2 Monitor (H2 Console) and use the following settings:
* Setting Name: Generic H2 (Server)
* Driver Class: org.h2.Driver
* JDBC URL: jdbc:h2:C:/Hays_Index_DB/hays_index;AUTO_SERVER=TRUE
* User Name: sa
* Password:

Example Query
-------------
SELECT DISTINCT RECTYP, DOCTYP, VOLUME, PAGE, FILEDATE, FNAME
FROM INDEX_RECORDS
WHERE EXTRACT(YEAR FROM FILEDATE) = 1920 AND RECTYP = 'OPR'
ORDER BY VOLUME, PAGE, FILEDATE

Example Query that properly orders PAGE
---------------------------------------
SELECT DISTINCT RECTYP, DOCTYP, VOLUME, CAST(PAGE as INT), FILEDATE, FNAME 
FROM INDEX_RECORDS 
WHERE EXTRACT(YEAR FROM FILEDATE) = 1920 AND RECTYP = 'OPR' 
ORDER BY VOLUME, CAST(PAGE as INT), FNAME

Example Query that generates a CSV file
---------------------------------------
call CSVWRITE ( 'C:/Hays_Index_DB/1920.csv', 'SELECT DISTINCT RECTYP, DOCTYP, VOLUME, PAGE, FILEDATE, FNAME
FROM INDEX_RECORDS
WHERE EXTRACT(YEAR FROM FILEDATE) = 1920 AND RECTYP = ''OPR''
ORDER BY VOLUME, PAGE, FILEDATE' );  

Example Query that properly orders PAGE and generates a CSV file
----------------------------------------------------------------
call CSVWRITE ( 'C:/Hays_Index_DB/1888.csv', 'SELECT DISTINCT RECTYP, DOCTYP, VOLUME, CAST(PAGE as INT), FILEDATE, FNAME FROM INDEX_RECORDS WHERE EXTRACT(YEAR FROM FILEDATE) = 1888 AND RECTYP = ''OPR'' ORDER BY VOLUME, CAST(PAGE as INT), FNAME' );