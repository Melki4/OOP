package service;

import ru.ssau.tk._repfor2lab_._OOP_.Dao.JdbcMathFunctionRepository;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.MathFunctionsDTO;
import ru.ssau.tk._repfor2lab_._OOP_.databaseEnteties.MathFunctions;

import java.util.List;

public class MathFunctionService {
    private final JdbcMathFunctionRepository mathFunctionRepository;

    {
        mathFunctionRepository = new JdbcMathFunctionRepository();
    }

    public List<MathFunctionsDTO> findMathFunctionsByUserId(Integer id){
        return mathFunctionRepository.findMathFunctionsByUserIdAsDTO(id);
    }

    public List<MathFunctionsDTO> findMathFunctionsByName(String name, Integer user_id){
        return mathFunctionRepository.findMathFunctionsByNameAsDTO(name, user_id);
    }

    public List<MathFunctionsDTO> findMathFunctionsByName(String name){
        return mathFunctionRepository.findMathFunctionsByNameAsDTO(name);
    }

    public MathFunctionsDTO findMathFunctionComplex(Double leftBoard, Double rightBoard, Integer amountOfDots,
                                                    String functionName, Integer owner_id){
        MathFunctions mathFunctions = mathFunctionRepository.findMathFunctionComplex(leftBoard, rightBoard, amountOfDots, functionName, owner_id);
        return new MathFunctionsDTO(mathFunctions.getFunctionId(), mathFunctions.getFunctionName(), mathFunctions.getAmountOfDots(),
                mathFunctions.getLeftBorder(), mathFunctions.getRightBorder(), mathFunctions.getOwnerId(), mathFunctions.getFunctionType());
    }

    public boolean existsMathFunctionComplex(Double leftBoard, Double rightBoard, Integer amountOfDots,
                                             String functionName, Integer owner_id){
        return mathFunctionRepository.existsFunctionComplex(leftBoard, rightBoard, amountOfDots, functionName, owner_id);
    }

    public MathFunctionsDTO createMathFunction(String function_name, int amount_of_dots, double left_boarder,
                                               double right_boarder, int owner_id, String function_type){
        MathFunctions mathFunctions = mathFunctionRepository.createMathFunction(function_name,
                amount_of_dots, left_boarder,  right_boarder, owner_id, function_type);
        return new MathFunctionsDTO(mathFunctions.getFunctionId(), mathFunctions.getFunctionName(), mathFunctions.getAmountOfDots(),
                mathFunctions.getLeftBorder(), mathFunctions.getRightBorder(), mathFunctions.getOwnerId(), mathFunctions.getFunctionType());
    }

    public MathFunctionsDTO findMathFunctionByFunctionId(int id){
        return mathFunctionRepository.findMathFunctionByFunctionId(id);
    }

    public void updateFunctionName(String name, int id){
        mathFunctionRepository.updateFunctionNameByFunctionId(name, id);
    }

    public boolean deleteAllFunctions(){
        return mathFunctionRepository.deleteAllFunctions();
    }

    public boolean deleteMathFunctionByFunctionId(int id){
        return mathFunctionRepository.deleteMathFunctionByFunctionId(id);
    }

    public boolean deleteMathFunctionsByUserId(int id){
        return mathFunctionRepository.deleteMathFunctionsByUserId(id);
    }
}
