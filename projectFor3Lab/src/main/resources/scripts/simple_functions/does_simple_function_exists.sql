SELECT EXISTS (
    SELECT 1
    FROM simple_functions
    WHERE function_code = ?
);