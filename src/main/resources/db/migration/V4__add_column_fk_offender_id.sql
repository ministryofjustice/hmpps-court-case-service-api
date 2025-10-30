ALTER TABLE defendant ADD COLUMN offender_id INT;
ALTER TABLE defendant ADD CONSTRAINT defendant_offender_id_fkey FOREIGN KEY (offender_id) REFERENCES offender(id);