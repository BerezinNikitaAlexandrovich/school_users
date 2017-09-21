ALTER DATABASE IF EXISTS school_berezin CONNECTION LIMIT 0;
SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = 'school_berezin';
DROP DATABASE IF EXISTS school_berezin;
DROP USER school_berezin_user;
