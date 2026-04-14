-- Clear existing data
DELETE FROM announcements;
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
    ('student2@sundevil.com', 'password123', 'STUDENT', 'Mary', 'Jane'),
    ('student3@sundevil.com', 'password123', 'STUDENT', 'Harry', 'Osborn'),
    ('leader@sundevil.com', 'password123', 'CLUB_LEADER', 'Peter', 'Parker'),
    ('admin@sundevil.com', 'password123', 'ADMIN', 'Matt', 'Murdock');

-- Students
INSERT OR IGNORE INTO students (userId, major, graduationYear) VALUES
    ((SELECT id FROM users WHERE email = 'student@sundevil.com'), 'Computer Science', 2027),
    ((SELECT id FROM users WHERE email = 'leader@sundevil.com'), 'Computer Science', 2026),
    ((SELECT id FROM users WHERE email = 'student2@sundevil.com'), 'Music', 2027),
    ((SELECT id FROM users WHERE email = 'student3@sundevil.com'), 'Business', 2026);

-- Clubs
INSERT OR IGNORE INTO clubs (name, description, category, foundedDate, status) VALUES
    ('ASU Coding Club', 'A club for students passionate about software development', 'Technology', '2020-01-15', 'ACTIVE'),
    ('Sun Devil Music Society', 'Bringing together musicians and music lovers on campus', 'Music', '2018-03-20', 'ACTIVE'),
    ('ASU Robotics', 'Building and competing with robots', 'Technology', '2019-09-01', 'ACTIVE'),
    ('ASU Career Development', 'Helping students prepare for their professional careers', 'Career', '2017-08-01', 'ACTIVE'),
    ('Sun Devil Gaming', 'Competitive and casual gaming community', 'Recreation', '2021-02-10', 'ACTIVE'),
    ('ASU Arts Collective', 'Celebrating student creativity and artistic expression', 'Art', '2016-05-15', 'ACTIVE'),
    ('Sun Devil Fitness', 'Promoting health and wellness on campus', 'Health', '2019-03-01', 'ACTIVE');

-- Club Memberships (Peter Parker leads Coding Club, Frank Castle is a member)
INSERT OR IGNORE INTO clubMemberships (studentId, clubId, role, joinDate, status) VALUES
    ((SELECT id FROM users WHERE email = 'leader@sundevil.com'), (SELECT clubId FROM clubs WHERE name = 'ASU Coding Club'), 'LEADER', '2020-01-15', 'ACTIVE'),
    ((SELECT id FROM users WHERE email = 'student@sundevil.com'), (SELECT clubId FROM clubs WHERE name = 'ASU Coding Club'), 'MEMBER', '2026-01-10', 'ACTIVE'),
    ((SELECT id FROM users WHERE email = 'student2@sundevil.com'), (SELECT clubId FROM clubs WHERE name = 'ASU Coding Club'), 'MEMBER', '2026-02-01', 'ACTIVE');

-- Membership Requests (Frank Castle requesting to join Robotics)
INSERT OR IGNORE INTO membershipRequests (studentId, clubId, status, requestDate) VALUES
    ((SELECT id FROM users WHERE email = 'student@sundevil.com'), (SELECT clubId FROM clubs WHERE name = 'ASU Robotics'), 'PENDING', '2026-04-01'),
    ((SELECT id FROM users WHERE email = 'student2@sundevil.com'), (SELECT clubId FROM clubs WHERE name = 'ASU Robotics'), 'PENDING', '2026-04-02'),
    ((SELECT id FROM users WHERE email = 'student3@sundevil.com'), (SELECT clubId FROM clubs WHERE name = 'ASU Coding Club'), 'PENDING', '2026-04-03'),
    ((SELECT id FROM users WHERE email = 'student3@sundevil.com'), (SELECT clubId FROM clubs WHERE name = 'ASU Robotics'), 'PENDING', '2026-04-05');

-- Events
INSERT OR IGNORE INTO events (title, description, category, location, event_date, capacity, is_paid, hostedByClub) VALUES
    ('Tech Talk: AI Trends', 'Learn about AI in 2026', 'Technology', 'Room 101', '2026-04-10', 100, 0, (SELECT clubId FROM clubs WHERE name = 'ASU Coding Club')),
    ('Music Night', 'Live performances', 'Music', 'Auditorium', '2026-04-12', 200, 1, (SELECT clubId FROM clubs WHERE name = 'Sun Devil Music Society')),
    ('Hackathon 2026', '24-hour coding event', 'Technology', 'Engineering Building', '2026-04-20', 150, 0, (SELECT clubId FROM clubs WHERE name = 'ASU Coding Club')),
    ('Robotics Workshop', 'Build your first robot', 'Technology', 'Lab 3', '2026-04-18', 40, 0, (SELECT clubId FROM clubs WHERE name = 'ASU Robotics')),
    ('Career Fair Prep', 'Resume + interview tips', 'Career', 'Student Center', '2026-04-22', 80, 0, (SELECT clubId FROM clubs WHERE name = 'ASU Career Development')),
    ('Startup Pitch Night', 'Pitch your ideas', 'Business', 'Room 205', '2026-04-25', 60, 1, (SELECT clubId FROM clubs WHERE name = 'ASU Career Development')),
    ('Gaming Tournament', 'Compete in esports', 'Recreation', 'Gaming Lounge', '2026-04-28', 120, 0, (SELECT clubId FROM clubs WHERE name = 'Sun Devil Gaming')),
    ('Art Showcase', 'Student artwork display', 'Art', 'Gallery Hall', '2026-05-01', 50, 0, (SELECT clubId FROM clubs WHERE name = 'ASU Arts Collective')),
    ('Fitness Bootcamp', 'Outdoor workout session', 'Health', 'Campus Field', '2026-05-03', 70, 0, (SELECT clubId FROM clubs WHERE name = 'Sun Devil Fitness')),
    ('Movie Night', 'Outdoor movie screening', 'Entertainment', 'Quad Lawn', '2026-05-05', 200, 0, (SELECT clubId FROM clubs WHERE name = 'Sun Devil Fitness'));

-- Announcements (Peter Parker posts to ASU Coding Club)
INSERT OR IGNORE INTO announcements (title, body, postedDate, postedToClub, createdBy, status) VALUES
    ('Welcome to the Coding Club!', 'Welcome to the ASU Coding Club! We are excited to have you here. Stay tuned for upcoming events and workshops.', '2026-01-15', (SELECT clubId FROM clubs WHERE name = 'ASU Coding Club'), (SELECT id FROM users WHERE email = 'leader@sundevil.com'), 'PUBLISHED'),
    ('Spring Hackathon Announced', 'We will be hosting a 24-hour hackathon this spring. Registration opens next week. Prizes for top three teams!', '2026-02-10', (SELECT clubId FROM clubs WHERE name = 'ASU Coding Club'), (SELECT id FROM users WHERE email = 'leader@sundevil.com'), 'PUBLISHED'),
    ('New Meeting Schedule', 'We are updating our weekly meeting schedule for the rest of the semester. More details coming soon.', NULL, (SELECT clubId FROM clubs WHERE name = 'ASU Coding Club'), (SELECT id FROM users WHERE email = 'leader@sundevil.com'), 'DRAFT'),
    ('Battle of the Bands', 'Sun Devil Music Society is proud to announce our annual Battle of the Bands event this April!', '2026-03-01', (SELECT clubId FROM clubs WHERE name = 'Sun Devil Music Society'), (SELECT id FROM users WHERE email = 'leader@sundevil.com'), 'PUBLISHED');