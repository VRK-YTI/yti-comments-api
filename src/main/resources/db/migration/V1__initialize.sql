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
  fixedthreads bool NOT NULL,
  openthreads bool NOT NULL,
  startdate date NULL,
  enddate date NULL,
  created timestamp without time zone NOT NULL,
  modified timestamp without time zone NOT NULL,
  source_id uuid NOT NULL,
  CONSTRAINT commentround_pkey PRIMARY KEY (id),
  CONSTRAINT fk_source_id FOREIGN KEY (source_id) REFERENCES source (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE commentround_sourcelabel (
  commentround_id uuid NOT NULL,
  language text NOT NULL,
  sourcelabel text NOT NULL,
  CONSTRAINT commentround_sourcelabel_pkey PRIMARY KEY (commentround_id, language),
  CONSTRAINT fk_commentround_sourcelabel FOREIGN KEY (commentround_id) REFERENCES commentround (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE TABLE organization (
  id uuid UNIQUE NOT NULL,
  url text NULL,
  removed bool NULL,
  CONSTRAINT organization_pkey PRIMARY KEY (id)
);

CREATE TABLE organization_preflabel (
  organization_id uuid NOT NULL,
  language text NOT NULL,
  preflabel text NOT NULL,
  CONSTRAINT organization_preflabel_pkey PRIMARY KEY (organization_id, language),
  CONSTRAINT fk_organization_id FOREIGN KEY (organization_id) REFERENCES organization (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE TABLE organization_description (
  organization_id uuid NOT NULL,
  language text NOT NULL,
  description text NOT NULL,
  CONSTRAINT organization_description_pkey PRIMARY KEY (organization_id, language),
  CONSTRAINT fk_organization_id FOREIGN KEY (organization_id) REFERENCES organization (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE TABLE commentround_organization (
  commentround_id uuid NOT NULL,
  organization_id uuid NOT NULL,
  CONSTRAINT commentround_organization_pkey PRIMARY KEY (commentround_id, organization_id),
  CONSTRAINT fk_commentround_id FOREIGN KEY (commentround_id) REFERENCES commentround (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_organization_id FOREIGN KEY (organization_id) REFERENCES organization (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE table commentthread (
  id uuid UNIQUE NOT NULL,
  resourceuri text NOT NULL,
  proposedtext text NULL,
  proposedstatus text NULL,
  user_id uuid NOT NULL,
  created timestamp without time zone NOT NULL,
  commentround_id uuid NOT NULL,
  CONSTRAINT commentthread_pkey PRIMARY KEY (id),
  CONSTRAINT fk_commentround_id FOREIGN KEY (commentround_id) REFERENCES commentround (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE TABLE commentthread_label (
  commentthread_id uuid NOT NULL,
  language text NOT NULL,
  label text NOT NULL,
  CONSTRAINT commentthread_label_pkey PRIMARY KEY (commentthread_id, language),
  CONSTRAINT fk_commentthread_label FOREIGN KEY (commentthread_id) REFERENCES commentthread (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE TABLE commentthread_definition (
  commentthread_id uuid NOT NULL,
  language text NOT NULL,
  definition text NOT NULL,
  CONSTRAINT commentthread_definition_pkey PRIMARY KEY (commentthread_id, language),
  CONSTRAINT fk_commentthread_label FOREIGN KEY (commentthread_id) REFERENCES commentthread (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE TABLE comment (
  id uuid UNIQUE NOT NULL,
  user_id uuid NOT NULL,
  content text NOT NULL,
  proposedStatus text NULL,
  created timestamp without time zone NOT NULL,
  parentcomment_id uuid NULL,
  commentthread_id uuid NOT NULL,
  CONSTRAINT comment_pkey PRIMARY KEY (id),
  CONSTRAINT fk_commentthread_id FOREIGN KEY (commentthread_id) REFERENCES commentthread (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE CASCADE
);

---