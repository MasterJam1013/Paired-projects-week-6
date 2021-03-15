--DROP DATABASE IF EXISTS pethospitalinvoice;
--CREATE DATABASE pethospitalinvoice;

BEGIN TRANSACTION;


CREATE TABLE hospital
(
   hospital_id serial,
   name varchar(100) NOT NULL,
   taxrate real NOT NULL,
   
   CONSTRAINT pk_hospital PRIMARY KEY(hospital_id)
);

CREATE TABLE pet
(
   pet_id serial,
   name varchar(64) NOT NULL,
   owner_id int NOT NULL,
   
   CONSTRAINT pk_pet PRIMARY KEY(pet_id)
);

CREATE TABLE owner
(
   owner_id serial,
   first_name varchar(64) NOT NULL,
   last_name varchar(64) NOT NULL,
   address1 varchar(100) NOT NULL,
   address2 varchar(100) NOT NULL,
   prefix varchar(11) NULL,
   
   CONSTRAINT pk_owner PRIMARY KEY(owner_id)
);

ALTER TABLE pet
ADD CONSTRAINT fk_pet_ownerid_ref_owner_ownerid FOREIGN KEY(owner_id)
REFERENCES owner(owner_id);

CREATE TABLE procedure
(
  procedure_id serial,
  name varchar(100) NOT NULL,
  cost money NOT NULL,
  is_paid boolean NOT NULL,
  
  CONSTRAINT pk_procedureid PRIMARY KEY(procedure_id)
);

CREATE TABLE pet_procedure
(
   pet_id int NOT NULL,
   visit_date date NOT NULL,
   procedure_id int NOT NULL,
   hospital_id int NOT NULL,
   
   CONSTRAINT pk_pet_procedure_composite PRIMARY KEY(pet_id, visit_date, procedure_id),
   CONSTRAINT fk_pet_procedure_petid_ref_pet_petid FOREIGN KEY(pet_id) REFERENCES pet(pet_id),
   CONSTRAINT fk_pet_procedure_procedureid_ref_procedure_procedureid FOREIGN KEY(procedure_id) REFERENCES procedure(procedure_id),
   CONSTRAINT fk_pet_procedure_hospitalid_ref_hospital_hospitalid FOREIGN KEY(hospital_id) REFERENCES hospital(hospital_id)
);

CREATE TABLE invoice
(
   invoice_id serial,
   invoice_date date NOT NULL,
   owner_id int NOT NULL,
   
   CONSTRAINT pk_invoice_id PRIMARY KEY(invoice_id),
   CONSTRAINT fk_invoice_ownerid_ref_owner_owner_id FOREIGN KEY(owner_id) REFERENCES owner(owner_id)
);


COMMIT;