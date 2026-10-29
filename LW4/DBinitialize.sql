SET client_encoding = 'UTF8';

DROP TABLE IF EXISTS bid, task, category, executor_profile, app_user CASCADE;

CREATE TABLE app_user (
    user_id       INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(60)  NOT NULL,
    full_name     VARCHAR(255) NOT NULL,
    phone         VARCHAR(20)  NOT NULL,
    role          VARCHAR(20)  NOT NULL DEFAULT 'CUSTOMER'
        CHECK (role IN ('CUSTOMER', 'EXECUTOR', 'CUSTOMER_EXECUTOR', 'ADMIN')),
    is_verified   BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE executor_profile (
    user_id        INT PRIMARY KEY REFERENCES app_user (user_id) ON DELETE CASCADE,
    specialization VARCHAR(100) NOT NULL,
    skills         TEXT,
    location       VARCHAR(255),
    hourly_rate    NUMERIC(10, 2) CHECK (hourly_rate IS NULL OR hourly_rate >= 0)
);

CREATE TABLE category (
    category_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    parent_id   INT REFERENCES category (category_id) ON DELETE SET NULL,
    description TEXT
);

CREATE TABLE task (
    task_id            INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    customer_id        INT            NOT NULL REFERENCES app_user (user_id),
    category_id        INT            NOT NULL REFERENCES category (category_id),
    title              VARCHAR(255)   NOT NULL,
    description        TEXT           NOT NULL,
    budget             NUMERIC(10, 2) NOT NULL CHECK (budget > 0),
    deadline           DATE           NOT NULL,
    location           VARCHAR(255),
    status             VARCHAR(20)    NOT NULL DEFAULT 'DRAFT'
        CHECK (status IN ('DRAFT', 'PENDING_MODERATION', 'PUBLISHED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    moderation_comment TEXT,
    created_at         TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE bid (
    bid_id         INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    task_id        INT            NOT NULL REFERENCES task (task_id) ON DELETE CASCADE,
    executor_id    INT            NOT NULL REFERENCES app_user (user_id),
    proposed_price NUMERIC(10, 2) NOT NULL CHECK (proposed_price > 0),
    deadline       DATE           NOT NULL,
    proposal_text  TEXT           NOT NULL,
    status         VARCHAR(20)    NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'CANCELLED')),
    created_at     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (task_id, executor_id)
);

CREATE INDEX idx_task_status_category ON task (status, category_id);
CREATE INDEX idx_bid_task ON bid (task_id);

INSERT INTO app_user (email, password_hash, full_name, phone, role, is_verified) VALUES
    ('admin@khaltura.by',  '$2a$10$4LxXGgtwhr/cCMu9MRX7LeyJONh1GOSVqrZn/fzZiphViO7kdXHJ2', 'Администратор',      '+375290000000', 'ADMIN',             TRUE),
    ('anna@demo.by',       '$2a$10$4LxXGgtwhr/cCMu9MRX7LeyJONh1GOSVqrZn/fzZiphViO7kdXHJ2', 'Анна Ковалёва',      '+375291111111', 'CUSTOMER',          TRUE),
    ('oleg@demo.by',       '$2a$10$4LxXGgtwhr/cCMu9MRX7LeyJONh1GOSVqrZn/fzZiphViO7kdXHJ2', 'Олег Петров',        '+375292222222', 'CUSTOMER_EXECUTOR', TRUE),
    ('sergey@demo.by',     '$2a$10$4LxXGgtwhr/cCMu9MRX7LeyJONh1GOSVqrZn/fzZiphViO7kdXHJ2', 'Сергей Иванов',      '+375293333333', 'EXECUTOR',          TRUE),
    ('andrey@demo.by',     '$2a$10$4LxXGgtwhr/cCMu9MRX7LeyJONh1GOSVqrZn/fzZiphViO7kdXHJ2', 'Андрей Сидоров',     '+375294444444', 'EXECUTOR',          TRUE),
    ('new@demo.by',        '$2a$10$4LxXGgtwhr/cCMu9MRX7LeyJONh1GOSVqrZn/fzZiphViO7kdXHJ2', 'Новый Пользователь', '+375295555555', 'CUSTOMER',          FALSE);

INSERT INTO executor_profile (user_id, specialization, skills, location, hourly_rate) VALUES
    (3, 'Мастер на все руки', 'сборка мебели, мелкий ремонт, сантехника', 'Минск',  20.00),
    (4, 'Маляр-штукатур',     'покраска стен, шпаклёвка, обои',           'Минск',  25.00),
    (5, 'Сборщик мебели',     'сборка мебели, шкафы, кухни',              'Гомель', 18.00);

INSERT INTO category (name, description) VALUES
    ('Ремонт и отделка',  'Бытовые ремонтные работы'),
    ('Сборка мебели',     'Сборка и установка мебели'),
    ('Уборка',            'Уборка помещений'),
    ('Курьерские услуги', 'Доставка документов и посылок'),
    ('Репетиторство',     'Обучение и консультации');
INSERT INTO category (name, parent_id, description) VALUES
    ('Покраска',          1, 'Покраска стен и потолков'),
    ('Сантехника',        1, 'Установка и ремонт сантехники');

INSERT INTO task (customer_id, category_id, title, description, budget, deadline, location, status) VALUES
    (2, 6, 'Покрасить стены в комнате',  'Комната 18 м², краска есть, нужно подготовить стены и покрасить в два слоя',
        250.00, CURRENT_DATE + 14, 'Минск, ул. Сурганова 5',     'PUBLISHED'),
    (2, 2, 'Собрать шкаф-купе',          'Шкаф IKEA PAX 2 м, все детали на месте',
        80.00,  CURRENT_DATE + 7,  'Минск, пр. Независимости 40', 'PUBLISHED'),
    (3, 7, 'Заменить смеситель на кухне', 'Смеситель куплен, старый нужно демонтировать',
        40.00,  CURRENT_DATE + 3,  'Минск, ул. Гикало 9',         'PENDING_MODERATION'),
    (2, 3, 'Генеральная уборка квартиры', 'Двухкомнатная квартира после ремонта',
        150.00, CURRENT_DATE + 10, 'Минск, ул. Кальварийская 21', 'DRAFT'),
    (3, 5, 'Репетитор по математике',    'Подготовка к ЦТ, 2 занятия в неделю',
        30.00,  CURRENT_DATE + 30, 'Гомель',                      'PUBLISHED');

INSERT INTO bid (task_id, executor_id, proposed_price, deadline, proposal_text) VALUES
    (1, 4, 240.00, CURRENT_DATE + 5, 'Покрашу за 3 дня, опыт 8 лет, свои инструменты'),
    (1, 3, 220.00, CURRENT_DATE + 7, 'Сделаю аккуратно, могу начать в выходные'),
    (1, 5, 300.00, CURRENT_DATE + 4, 'Быстро и качественно'),
    (2, 5, 70.00,  CURRENT_DATE + 2, 'Собираю PAX регулярно, займёт 3 часа'),
    (2, 3, 85.00,  CURRENT_DATE + 3, 'Соберу и закреплю к стене');
