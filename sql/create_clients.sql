CREATE TABLE client (
  client_id SERIAL PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  surname VARCHAR(50) NOT NULL,
  birth_year INT NOT NULL,
  money FLOAT(53) NOT NULL DEFAULT 0
);

INSERT INTO client (name, surname, birth_year, money)
VALUES
 ('Jan', 'Nowak', 1970, 2000),
 ('Krzysztof', 'Kowalski', 1978, 10000),
 ('Piotr', 'Wilk', 2000, 100),
 ('Joanna', 'Zielińska', 2010, 200),
 ('Bożena', 'Wójcik', 1995, 2400),
 ('Jan', 'Woźniak', 1999, 0),
 ('Anna', 'Kowalczyk', 2013, 150),
 ('Marek', 'Wiśniewski', 1956, 200000),
 ('Sandra', 'Kowalska', 1977, 15500),
 ('Witold', 'Lewandowski', 2008, 300),
 ('Hieronim', 'Kawa', 2001, 2000),
 ('Dawid', 'Bil', 1989, 3000),
 ('Ewelina', 'Wilk', 1990, 200),
 ('Mikołaj', 'Mazur', 1998, 0),
 ('Katarzyna', 'Kwiatkowska', 2009, 10),
 ('Anna', 'Kozłowska', 1973, 20000),
 ('Przemysław', 'Krawczyk', 2004, 500),
 ('Bogdan', 'Wojciechowski', 1949, 130000),
 ('Róża', 'Jankowska', 1990, 12330),
 ('Piotr', 'Nowak', 1991, 10000),
 ('Natalia', 'Grabowska', 1973, 20000);