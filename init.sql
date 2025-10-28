WAITFOR DELAY '00:00:05'
GO

IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'StemHub')
BEGIN
    CREATE DATABASE StemHub;
    PRINT 'Database StemHub created.';
END;
GO