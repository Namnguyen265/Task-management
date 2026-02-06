-- ===========================
-- TABLE: USERS
-- ===========================
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    
    role ENUM('admin', 'employee') NOT NULL DEFAULT 'employee',
    status ENUM('active', 'inactive') NOT NULL DEFAULT 'active',

    start_date DATE,
    end_date DATE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
ALTER TABLE users MODIFY status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE';
ALTER TABLE users MODIFY role ENUM('ADMIN', 'EMPLOYEE') NOT NULL DEFAULT 'EMPLOYEE';
-- ===========================
-- TABLE: PROJECTS
-- ===========================
CREATE TABLE projects (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    description TEXT,

    start_date DATE,
    end_date DATE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

ALTER TABLE projects
ADD COLUMN created_by BIGINT NOT NULL;
ALTER TABLE projects
ADD CONSTRAINT fk_project_created_by
FOREIGN KEY (created_by) REFERENCES users(id)
ON DELETE RESTRICT;

-- ===========================
-- TABLE: PROJECT_MEMBERS (N-N)
-- ===========================
CREATE TABLE project_members (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    project_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,

    role_in_project ENUM('leader', 'member') NOT NULL DEFAULT 'member',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_pm_project
        FOREIGN KEY (project_id) REFERENCES projects(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_pm_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
);

ALTER TABLE project_members MODIFY role_in_project ENUM('LEADER', 'MEMBER') NOT NULL DEFAULT 'MEMBER';


-- Tạo unique để tránh 1 user nằm 2 lần trong cùng project
ALTER TABLE project_members
ADD CONSTRAINT uq_project_user UNIQUE (project_id, user_id);

-- ===========================
-- TABLE: TASKS
-- ===========================
CREATE TABLE tasks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    project_id BIGINT NOT NULL,

    title VARCHAR(200) NOT NULL,
    description TEXT,

    priority ENUM('low', 'medium', 'high') DEFAULT 'medium',

    start_date DATE,
    target_end_date DATE,
    complete_date DATE,

    created_by BIGINT NULL,     -- user tạo task
    assigned_to BIGINT NULL,    -- user được giao task

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Foreign keys
    CONSTRAINT fk_task_project
        FOREIGN KEY (project_id) REFERENCES projects(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_task_created_by
        FOREIGN KEY (created_by) REFERENCES users(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_task_assigned_to
        FOREIGN KEY (assigned_to) REFERENCES users(id)
        ON DELETE SET NULL
);

ALTER TABLE tasks
ADD COLUMN status ENUM('pending', 'in_progress', 'done') 
NOT NULL DEFAULT 'pending';

ALTER TABLE tasks MODIFY priority ENUM('LOW', 'MEDIUM', 'HIGH') NOT NULL DEFAULT 'MEDIUM';
ALTER TABLE tasks MODIFY status ENUM('PENDING', 'IN_PROGRESS', 'DONE') NOT NULL DEFAULT 'PENDING';


-- ===========================
-- TABLE: TASK_ATTACHMENTS
-- ===========================
CREATE TABLE task_attachments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    task_id BIGINT NOT NULL,
    file_url VARCHAR(300) NOT NULL,
    file_type VARCHAR(50),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_attachment_task
        FOREIGN KEY (task_id) REFERENCES tasks(id)
        ON DELETE CASCADE
);

ALTER TABLE task_attachments
CHANGE COLUMN file_url stored_filename VARCHAR(300) NOT NULL;

ALTER TABLE task_attachments
ADD COLUMN original_filename VARCHAR(255) AFTER task_id,
ADD COLUMN file_size BIGINT AFTER file_type,
ADD COLUMN uploaded_by BIGINT AFTER file_size;

ALTER TABLE task_attachments
ADD CONSTRAINT fk_attachment_uploader
    FOREIGN KEY (uploaded_by) REFERENCES users(id)
    ON DELETE SET NULL;

INSERT INTO users (name, email, password, role, status, start_date)
VALUES
('Admin User', 'admin@example.com', '123456', 'admin', 'active', '2024-01-01'),
('Nguyen Van A', 'a@example.com', '123456', 'employee', 'active', '2024-02-01'),
('Tran Thi B', 'b@example.com', '123456', 'employee', 'active', '2024-03-01'),
('Le Van C', 'c@example.com', '123456', 'employee', 'inactive', '2024-04-01');


INSERT INTO projects (name, description, start_date)
VALUES
('E-learning Platform', 'Hệ thống học trực tuyến', '2024-06-01'),
('HR Management', 'Quản lý nhân sự nội bộ', '2024-06-15');

INSERT INTO project_members (project_id, user_id, role_in_project)
VALUES
-- Project 1 (Leader = User 2)
(1, 1, 'member'),
(1, 2, 'leader'),
(1, 3, 'member'),

-- Project 2 (Leader = User 3)
(2, 1, 'member'),
(2, 3, 'leader'),
(2, 4, 'member'),

-- Project 3 (Leader = User 2 again)
(3, 2, 'leader'),
(3, 1, 'member'),
(3, 3, 'member'),

-- Project 4 (Leader = User 1 - admin)
(4, 1, 'leader'),
(4, 2, 'member'),
(4, 3, 'member');

INSERT INTO tasks 
(project_id, title, description, priority, start_date, target_end_date, created_by, assigned_to)
VALUES
(1, 'Phân tích yêu cầu CRM', 'Thu thập và phân tích nghiệp vụ', 'high',
 '2024-05-02', '2024-05-06', 1, 2),

(1, 'Thiết kế ERD CRM', 'Chuẩn hóa bảng và quan hệ', 'medium',
 '2024-05-03', '2024-05-08', 2, 3);


-- Project 2
INSERT INTO tasks 
(project_id, title, description, priority, start_date, target_end_date, created_by, assigned_to)
VALUES
(2, 'Thiết kế UI App', 'Giao diện React Native', 'low',
 '2024-05-11', '2024-05-20', 3, 4),

(2, 'Kết nối API', 'Đồng bộ App và server', 'high',
 '2024-05-12', '2024-05-18', 3, 1);


-- Project 3
INSERT INTO tasks 
(project_id, title, description, priority, start_date, target_end_date, created_by, assigned_to)
VALUES
(3, 'Xây dựng module khóa học', 'Module học trực tuyến', 'medium',
 '2024-06-02', '2024-06-12', 2, 3);


-- Project 4
INSERT INTO tasks 
(project_id, title, description, priority, start_date, target_end_date, created_by, assigned_to)
VALUES
(4, 'Tạo form thêm nhân viên', 'Module quản lý HR', 'high',
 '2024-06-16', '2024-06-20', 1, 2);
 
 
 
 
 -- Task 1
INSERT INTO task_attachments (task_id, file_url, file_type)
VALUES
(1, 'https://example.com/uploads/tasks/1/requirement_crm.pdf', 'pdf'),
(1, 'https://example.com/uploads/tasks/1/usecase_diagram.png', 'image/png');

-- Task 2
INSERT INTO task_attachments (task_id, file_url, file_type)
VALUES
(2, 'https://example.com/uploads/tasks/2/erd_design.png', 'image/png');

-- Task 3
INSERT INTO task_attachments (task_id, file_url, file_type)
VALUES
(3, 'https://example.com/uploads/tasks/3/ui_mockup.jpg', 'image/jpeg');

-- Task 4
INSERT INTO task_attachments (task_id, file_url, file_type)
VALUES
(4, 'https://example.com/uploads/tasks/4/api_specification.pdf', 'pdf'),
(4, 'https://example.com/uploads/tasks/4/app_flowchart.png', 'image/png');

-- Task 5
INSERT INTO task_attachments (task_id, file_url, file_type)
VALUES
(5, 'https://example.com/uploads/tasks/5/module_list.txt', 'text/plain');

-- Task 6
INSERT INTO task_attachments (task_id, file_url, file_type)
VALUES
(6, 'https://example.com/uploads/tasks/6/hr_form_design.jpg', 'image/jpeg');


SELECT pm.project_id, u.name, pm.role_in_project
FROM project_members pm
JOIN users u ON pm.user_id = u.id;

SELECT * FROM users;
SELECT * FROM projects;
SELECT * FROM project_members;
select * from tasks;
select * from task_attachments;

UPDATE projects
SET created_by = 10
WHERE created_by = 0;

SELECT t.id, t.title, u.name AS assigned_to
FROM tasks t
LEFT JOIN users u ON t.assigned_to = u.id;