CREATE DATABASE IF NOT EXISTS userdb;
CREATE DATABASE IF NOT EXISTS vendor_db;
CREATE DATABASE IF NOT EXISTS customer_service_db;
CREATE DATABASE IF NOT EXISTS device_db;
CREATE DATABASE IF NOT EXISTS repairdb;
CREATE DATABASE IF NOT EXISTS audit_service_db;
CREATE DATABASE IF NOT EXISTS order_db;
CREATE DATABASE IF NOT EXISTS notification_db;

GRANT ALL PRIVILEGES ON userdb.* TO 'devicesupply'@'%';
GRANT ALL PRIVILEGES ON vendor_db.* TO 'devicesupply'@'%';
GRANT ALL PRIVILEGES ON customer_service_db.* TO 'devicesupply'@'%';
GRANT ALL PRIVILEGES ON device_db.* TO 'devicesupply'@'%';
GRANT ALL PRIVILEGES ON repairdb.* TO 'devicesupply'@'%';
GRANT ALL PRIVILEGES ON audit_service_db.* TO 'devicesupply'@'%';
GRANT ALL PRIVILEGES ON order_db.* TO 'devicesupply'@'%';
GRANT ALL PRIVILEGES ON notification_db.* TO 'devicesupply'@'%';

FLUSH PRIVILEGES;