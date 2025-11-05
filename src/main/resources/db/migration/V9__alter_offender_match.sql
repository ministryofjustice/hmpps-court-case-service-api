ALTER TABLE offender_match ALTER COLUMN id TYPE BIGINT;
ALTER TABLE offender_match ALTER COLUMN offender_id TYPE BIGINT;
ALTER TABLE offender_match ALTER COLUMN offender_match_group_id TYPE BIGINT;
ALTER TABLE offender_match ADD COLUMN match_type VARCHAR(255);
ALTER TABLE offender_match ADD COLUMN is_rejected BOOLEAN;
ALTER TABLE offender_match ADD COLUMN aliases JSONB;
ALTER TABLE offender_match ADD COLUMN match_probability FLOAT8;
