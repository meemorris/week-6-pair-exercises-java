--BEGIN TRANSACTION;
--DROP DATABASE IF EXISTS historyreport;
CREATE DATABASE historyreport;

CREATE TABLE pet
(  pet_id SERIAL,
   name varchar(64) NOT NULL,
   age int NOT NULL,
   type varchar(64) NOT NULL,
   CONSTRAINT pk_pet_id PRIMARY KEY (pet_id)  );
   
CREATE TABLE owner
(   owner_id SERIAL,
    first_name varchar(64) NOT NULL,
    last_name varchar(64) NOT NULL,
    CONSTRAINT pk_owner_id PRIMARY KEY (owner_id)  );
    
CREATE TABLE ownership
(   owner_id int NOT NULL,
    pet_id int NOT NULL,
    CONSTRAINT pk_ownership PRIMARY KEY (owner_id, pet_id)  );
    
CREATE TABLE procedures
(   procedure_id SERIAL,
    name varchar(64) NOT NULL,
    CONSTRAINT pk_procedure_id PRIMARY KEY (procedure_id)  );
    
CREATE TABLE visits
(   visit_id SERIAL,
    visit_date date NOT NULL,
    pet_id int NOT NULL,
    procedure_id int NOT NULL,
    CONSTRAINT pk_visit_id PRIMARY KEY (visit_id)  );
    
ALTER TABLE visits
ADD CONSTRAINT fk_pet_id FOREIGN KEY(pet_id) REFERENCES pet(pet_id);
    
--COMMIT;