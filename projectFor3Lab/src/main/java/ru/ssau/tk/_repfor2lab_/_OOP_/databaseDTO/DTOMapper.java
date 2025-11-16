package ru.ssau.tk._repfor2lab_._OOP_.databaseDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.simpleFunctionInterface;

public class DTOMapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(DTOMapper.class);
    // Трансформация из данных интерфейса в UserDTO
    public static UserDTO toUserDTO(String dbData) {
        LOGGER.info("Начинаем преобразовывать строку с данными о пользователе в UserDTO");
        String[] parts = dbData.split(" ");
        return new UserDTO(
                Long.parseLong(parts[0]),  // userId
                parts[1],                  // factoryType
                parts[2],                  // login
                parts[3],                  // password
                parts[4]                   // role
        );
    }

    // Трансформация из данных интерфейса в PointDTO
    public static PointDTO toPointDTO(String dbData) {
        LOGGER.info("Начинаем преобразовывать строку с данными о пользователе в PointDTO");
        String[] parts = dbData.split(" ");
        return new PointDTO(
                Long.parseLong(parts[0]),  // pointId
                Double.parseDouble(parts[1]), // xValue
                Double.parseDouble(parts[2]), // yValue
                Long.parseLong(parts[3])   // functionId
        );
    }

    // Трансформация из данных интерфейса в MathFunctionDTO
    public static MathFunctionDTO toMathFunctionDTO(String dbData) {
        LOGGER.info("Начинаем преобразовывать строку с данными о пользователе в MathFunctionsDTO");
        String[] parts = dbData.split(" ");
        return new MathFunctionDTO(
                Long.parseLong(parts[0]),   // functionId
                parts[1],                   // functionName
                Integer.parseInt(parts[2]), // amountOfDots
                Double.parseDouble(parts[3]), // leftBorder
                Double.parseDouble(parts[4]), // rightBorder
                Long.parseLong(parts[5]),   // ownerId
                parts[6]                    // functionType
        );
    }

    // Трансформация из данных интерфейса в SimpleFunctionDTO
    public static SimpleFunctionDTO toSimpleFunctionDTO(String dbData) {
        LOGGER.info("Начинаем преобразовывать строку с данными о пользователе в SimpleFunctionDTO");
        String[] parts = dbData.split(" ");

        StringBuilder boof = new StringBuilder();

        for (int i = 2; i<parts.length;++i){
            boof.append(parts[i] + " ");
        }

        return new SimpleFunctionDTO(
                Long.parseLong(parts[0]), //pointId
                parts[1],// functionCode
                boof.toString().trim() // localName
        );
    }
}