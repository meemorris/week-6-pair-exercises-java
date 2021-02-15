CREATE DATABASE invoice;

CREATE TABLE owner
(   owner_id SERIAL,
    salutation varchar(6),
    first_name varchar(64) NOT NULL,
    last_name varchar(64) NOT NULL,
    street_address varchar(100) NOT NULL,
    city varchar(35) NOT NULL,
    state varchar(25) NOT NULL,
    zipcode int NOT NULL,
    
    CONSTRAINT pk_owner_id PRIMARY KEY(owner_id)   );
    
CREATE TABLE pet
(   pet_id SERIAL,
    name varchar(64) NOT NULL,
    
    CONSTRAINT pk_pet_id PRIMARY KEY(pet_id)    );
    

CREATE TABLE procedure
(   procedure_id SERIAL,
    name varchar(64) NOT NULL,
    
    CONSTRAINT pk_procedure_id PRIMARY KEY(procedure_id)    );
    

CREATE TABLE payment
(   payment_id SERIAL,
    procedure_id int,
    procedure_price money,
    subtotal money,
    tax money,
    amount_owing money,
    
    CONSTRAINT pk_payment_id PRIMARY KEY(payment_id),
    CONSTRAINT fk_procedure_id FOREIGN KEY(procedure_id) REFERENCES procedure(procedure_id)    );
    
CREATE TABLE invoice
(   invoice_id SERIAL,
    payment_id int,
    pet_id int,
    owner_id int,
    invoice_date date,
    
    CONSTRAINT pk_invoice_id PRIMARY KEY(invoice_id),
    CONSTRAINT fk_payment_id FOREIGN KEY(payment_id) REFERENCES payment(payment_id),
    CONSTRAINT fk_pet_id FOREIGN KEY(pet_id) REFERENCES pet(pet_id),
    CONSTRAINT fk_owner_id FOREIGN KEY(owner_id) REFERENCES owner(owner_id)   );
    
CREATE TABLE hospital
(   location_id SERIAL,
    invoice_id int,
    name varchar (64),
    
    CONSTRAINT pk_location_id PRIMARY KEY(location_id),
    CONSTRAINT fk_invoice_id FOREIGN KEY(invoice_id) REFERENCES invoice(invoice_id)  );
    