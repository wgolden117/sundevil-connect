CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    email TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    role TEXT NOT NULL,
    firstName TEXT,
    lastName TEXT
);

CREATE TABLE IF NOT EXISTS students (
    userId INTEGER PRIMARY KEY,
    major TEXT,
    graduationYear INTEGER,
    FOREIGN KEY (userId) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS events (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    description TEXT,
    category TEXT,
    location TEXT,
    event_date TEXT,
    capacity INTEGER,
    is_paid INTEGER
);

CREATE TABLE IF NOT EXISTS announcements (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    body TEXT,
    postedDate TEXT,
    postedToClub INTEGER NOT NULL,
    createdBy INTEGER NOT NULL,
    status TEXT NOT NULL,
    FOREIGN KEY (createdBy) REFERENCES  users(id),
    FOREIGN KEY (postedToClub) REFERENCES clubs(clubId)
);

CREATE TABLE IF NOT EXISTS eventRegistrations (
    registrationId INTEGER PRIMARY KEY AUTOINCREMENT,
    studentId INTEGER NOT NULL,
    eventId INTEGER NOT NULL,
    registrationDate TEXT,

    FOREIGN KEY (studentId) REFERENCES users(id),
    FOREIGN KEY (eventId) REFERENCES events(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_unique_event_registration
    ON eventRegistrations (studentId, eventId);

CREATE TABLE IF NOT EXISTS clubs (
    clubId INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT,
    category TEXT,
    foundedDate TEXT,
    status TEXT
);

CREATE TABLE IF NOT EXISTS clubMemberships (
    membershipId INTEGER PRIMARY KEY AUTOINCREMENT,
    studentId INTEGER NOT NULL,
    clubId INTEGER NOT NULL,
    role TEXT,
    joinDate TEXT,
    status TEXT,
    FOREIGN KEY (studentId) REFERENCES users(id),
    FOREIGN KEY (clubId) REFERENCES clubs(clubId)
);

CREATE TABLE IF NOT EXISTS membershipRequests (
    requestId INTEGER PRIMARY KEY AUTOINCREMENT ,
    studentId INTEGER NOT NULL,
    clubId INTEGER NOT NULL,
    status TEXT,
    requestDate TEXT,
    reviewedBy INTEGER,
    reviewDate TEXT,
    FOREIGN KEY (studentId) REFERENCES users(id),
    FOREIGN KEY (clubId) REFERENCES clubs(clubId),
    FOREIGN KEY (reviewedBy) REFERENCES users(id)
);