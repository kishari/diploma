#select * from messagingdb.messages;

#select * from messagingdb.recipients;

#delete from messagingdb.messages where id > 0;

#delete from messagingdb.recipients where id > 0;

update messagingdb.recipients
set delivery_status = 'NEW'
where id = 109;