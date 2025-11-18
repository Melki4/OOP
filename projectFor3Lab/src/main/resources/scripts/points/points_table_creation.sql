CREATE TABLE IF NOT EXISTS points(
	point_id SERIAL NOT NULL PRIMARY KEY,
	xValue DOUBLE PRECISION NOT NULL,
	yValue DOUBLE PRECISION NOT NULL,
	function_id LONG NOT NULL,

	CONSTRAINT function_id_fk FOREIGN KEY (function_id) REFERENCES math_functions (math_function_id)
);