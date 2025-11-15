CREATE TABLE IF NOT EXISTS simple_functions(
    simple_function_id SERIAL NOT NULL PRIMARY KEY,
	function_code VARCHAR(32) UNIQUE NOT NULL,
	local_name VARCHAR(32) UNIQUE NOT NULL
);