---

--- Comment round datamodel

CREATE TABLE source (
  id uuid UNIQUE NOT NULL,
  containertype text NOT NULL,
  containeruri text NOT NULL,
  CONSTRAINT source_pkey PRIMARY KEY (id)
);

CREATE TABLE commentround (
  id uuid UNIQUE NOT NULL,
  user_id uuid NOT NULL,
  label text NOT NULL,
  description text NULL,
  status text NOT NULL,
  fixedcomments bool NOT NULL,
  opencomments bool NOT NULL,
  startdate date NULL,
  enddate date NULL,
  created timestamp without time zone NOT NULL,
  modified timestamp without time zone NOT NULL,
  source_id uuid NOT NULL,
  CONSTRAINT commentround_pkey PRIMARY KEY (id),
  CONSTRAINT fk_source_id FOREIGN KEY (source_id) REFERENCES source (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE commentround_group (
  id uuid NOT NULL,
  commentround_id uuid NOT NULL,
  CONSTRAINT commentround_group_pkey PRIMARY KEY (id),
  CONSTRAINT fk_commentround_id FOREIGN KEY (commentround_id) REFERENCES commentround (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE globalcomments (
  id uuid UNIQUE NOT NULL,
  created timestamp without time zone NOT NULL,
  source_id uuid NOT NULL,
  CONSTRAINT globalcomments_pkey PRIMARY KEY (id),
  CONSTRAINT fk_source_id FOREIGN KEY (source_id) REFERENCES source (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE comment (
  id uuid UNIQUE NOT NULL,
  resourceuri text NOT NULL,
  resourcesuggestion text NOT NULL,
  user_id uuid NOT NULL,
  content text NOT NULL,
  proposedStatus text NULL,
  created timestamp without time zone NOT NULL,
  relatedcomment_id uuid NULL,
  commentround_id uuid NOT NULL,
  globalcomments_id uuid NULL,
  CONSTRAINT comment_pkey PRIMARY KEY (id),
  CONSTRAINT fk_globalcomments_id FOREIGN KEY (globalcomments_id) REFERENCES globalcomments (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

---