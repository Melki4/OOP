SELECT
    math_functions.math_function_id,
    math_functions.function_name,
    math_functions.amount_of_dots,
    math_functions.left_boarder,
    math_functions.right_boarder,
    math_functions.owner_id,
    users.login,
    math_functions.function_type,
    simple_functions.local_name
 FROM math_functions
 LEFT JOIN users ON users.user_id = math_functions.owner_id
 LEFT JOIN simple_functions ON simple_functions.function_code = math_functions.function_type
 where function_name = ?