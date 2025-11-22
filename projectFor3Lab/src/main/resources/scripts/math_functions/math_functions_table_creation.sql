CREATE TABLE IF NOT EXISTS math_functions(
	math_function_id SERIAL PRIMARY KEY,
	function_name VARCHAR(128) NOT NULL,
	amount_of_dots BIGINT NOT NULL,
	left_boarder DECIMAL NOT NULL,
	right_boarder DECIMAL NOT NULL,

	owner_id BIGINT NOT NULL,
	function_type VARCHAR(64) NOT NULL,

	CONSTRAINT owner_id_fk FOREIGN KEY (owner_id) REFERENCES users (user_id)
);