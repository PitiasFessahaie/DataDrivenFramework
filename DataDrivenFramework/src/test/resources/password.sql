CREATE TABLE password(
    user_name VARCHAR2(30) NOT NULL,
    user_password VARCHAR2(30) NOT NULL,
    PRIMARY KEY(user_name)
);

INSERT INTO password VALUES(
'adminstrator','password'
);

INSERT INTO password VALUES(
'admin','demo123'
);
INSERT INTO password VALUES(
'admin12','adminuser'
);
INSERT INTO password VALUES(
'james@uk.org','bonder'
);
INSERT INTO password VALUES(
'frank@123','@bester'
);

