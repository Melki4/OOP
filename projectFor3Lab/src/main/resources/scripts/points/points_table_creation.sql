CREATE TABLE IF NOT EXISTS points(
    point_id SERIAL PRIMARY KEY,
    xValue DOUBLE PRECISION NOT NULL,
    yValue DOUBLE PRECISION NOT NULL,
    function_id BIGINT NOT NULL,

    CONSTRAINT function_id_fk FOREIGN KEY (function_id) REFERENCES math_functions (math_function_id),
    CONSTRAINT unique_function_xvalue UNIQUE (function_id, xValue)
);