package service;

import ru.ssau.tk._repfor2lab_._OOP_.Dao.JdbcSimpleFunctionRepository;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.SimpleFunctionsDTO;
import ru.ssau.tk._repfor2lab_._OOP_.databaseEnteties.SimpleFunctions;

import java.util.List;

public class SimpleFunctionService {
    private final JdbcSimpleFunctionRepository simpleFunctionRepository;

    {
        simpleFunctionRepository = new JdbcSimpleFunctionRepository();
    }

    public List<SimpleFunctionsDTO> findAllSimpleFunctions(){
        return simpleFunctionRepository.findAllSimpleFunctionsAsDTO();
    }

    public List<SimpleFunctionsDTO> findAllSimpleFunctionsSorted(){
        return simpleFunctionRepository.findAllSimpleFunctionsSortedByLocalNameAsDTO();
    }

    public boolean existsByLocalName(String local_name){
        return simpleFunctionRepository.existSimpleFunction(local_name);
    }

    public SimpleFunctionsDTO createSimpleFunction(String local_name){
        SimpleFunctions simpleFunctions = simpleFunctionRepository.createSimpleFunction(local_name);
        return new SimpleFunctionsDTO(simpleFunctions.getLocalName());
    }

    public void updateSimpleFunction(String oldName, String newName){
        simpleFunctionRepository.updateSimpleFunctionName(oldName, newName);
    }

    public boolean deleteAllFunctions(){
        return simpleFunctionRepository.deleteAllFunctions();
    }

    public boolean deleteSimpleFunction(String name){
        return simpleFunctionRepository.deleteSimpleFunctionByName(name);
    }
}
