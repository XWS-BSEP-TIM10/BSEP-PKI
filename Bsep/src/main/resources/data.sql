INSERT INTO role (ID, NAME)
VALUES (1, 'ROLE_USER');

INSERT INTO role (ID, NAME)
VALUES (2, 'ROLE_ADMIN');

INSERT INTO users (ID, FIRST_NAME, LAST_NAME, PASSWORD, PHONE_NUMBER, USERNAME, IS_USING_2FA, SECRET)
VALUES (1, 'Pera', 'Peric', '$2a$10$28MUwyYgna28OIxoUnE7VOpjby0JRJUU0WQV0UZdMX5XA46XAvBCK', '+329032032', 'peraperic', false, 'QDWSM3OYBPGTEVSPB5FKVDM3CSNCWHVK');

INSERT INTO users (ID, FIRST_NAME, LAST_NAME, PASSWORD, PHONE_NUMBER, USERNAME, IS_USING_2FA, SECRET)
VALUES (2, 'Toma', 'Tomic', '$2a$10$28MUwyYgna28OIxoUnE7VOpjby0JRJUU0WQV0UZdMX5XA46XAvBCK', '+32323232', 'tomatomic', false, 'QDWSM3OYBPGTEVSPB5FKVDM3CSNCWHVK');

INSERT INTO users (ID, FIRST_NAME, LAST_NAME, PASSWORD, PHONE_NUMBER, USERNAME, IS_USING_2FA, SECRET)
VALUES (3, 'Steva', 'Stevic', '$2a$10$28MUwyYgna28OIxoUnE7VOpjby0JRJUU0WQV0UZdMX5XA46XAvBCK', '+2342332', 'stevastevic', false, 'QDWSM3OYBPGTEVSPB5FKVDM3CSNCWHVK');

INSERT INTO user_role (ROLE_ID, USER_ID)
VALUES (2, 1);
INSERT INTO user_role (ROLE_ID, USER_ID)
VALUES (1, 2);
INSERT INTO user_role (ROLE_ID, USER_ID)
VALUES (1, 3);

INSERT INTO permission(id, name)
VALUES (1, 'CREATE_CERTIFICATE_PERMISSION');
INSERT INTO permission(id, name)
VALUES (2, 'GET_CERTIFICATES_PERMISSION');
INSERT INTO permission(id, name)
VALUES (3, 'DOWNLOAD_CERTIFICATE_PERMISSION');
INSERT INTO permission(id, name)
VALUES (4, 'REVOKE_CERTIFICATE_PERMISSION');
INSERT INTO permission(id, name)
VALUES (5, 'CHECK_CERTIFICATE_PERMISSION');
INSERT INTO permission(id, name)
VALUES (6, 'GET_USERS_PERMISSION');
INSERT INTO permission(id, name)
VALUES (7, 'GET_ALL_CERTIFICATES_PERMISSION');
INSERT INTO permission(id, name)
VALUES (8, 'CHANGE_PASSWORD_PERMISSION');
INSERT INTO permission(id, name)
VALUES (9, 'GET_2FA_STATUS_PERMISSION');
INSERT INTO permission(id, name)
VALUES (10, 'CHANGE_2FA_STATUS_PERMISSION');

INSERT INTO roles_permissions(role_id, permission_id)
VALUES (1, 1);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (2, 1);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (1, 2);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (2, 7);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (1, 3);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (2, 3);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (2, 4);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (1, 5);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (2, 5);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (1, 6);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (2, 6);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (1, 8);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (2, 8);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (1, 9);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (2, 9);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (1, 10);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (2, 10);