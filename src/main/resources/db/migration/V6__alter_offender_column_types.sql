ALTER TABLE offender ALTER COLUMN suspended_sentence_order SET DATA TYPE BOOLEAN USING suspended_sentence_order::BOOLEAN;
ALTER TABLE offender ALTER COLUMN breach SET DATA TYPE BOOLEAN USING breach::BOOLEAN;
ALTER TABLE offender ALTER COLUMN awaiting_psr SET DATA TYPE BOOLEAN USING awaiting_psr::BOOLEAN;
ALTER TABLE offender ALTER COLUMN pre_sentence_activity SET DATA TYPE BOOLEAN USING pre_sentence_activity::BOOLEAN;
ALTER TABLE offender ALTER COLUMN previously_known_termination_date SET DATA TYPE DATE USING previously_known_termination_date::DATE;