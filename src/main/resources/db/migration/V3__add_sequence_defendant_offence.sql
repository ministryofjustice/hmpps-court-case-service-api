CREATE SEQUENCE defendant_offence_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    NO MAXVALUE
    CACHE 1000
    OWNED BY defendant_offence.id;

ALTER TABLE defendant_offence ALTER COLUMN id SET DEFAULT nextval('defendant_offence_id_seq');

