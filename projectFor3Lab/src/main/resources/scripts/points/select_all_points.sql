SELECT
    points.point_id,
    points.xvalue,
    points.yvalue,
    points.function_id,
    math_functions.function_name
 FROM points
 LEFT JOIN math_functions ON math_functions.math_function_id = points.function_id