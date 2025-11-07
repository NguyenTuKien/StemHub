IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'StemHub')
BEGIN
    CREATE DATABASE StemHub;
    PRINT 'Database StemHub created.';
END;
GO