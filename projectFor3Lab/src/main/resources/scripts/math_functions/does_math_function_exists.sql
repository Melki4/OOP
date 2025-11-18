SELECT EXISTS (
    SELECT 1
    FROM math_functions
    WHERE function_name = ?
);