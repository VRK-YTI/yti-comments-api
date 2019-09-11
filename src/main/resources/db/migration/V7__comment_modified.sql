-- Adding modified to comment table with migration

ALTER TABLE comment ADD COLUMN modified TIMESTAMP without TIME ZONE NULL;
UPDATE comment as c SET modified = c.created WHERE modified IS NULL;
ALTER TABLE comment ALTER COLUMN modified SET NOT NULL;

