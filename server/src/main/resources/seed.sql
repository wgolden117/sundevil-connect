-- Clear existing data (order matters for FK constraints)
DELETE FROM eventRegistrations;
DELETE FROM announcements;
DELETE FROM membershipRequests;
DELETE FROM clubMemberships;
DELETE FROM events;
DELETE FROM content;
DELETE FROM clubs;
DELETE FROM students;
DELETE FROM users;


------------------------------------------------
-- USERS
------------------------------------------------
INSERT OR IGNORE INTO users (email, password, role, firstName, lastName) VALUES
    ('student@sundevil.com',  'password123', 'STUDENT',     'Frank',  'Castle'),
    ('student2@sundevil.com', 'password123', 'STUDENT',     'Mary',   'Jane'),
    ('student3@sundevil.com', 'password123', 'STUDENT',     'Harry',  'Osborn'),
    ('student4@sundevil.com', 'password123', 'STUDENT',     'Gwen',   'Stacy'),
    ('student5@sundevil.com', 'password123', 'STUDENT',     'Miles',  'Morales'),
    ('leader@sundevil.com',   'password123', 'CLUB_LEADER', 'Peter',  'Parker'),
    ('leader2@sundevil.com',  'password123', 'CLUB_LEADER', 'Tony',   'Stark'),
    ('leader3@sundevil.com',  'password123', 'CLUB_LEADER', 'Natasha','Romanoff'),
    ('admin@sundevil.com',    'password123', 'ADMIN',       'Matt',   'Murdock');

------------------------------------------------
-- STUDENTS (one row per STUDENT or CLUB_LEADER user)
------------------------------------------------
INSERT OR IGNORE INTO students (userId, major, graduationYear) VALUES
    ((SELECT id FROM users WHERE email = 'student@sundevil.com'),  'Computer Science', 2027),
    ((SELECT id FROM users WHERE email = 'student2@sundevil.com'), 'Music',            2027),
    ((SELECT id FROM users WHERE email = 'student3@sundevil.com'), 'Business',         2026),
    ((SELECT id FROM users WHERE email = 'student4@sundevil.com'), 'Psychology',       2028),
    ((SELECT id FROM users WHERE email = 'student5@sundevil.com'), 'Computer Science', 2028),
    ((SELECT id FROM users WHERE email = 'leader@sundevil.com'),   'Computer Science', 2026),
    ((SELECT id FROM users WHERE email = 'leader2@sundevil.com'),  'Electrical Engineering', 2025),
    ((SELECT id FROM users WHERE email = 'leader3@sundevil.com'),  'Political Science', 2026);

------------------------------------------------
-- CLUBS  (ACTIVE clubs first, then PENDING)
------------------------------------------------
INSERT OR IGNORE INTO clubs (name, description, category, foundedDate, status, submittedBy) VALUES
    ('ASU Coding Club',         'A club for students passionate about software development',       'Technology',  '2020-01-15', 'ACTIVE',  NULL),
    ('Sun Devil Music Society', 'Bringing together musicians and music lovers on campus',          'Music',       '2018-03-20', 'ACTIVE',  NULL),
    ('ASU Robotics',            'Building and competing with robots',                              'Technology',  '2019-09-01', 'ACTIVE',  NULL),
    ('ASU Career Development',  'Helping students prepare for their professional careers',         'Career',      '2017-08-01', 'ACTIVE',  NULL),
    ('Sun Devil Gaming',        'Competitive and casual gaming community',                         'Recreation',  '2021-02-10', 'ACTIVE',  NULL),
    ('ASU Arts Collective',     'Celebrating student creativity and artistic expression',           'Art',         '2016-05-15', 'ACTIVE',  NULL),
    ('Sun Devil Fitness',       'Promoting health and wellness on campus',                         'Health',      '2019-03-01', 'ACTIVE',  NULL),
    -- PENDING clubs awaiting admin approval
    ('ASU Chess Club',          'Casual and competitive chess for all skill levels',               'Recreation',  '2026-03-10', 'PENDING', (SELECT id FROM users WHERE email = 'leader2@sundevil.com')),
    ('Sun Devil Debate Society','Competitive debate and public speaking practice',                 'Academic',    '2026-04-01', 'PENDING', (SELECT id FROM users WHERE email = 'leader3@sundevil.com'));

------------------------------------------------
-- CLUB MEMBERSHIPS  (leaders for every active club + some members)
------------------------------------------------
INSERT OR IGNORE INTO clubMemberships (studentId, clubId, role, joinDate, status) VALUES
    -- ASU Coding Club
    ((SELECT id FROM users WHERE email = 'leader@sundevil.com'),   (SELECT clubId FROM clubs WHERE name = 'ASU Coding Club'),         'LEADER', '2020-01-15', 'ACTIVE'),
    ((SELECT id FROM users WHERE email = 'student@sundevil.com'),  (SELECT clubId FROM clubs WHERE name = 'ASU Coding Club'),         'MEMBER', '2026-01-10', 'ACTIVE'),
    ((SELECT id FROM users WHERE email = 'student2@sundevil.com'), (SELECT clubId FROM clubs WHERE name = 'ASU Coding Club'),         'MEMBER', '2026-02-01', 'ACTIVE'),
    ((SELECT id FROM users WHERE email = 'student5@sundevil.com'), (SELECT clubId FROM clubs WHERE name = 'ASU Coding Club'),         'MEMBER', '2026-02-15', 'ACTIVE'),
    -- Sun Devil Music Society
    ((SELECT id FROM users WHERE email = 'leader2@sundevil.com'),  (SELECT clubId FROM clubs WHERE name = 'Sun Devil Music Society'), 'LEADER', '2018-03-20', 'ACTIVE'),
    ((SELECT id FROM users WHERE email = 'student2@sundevil.com'), (SELECT clubId FROM clubs WHERE name = 'Sun Devil Music Society'), 'MEMBER', '2026-01-20', 'ACTIVE'),
    -- ASU Robotics
    ((SELECT id FROM users WHERE email = 'leader@sundevil.com'),   (SELECT clubId FROM clubs WHERE name = 'ASU Robotics'),            'LEADER', '2019-09-01', 'ACTIVE'),
    -- ASU Career Development
    ((SELECT id FROM users WHERE email = 'leader3@sundevil.com'),  (SELECT clubId FROM clubs WHERE name = 'ASU Career Development'),  'LEADER', '2017-08-01', 'ACTIVE'),
    ((SELECT id FROM users WHERE email = 'student3@sundevil.com'), (SELECT clubId FROM clubs WHERE name = 'ASU Career Development'),  'MEMBER', '2026-01-05', 'ACTIVE'),
    -- Sun Devil Gaming
    ((SELECT id FROM users WHERE email = 'leader2@sundevil.com'),  (SELECT clubId FROM clubs WHERE name = 'Sun Devil Gaming'),        'LEADER', '2021-02-10', 'ACTIVE'),
    ((SELECT id FROM users WHERE email = 'student4@sundevil.com'), (SELECT clubId FROM clubs WHERE name = 'Sun Devil Gaming'),        'MEMBER', '2026-03-01', 'ACTIVE'),
    -- ASU Arts Collective
    ((SELECT id FROM users WHERE email = 'leader3@sundevil.com'),  (SELECT clubId FROM clubs WHERE name = 'ASU Arts Collective'),     'LEADER', '2016-05-15', 'ACTIVE'),
    ((SELECT id FROM users WHERE email = 'student4@sundevil.com'), (SELECT clubId FROM clubs WHERE name = 'ASU Arts Collective'),     'MEMBER', '2026-02-10', 'ACTIVE'),
    -- Sun Devil Fitness
    ((SELECT id FROM users WHERE email = 'leader@sundevil.com'),   (SELECT clubId FROM clubs WHERE name = 'Sun Devil Fitness'),       'LEADER', '2019-03-01', 'ACTIVE'),
    ((SELECT id FROM users WHERE email = 'student@sundevil.com'),  (SELECT clubId FROM clubs WHERE name = 'Sun Devil Fitness'),       'MEMBER', '2026-03-15', 'ACTIVE');

------------------------------------------------
-- MEMBERSHIP REQUESTS
------------------------------------------------
INSERT OR IGNORE INTO membershipRequests (studentId, clubId, status, requestDate) VALUES
    ((SELECT id FROM users WHERE email = 'student@sundevil.com'),  (SELECT clubId FROM clubs WHERE name = 'ASU Robotics'),    'PENDING', '2026-04-01'),
    ((SELECT id FROM users WHERE email = 'student2@sundevil.com'), (SELECT clubId FROM clubs WHERE name = 'ASU Robotics'),    'PENDING', '2026-04-02'),
    ((SELECT id FROM users WHERE email = 'student3@sundevil.com'), (SELECT clubId FROM clubs WHERE name = 'ASU Coding Club'), 'PENDING', '2026-04-03'),
    ((SELECT id FROM users WHERE email = 'student3@sundevil.com'), (SELECT clubId FROM clubs WHERE name = 'ASU Robotics'),    'PENDING', '2026-04-05'),
    ((SELECT id FROM users WHERE email = 'student5@sundevil.com'), (SELECT clubId FROM clubs WHERE name = 'Sun Devil Gaming'), 'PENDING', '2026-04-08');

------------------------------------------------
-- CONTENT + EVENTS  (one content row per event, inserted immediately before it)
-- content rows 1–10 belong to events.
------------------------------------------------

-- Event 1: Tech Talk
INSERT INTO content (createdBy, createdDate, status, isFlagged) VALUES
    ((SELECT id FROM users WHERE email = 'leader@sundevil.com'), '2026-03-15', 'ACTIVE', 0);
INSERT OR IGNORE INTO events (contentId, title, description, category, location, event_date, capacity, is_paid, hostedByClub, status) VALUES
    (last_insert_rowid(), 'Tech Talk: AI Trends', 'Learn about AI in 2026', 'Technology', 'Room 101', '2026-04-10', 100, 0, (SELECT clubId FROM clubs WHERE name = 'ASU Coding Club'), 'ACTIVE');

-- Event 2: Music Night
INSERT INTO content (createdBy, createdDate, status, isFlagged) VALUES
    ((SELECT id FROM users WHERE email = 'leader2@sundevil.com'), '2026-03-16', 'ACTIVE', 0);
INSERT OR IGNORE INTO events (contentId, title, description, category, location, event_date, capacity, is_paid, hostedByClub, status) VALUES
    (last_insert_rowid(), 'Music Night', 'Live performances', 'Music', 'Auditorium', '2026-04-12', 200, 1, (SELECT clubId FROM clubs WHERE name = 'Sun Devil Music Society'), 'ACTIVE');

-- Event 3: Hackathon 2026
INSERT INTO content (createdBy, createdDate, status, isFlagged) VALUES
    ((SELECT id FROM users WHERE email = 'leader@sundevil.com'), '2026-03-17', 'ACTIVE', 0);
INSERT OR IGNORE INTO events (contentId, title, description, category, location, event_date, capacity, is_paid, hostedByClub, status) VALUES
    (last_insert_rowid(), 'Hackathon 2026', '24-hour coding event', 'Technology', 'Engineering Building', '2026-04-20', 150, 0, (SELECT clubId FROM clubs WHERE name = 'ASU Coding Club'), 'ACTIVE');

-- Event 4: Robotics Workshop
INSERT INTO content (createdBy, createdDate, status, isFlagged) VALUES
    ((SELECT id FROM users WHERE email = 'leader@sundevil.com'), '2026-03-18', 'ACTIVE', 0);
INSERT OR IGNORE INTO events (contentId, title, description, category, location, event_date, capacity, is_paid, hostedByClub, status) VALUES
    (last_insert_rowid(), 'Robotics Workshop', 'Build your first robot', 'Technology', 'Lab 3', '2026-04-18', 40, 0, (SELECT clubId FROM clubs WHERE name = 'ASU Robotics'), 'ACTIVE');

-- Event 5: Career Fair Prep
INSERT INTO content (createdBy, createdDate, status, isFlagged) VALUES
    ((SELECT id FROM users WHERE email = 'leader3@sundevil.com'), '2026-03-19', 'ACTIVE', 0);
INSERT OR IGNORE INTO events (contentId, title, description, category, location, event_date, capacity, is_paid, hostedByClub, status) VALUES
    (last_insert_rowid(), 'Career Fair Prep', 'Resume + interview tips', 'Career', 'Student Center', '2026-04-22', 80, 0, (SELECT clubId FROM clubs WHERE name = 'ASU Career Development'), 'ACTIVE');

-- Event 6: Startup Pitch Night
INSERT INTO content (createdBy, createdDate, status, isFlagged) VALUES
    ((SELECT id FROM users WHERE email = 'leader3@sundevil.com'), '2026-03-20', 'ACTIVE', 0);
INSERT OR IGNORE INTO events (contentId, title, description, category, location, event_date, capacity, is_paid, hostedByClub, status) VALUES
    (last_insert_rowid(), 'Startup Pitch Night', 'Pitch your ideas', 'Business', 'Room 205', '2026-04-25', 60, 1, (SELECT clubId FROM clubs WHERE name = 'ASU Career Development'), 'ACTIVE');

-- Event 7: Gaming Tournament
INSERT INTO content (createdBy, createdDate, status, isFlagged) VALUES
    ((SELECT id FROM users WHERE email = 'leader2@sundevil.com'), '2026-03-21', 'ACTIVE', 0);
INSERT OR IGNORE INTO events (contentId, title, description, category, location, event_date, capacity, is_paid, hostedByClub, status) VALUES
    (last_insert_rowid(), 'Gaming Tournament', 'Compete in esports', 'Recreation', 'Gaming Lounge', '2026-04-28', 120, 0, (SELECT clubId FROM clubs WHERE name = 'Sun Devil Gaming'), 'ACTIVE');

-- Event 8: Art Showcase
INSERT INTO content (createdBy, createdDate, status, isFlagged) VALUES
    ((SELECT id FROM users WHERE email = 'leader3@sundevil.com'), '2026-03-22', 'ACTIVE', 0);
INSERT OR IGNORE INTO events (contentId, title, description, category, location, event_date, capacity, is_paid, hostedByClub, status) VALUES
    (last_insert_rowid(), 'Art Showcase', 'Student artwork display', 'Art', 'Gallery Hall', '2026-05-01', 50, 0, (SELECT clubId FROM clubs WHERE name = 'ASU Arts Collective'), 'ACTIVE');

-- Event 9: Fitness Bootcamp
INSERT INTO content (createdBy, createdDate, status, isFlagged) VALUES
    ((SELECT id FROM users WHERE email = 'leader@sundevil.com'), '2026-03-23', 'ACTIVE', 0);
INSERT OR IGNORE INTO events (contentId, title, description, category, location, event_date, capacity, is_paid, hostedByClub, status) VALUES
    (last_insert_rowid(), 'Fitness Bootcamp', 'Outdoor workout session', 'Health', 'Campus Field', '2026-05-03', 70, 0, (SELECT clubId FROM clubs WHERE name = 'Sun Devil Fitness'), 'ACTIVE');

-- Event 10: Movie Night
INSERT INTO content (createdBy, createdDate, status, isFlagged) VALUES
    ((SELECT id FROM users WHERE email = 'leader@sundevil.com'), '2026-03-24', 'ACTIVE', 0);
INSERT OR IGNORE INTO events (contentId, title, description, category, location, event_date, capacity, is_paid, hostedByClub, status) VALUES
    (last_insert_rowid(), 'Movie Night', 'Outdoor movie screening', 'Entertainment', 'Quad Lawn', '2026-05-05', 200, 0, (SELECT clubId FROM clubs WHERE name = 'Sun Devil Fitness'), 'ACTIVE');

------------------------------------------------
-- CONTENT + ANNOUNCEMENTS  (content rows 11–14)
------------------------------------------------

-- Announcement 1: Welcome
INSERT INTO content (createdBy, createdDate, status, isFlagged) VALUES
    ((SELECT id FROM users WHERE email = 'leader@sundevil.com'), '2026-01-15', 'ACTIVE', 0);
INSERT OR IGNORE INTO announcements (contentId, title, body, postedDate, postedToClub, createdBy, status) VALUES
    (last_insert_rowid(),
     'Welcome to the Coding Club!',
     'Welcome to the ASU Coding Club! We are excited to have you here. Stay tuned for upcoming events and workshops.',
     '2026-01-15',
     (SELECT clubId FROM clubs WHERE name = 'ASU Coding Club'),
     (SELECT id FROM users WHERE email = 'leader@sundevil.com'),
     'PUBLISHED');

-- Announcement 2: Spring Hackathon
INSERT INTO content (createdBy, createdDate, status, isFlagged) VALUES
    ((SELECT id FROM users WHERE email = 'leader@sundevil.com'), '2026-02-10', 'ACTIVE', 0);
INSERT OR IGNORE INTO announcements (contentId, title, body, postedDate, postedToClub, createdBy, status) VALUES
    (last_insert_rowid(),
     'Spring Hackathon Announced',
     'We will be hosting a 24-hour hackathon this spring. Registration opens next week. Prizes for top three teams!',
     '2026-02-10',
     (SELECT clubId FROM clubs WHERE name = 'ASU Coding Club'),
     (SELECT id FROM users WHERE email = 'leader@sundevil.com'),
     'PUBLISHED');

-- Announcement 3: New Meeting Schedule (DRAFT — no postedDate)
INSERT INTO content (createdBy, createdDate, status, isFlagged) VALUES
    ((SELECT id FROM users WHERE email = 'leader@sundevil.com'), '2026-03-01', 'ACTIVE', 0);
INSERT OR IGNORE INTO announcements (contentId, title, body, postedDate, postedToClub, createdBy, status) VALUES
    (last_insert_rowid(),
     'New Meeting Schedule',
     'We are updating our weekly meeting schedule for the rest of the semester. More details coming soon.',
     NULL,
     (SELECT clubId FROM clubs WHERE name = 'ASU Coding Club'),
     (SELECT id FROM users WHERE email = 'leader@sundevil.com'),
     'DRAFT');

-- Announcement 4: Battle of the Bands — FLAGGED
INSERT INTO content (createdBy, createdDate, status, isFlagged, flagReason) VALUES
    ((SELECT id FROM users WHERE email = 'leader2@sundevil.com'), '2026-03-01', 'ACTIVE', 1, 'Reported as spam by multiple members');
INSERT OR IGNORE INTO announcements (contentId, title, body, postedDate, postedToClub, createdBy, status) VALUES
    (last_insert_rowid(),
     'Battle of the Bands',
     'Sun Devil Music Society is proud to announce our annual Battle of the Bands event this April!',
     '2026-03-01',
     (SELECT clubId FROM clubs WHERE name = 'Sun Devil Music Society'),
     (SELECT id FROM users WHERE email = 'leader2@sundevil.com'),
     'PUBLISHED');

------------------------------------------------
-- FLAGGED EVENT CONTENT  (one additional flagged event)
------------------------------------------------
INSERT INTO content (createdBy, createdDate, status, isFlagged, flagReason) VALUES
    ((SELECT id FROM users WHERE email = 'leader2@sundevil.com'), '2026-04-01', 'ACTIVE', 1, 'Event description contains inappropriate language');
INSERT OR IGNORE INTO events (contentId, title, description, category, location, event_date, capacity, is_paid, hostedByClub, status) VALUES
    (last_insert_rowid(), 'Late Night LAN Party', 'Unofficial after-hours gaming session', 'Recreation', 'Off-campus venue', '2026-04-30', 30, 0, (SELECT clubId FROM clubs WHERE name = 'Sun Devil Gaming'), 'ACTIVE');

------------------------------------------------
-- EVENT REGISTRATIONS
------------------------------------------------
INSERT OR IGNORE INTO eventRegistrations (studentId, eventId, registrationDate) VALUES
    ((SELECT id FROM users WHERE email = 'student@sundevil.com'),  (SELECT id FROM events WHERE title = 'Tech Talk: AI Trends'),  '2026-04-01'),
    ((SELECT id FROM users WHERE email = 'student@sundevil.com'),  (SELECT id FROM events WHERE title = 'Hackathon 2026'),        '2026-04-02'),
    ((SELECT id FROM users WHERE email = 'student2@sundevil.com'), (SELECT id FROM events WHERE title = 'Music Night'),           '2026-04-03'),
    ((SELECT id FROM users WHERE email = 'student3@sundevil.com'), (SELECT id FROM events WHERE title = 'Career Fair Prep'),      '2026-04-05'),
    ((SELECT id FROM users WHERE email = 'student4@sundevil.com'), (SELECT id FROM events WHERE title = 'Gaming Tournament'),     '2026-04-06'),
    ((SELECT id FROM users WHERE email = 'student5@sundevil.com'), (SELECT id FROM events WHERE title = 'Tech Talk: AI Trends'),  '2026-04-07'),
    ((SELECT id FROM users WHERE email = 'student5@sundevil.com'), (SELECT id FROM events WHERE title = 'Hackathon 2026'),        '2026-04-07');