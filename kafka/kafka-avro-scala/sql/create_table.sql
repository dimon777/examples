use tran;

create table equity_transaction (
tranId VARCHAR(36),
tranTs VARCHAR(24),
clientId VARCHAR(12),
pdType VARCHAR(12),
symbol VARCHAR(8),
unitNum Integer,
unitPrice Float
);

