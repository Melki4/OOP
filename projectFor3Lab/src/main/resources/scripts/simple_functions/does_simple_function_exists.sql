SELECT EXISTS (
    SELECT 1
    FROM simple_functions
    WHERE local_name = ?
);