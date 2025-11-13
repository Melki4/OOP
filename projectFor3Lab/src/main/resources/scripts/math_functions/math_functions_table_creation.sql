CREATE TABLE IF NOT EXISTS math_functions(
	math_function_id SERIAL NOT NULL PRIMARY KEY,
	function_name VARCHAR(128) NOT NULL,
	amount_of_dots BIGINT NOT NULL,
	left_boarder BIGINT NOT NULL,
	right_boarder BIGINT NOT NULL,

	owner_id BIGINT NOT NULL,
	function_type VARCHAR(16) DEFAULT 'tabulated_f' NOT NULL,

	CONSTRAINT owner_id_fk FOREIGN KEY (owner_id) REFERENCES users (user_id)
);