--we put pet birthday in the pet table instead of an int for age since it would change yearly, then pet age on the report would be a calculated value

--DROP DATABASE IF EXISTS healthhistoryreport;
--CREATE DATABASE healthhistoryreport;


BEGIN TRANSACTION;

CREATE TABLE pet
(
   pet_id serial,
   name varchar(64) NOT NULL,
   type varchar(64) NOT NULL,
   birthday date NOT NULL,
   owner_id int NOT NULL,
   
   CONSTRAINT pk_pet PRIMARY KEY(pet_id)
);

CREATE TABLE owner
(
   owner_id serial,
   first_name varchar(64) NOT NULL,
   last_name varchar(64) NOT NULL,
   address varchar(100) NOT NULL,
   phone_number varchar(11) NULL,
   
   CONSTRAINT pk_owner PRIMARY KEY(owner_id)
);

ALTER TABLE pet
ADD CONSTRAINT fk_pet_ownerid_ref_owner_ownerid FOREIGN KEY(owner_id)
REFERENCES owner(owner_id);

CREATE TABLE procedure
(
  procedure_id serial,
  name varchar(100) NOT NULL,
  
  CONSTRAINT pk_procedureid PRIMARY KEY(procedure_id)
);

CREATE TABLE pet_procedure
(
   pet_id int NOT NULL,
   visit_date date NOT NULL,
   procedure_id int NOT NULL,
   
   CONSTRAINT pk_pet_procedure_composite PRIMARY KEY(pet_id, visit_date, procedure_id),
   CONSTRAINT fk_pet_procedure_petid_ref_pet_petid FOREIGN KEY(pet_id) REFERENCES pet(pet_id),
   CONSTRAINT fk_pet_procedure_procedureid_ref_procedure_procedureid FOREIGN KEY(procedure_id) REFERENCES procedure(procedure_id)
);



COMMIT;