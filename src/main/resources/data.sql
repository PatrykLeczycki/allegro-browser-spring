insert into role (name)
values ('ROLE_ADMIN');

insert into role (name)
values ('ROLE_USER');

INSERT INTO user (id, created_at, email, password, username, enabled, registration_token, pass_recovery_token) VALUES
(1, NOW(), 'patryk.leczycki1@gmail.com', '$2a$10$XGOnLLwK7gvuoEq5AK5vOe4h.DavvDVawALgj3scEVev6SYUqERt6', null, 0, null, null),
(2, NOW(), '1@1', '$2a$10$ppT.OJXeOr8xcCR4/yF.yeLwbCWaIxyHGfHFXv2ZkQYwD8kgVf/X.', null, 1, null, null),
(3, NOW(), 'test@test', '$2a$10$gbqLj6ZsQYh./3m0at.rEeL/bMp9RpUvlmANmcsqe3EMQRlV613YC', null, 1, null, null),
(4, NOW(), 'admin@admin', '$2a$10$6UefAy/6SYU.PU66ZMPApuQrGJtTWEP9LJS1x7Pk.gKH0vvqMkBKm', null, 1, null, null);

INSERT INTO user_roles (user_id, role_id) VALUES
(1,1), (1,2),
(2,1), (2,2),
(3,1),
(4,1), (4,2);