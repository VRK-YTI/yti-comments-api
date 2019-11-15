--- Migration to support uris and sequence_ids

--- Sequence id additions to tables
ALTER TABLE commentround ADD COLUMN sequence_id integer;
ALTER TABLE commentthread ADD COLUMN sequence_id integer;
ALTER TABLE comment ADD COLUMN sequence_id integer;

--- URI additions to tables
ALTER TABLE commentround ADD COLUMN uri text UNIQUE;
ALTER TABLE commentthread ADD COLUMN uri text UNIQUE;
ALTER TABLE comment ADD COLUMN uri text UNIQUE;

--- Create commentround sequence
CREATE SEQUENCE seq_rounds START WITH 1 INCREMENT BY 1;

--- Function to create sequence for new commentrounds
CREATE OR REPLACE FUNCTION create_sequence_for_commentround_commentthreads() RETURNS TRIGGER AS $$
DECLARE
  sql text := 'CREATE SEQUENCE seq_round_threads_' || replace(NEW.id::varchar, '-', '_');
BEGIN
  EXECUTE sql;
  return NEW;
END;
$$ LANGUAGE plpgsql;

--- Trigger that automatically creates new sequences when commentrounds are added
CREATE TRIGGER trigger_sequence_for_commentround_commentthreads
  AFTER INSERT
  ON commentround
  FOR EACH ROW
EXECUTE PROCEDURE create_sequence_for_commentround_commentthreads();

--- Function to create sequence for new commentthreads
CREATE OR REPLACE FUNCTION create_sequence_for_commentthread_comments() RETURNS TRIGGER AS $$
DECLARE
  sql text := 'CREATE SEQUENCE seq_thread_comments_' || replace(NEW.id::varchar, '-', '_');
BEGIN
  EXECUTE sql;
  return NEW;
END;
$$ LANGUAGE plpgsql;

--- Trigger that automatically creates new sequences when commentthreads are added
CREATE TRIGGER trigger_sequence_for_commentthread_comments
  AFTER INSERT
  ON commentthread
  FOR EACH ROW
EXECUTE PROCEDURE create_sequence_for_commentthread_comments();

--- Function that drops sequences for deleted commentrounds
CREATE OR REPLACE FUNCTION drop_sequence_for_commentround_commentthreads() RETURNS TRIGGER AS $$
DECLARE
  sql text := 'DROP SEQUENCE IF EXISTS seq_round_threads_' || replace(OLD.id::varchar, '-', '_');
BEGIN
  EXECUTE sql;
  return OLD;
END;
$$ LANGUAGE plpgsql;

--- Trigger that automatically removes earlier sequences when commentrounds are deleted
CREATE TRIGGER trigger_drop_existing_commentround_commentthreads_sequence
  AFTER DELETE
  ON commentround
  FOR EACH ROW
EXECUTE PROCEDURE drop_sequence_for_commentround_commentthreads();

-- Function that drops sequences for deleted commentthreads
CREATE OR REPLACE FUNCTION drop_sequence_for_commentthread_comments() RETURNS TRIGGER AS $$
DECLARE
  sql text := 'DROP SEQUENCE IF EXISTS seq_thread_comments_' || replace(OLD.id::varchar, '-', '_');
BEGIN
  EXECUTE sql;
  return OLD;
END;
$$ LANGUAGE plpgsql;

--- Trigger that automatically removes earlier sequences when commentthreads are deleted
CREATE TRIGGER trigger_drop_existing_commentthread_comments_sequence
  AFTER DELETE
  ON commentthread
  FOR EACH ROW
EXECUTE PROCEDURE drop_sequence_for_commentthread_comments();

--- Function that creates commentthread sequences per each commentround
CREATE OR REPLACE FUNCTION create_commentthread_sequences() RETURNS VOID AS $$
DECLARE
  rec RECORD;
  sequenceName varchar;
  sql text;
BEGIN
  FOR rec IN SELECT id
             FROM commentround
             ORDER BY created
    LOOP
      RAISE NOTICE '%', rec.id;
      sequenceName := 'seq_round_threads_' || replace(rec.id::varchar, '-', '_');
      sql := 'CREATE SEQUENCE ' || sequenceName || ' START WITH 1 INCREMENT BY 1';
      EXECUTE sql;
    END LOOP;
  RETURN;
END;
$$ LANGUAGE plpgsql;

SELECT * FROM create_commentthread_sequences();

DROP FUNCTION create_commentthread_sequences();

--- Function that creates comment sequences per each commentthread
CREATE OR REPLACE FUNCTION create_comment_sequences() RETURNS VOID AS $$
DECLARE
  rec RECORD;
  sequenceName varchar;
  sql text;
BEGIN
  FOR rec IN SELECT id
             FROM commentthread
             ORDER BY created
    LOOP
      RAISE NOTICE '%', rec.id;
      sequenceName := 'seq_thread_comments_' || replace(rec.id::varchar, '-', '_');
      sql := 'CREATE SEQUENCE ' || sequenceName || ' START WITH 1 INCREMENT BY 1';
      EXECUTE sql;
    END LOOP;
  RETURN;
END;
$$ LANGUAGE plpgsql;

SELECT * FROM create_comment_sequences();

DROP FUNCTION create_comment_sequences();

-- Function that populates sequences to earlier commentthreads
CREATE OR REPLACE FUNCTION set_sequence_ids_for_commentthreads() RETURNS VOID AS $$
DECLARE
  rec RECORD;
  sql text;
  commentThreadId varchar;
BEGIN
  FOR rec IN SELECT id, commentround_id
             FROM commentthread
             ORDER BY created
    LOOP
      RAISE NOTICE '%', rec.id;
      RAISE NOTICE '%', rec.commentround_id;
      commentThreadId := rec.id::varchar;
      sql := 'UPDATE commentthread SET sequence_id = (SELECT nextval(''seq_round_threads_'
                               || replace(rec.commentround_id::varchar, '-', '_')
                               || '''))'
                               || ' WHERE id = ''' || commentThreadId || '''';
      EXECUTE sql;
    END LOOP;
  RETURN;
END;
$$ LANGUAGE plpgsql;

SELECT * FROM set_sequence_ids_for_commentthreads();

DROP FUNCTION set_sequence_ids_for_commentthreads();

-- Function that populates sequences to earlier comments
CREATE OR REPLACE FUNCTION set_sequence_ids_for_comments() RETURNS VOID AS $$
DECLARE
  rec RECORD;
  sql text;
  commentId varchar;
BEGIN
  FOR rec IN SELECT id, commentthread_id
             FROM comment
             ORDER BY created
    LOOP
      RAISE NOTICE '%', rec.id;
      RAISE NOTICE '%', rec.commentthread_id;
      commentId := rec.id::varchar;
      sql := 'UPDATE comment SET sequence_id = (SELECT nextval(''seq_thread_comments_'
                               || replace(rec.commentthread_id::varchar, '-', '_')
                               || '''))'
                               || ' WHERE id = ''' || commentId || '''';
      EXECUTE sql;
    END LOOP;
  RETURN;
END;
$$ LANGUAGE plpgsql;

SELECT * FROM set_sequence_ids_for_comments();

DROP FUNCTION set_sequence_ids_for_comments();

--- Migrate earlier data to support sequences
UPDATE commentround SET sequence_id = NEXTVAL('seq_rounds');

--- Migrate earlier data to support uris
UPDATE commentround SET uri = CONCAT('http://uri.suomi.fi/comments/round/', sequence_id);
UPDATE commentthread SET uri = CONCAT((SELECT uri FROM commentround WHERE id = commentround_id), '/thread/', sequence_id);
UPDATE comment SET uri = CONCAT((SELECT uri FROM commentthread WHERE id = commentthread_id), '/comment/', sequence_id);

--- Add not null constraints to uris
ALTER TABLE commentround ALTER COLUMN uri SET NOT NULL;
ALTER TABLE commentthread ALTER COLUMN uri SET NOT NULL;
ALTER TABLE comment ALTER COLUMN uri SET NOT NULL;

--- Add unique constraints to uris for each table
ALTER TABLE commentround ADD CONSTRAINT uq_commentround UNIQUE(sequence_id);
ALTER TABLE commentround ALTER COLUMN sequence_id SET NOT NULL;

ALTER TABLE commentthread ADD CONSTRAINT uq_commentround_commentthread UNIQUE(commentround_id, sequence_id);
ALTER TABLE commentthread ALTER COLUMN sequence_id SET NOT NULL;

ALTER TABLE comment ADD CONSTRAINT uq_commentthread_comment UNIQUE(commentthread_id, sequence_id);
ALTER TABLE comment ALTER COLUMN sequence_id SET NOT NULL;
