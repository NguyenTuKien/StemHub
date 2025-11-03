WAITFOR DELAY '00:00:05'
GO

IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'TicketBooth')
BEGIN
    CREATE DATABASE TicketBooth;
    PRINT 'Database TicketBooth created.';
END;
GO