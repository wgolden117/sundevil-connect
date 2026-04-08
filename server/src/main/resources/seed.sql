-- Clear existing data
DELETE FROM membershipRequests;
DELETE FROM clubMemberships;
DELETE FROM events;
DELETE FROM clubs;
DELETE FROM students;
DELETE FROM users;
DELETE FROM eventRegistrations;

-- Users
INSERT OR IGNORE INTO users (email, password, role, firstName, lastName) VALUES
    ('student@sundevil.com', 'password123', 'STUDENT', 'Frank', 'Castle'),
    ('leader@sundevil.com', 'password123', 'CLUB_LEADER', 'Peter', 'Parker'),
    ('admin@sundevil.com', 'password123', 'ADMIN', 'Matt', 'Murdock');

-- Students
INSERT OR IGNORE INTO students (userId, major, graduationYear) VALUES
    ((SELECT id FROM users WHERE email = 'student@sundevil.com'), 'Computer Science', 2027),
    ((SELECT id FROM users WHERE email = 'leader@sundevil.com'), 'Computer Science', 2026);

-- Clubs
INSERT OR IGNORE INTO clubs (name, description, category, foundedDate, status) VALUES
    ('ASU Coding Club', 'A club for students passionate about software development', 'Technology', '2020-01-15', 'ACTIVE'),
    ('Sun Devil Music Society', 'Bringing together musicians and music lovers on campus', 'Music', '2018-03-20', 'ACTIVE'),
    ('ASU Robotics', 'Building and competing with robots', 'Technology', '2019-09-01', 'ACTIVE');

-- Club Memberships (Peter Parker leads Coding Club, Frank Castle is a member)
INSERT OR IGNORE INTO clubMemberships (studentId, clubId, role, joinDate, status) VALUES
    ((SELECT id FROM users WHERE email = 'leader@sundevil.com'), (SELECT clubId FROM clubs WHERE name = 'ASU Coding Club'), 'LEADER', '2020-01-15', 'ACTIVE'),
    ((SELECT id FROM users WHERE email = 'student@sundevil.com'), (SELECT clubId FROM clubs WHERE name = 'ASU Coding Club'), 'MEMBER', '2026-01-10', 'ACTIVE');

-- Membership Requests (Frank Castle requesting to join Robotics)
INSERT OR IGNORE INTO membershipRequests (studentId, clubId, status, requestDate) VALUES
    ((SELECT id FROM users WHERE email = 'student@sundevil.com'), (SELECT clubId FROM clubs WHERE name = 'ASU Robotics'), 'PENDING', '2026-04-01');

-- Events
INSERT OR IGNORE INTO events (title, description, category, location, event_date, capacity, is_paid) VALUES
    ('Tech Talk', 'Learn about new tech trends', 'Technology', 'Room 101', '2026-04-10', 100, 0),
    ('Music Night', 'Live performances', 'Music', 'Auditorium', '2026-04-12', 200, 1);