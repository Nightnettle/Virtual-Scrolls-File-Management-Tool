CREATE TABLE IF NOT EXISTS users (
	userid VARCHAR(15) PRIMARY KEY,
	password CHAR(64),
	utype VARCHAR(20),
	phonenum VARCHAR(10),
	email VARCHAR(30),
	fullname VARCHAR(30)
);

CREATE TABLE IF NOT EXISTS scrolls (
        name VARCHAR(20),
        scrollid VARCHAR(20),
        scroll BLOB,
        downloads INTEGER,
        uploads INTEGER,
        userid VARCHAR(15) REFERENCES users,
        PRIMARY KEY(name, scrollid)
);

