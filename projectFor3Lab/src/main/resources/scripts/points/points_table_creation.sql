CREATE TABLE IF NOT EXISTS points(
	point_id SERIAL NOT NULL PRIMARY KEY,
	xValue DECIMAL NOT NULL,
	yValue DECIMAL NOT NULL,
	function_id BIGINT NOT NULL,

	CONSTRAINT function_id_fk FOREIGN KEY (function_id) REFERENCES math_functions (math_function_id)
);