package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.UserDTO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class userInterfaceDTOTest {

    @Test
    void selectAllUsersAsDTO() {
        userInterface userInterface = new userInterface();
        userInterface.createTable();

        userInterface.addUser("OrigFact", "updateuser", "originalpass", "user");
        userInterface.addUser("OrigFact", "updateuser1", "originalpass1", "user");
        userInterface.addUser("OrigFact", "updateuser2", "originalpass2", "user");

        List<String> list = userInterface.selectAllUsers();
        List<UserDTO> userDTOList = userInterface.selectAllUsersAsDTO();

        for (int i =0; i< list.size(); ++i){

            String[] boof = list.get(i).split(" ");

            assertEquals(Integer.parseInt(boof[0]), userDTOList.get(i).getUserId());
            assertEquals(boof[1], userDTOList.get(i).getFactoryType());
            assertEquals(boof[2], userDTOList.get(i).getLogin());
            assertEquals(boof[3], userDTOList.get(i).getPassword());
            assertEquals(boof[4], userDTOList.get(i).getRole());
        }
        userInterface.deleteAllUsers();
    }

    @Test
    void selectUserByIdAsDTO() {
        userInterface userInterface = new userInterface();
        userInterface.createTable();

        userInterface.addUser("OrigFact", "updateuser", "originalpass", "user");
        userInterface.addUser("OrigFact", "updateuser1", "originalpass1", "user");
        userInterface.addUser("OrigFact", "updateuser2", "originalpass2", "user");

        int id = userInterface.selectIdByLogin("updateuser1");

        String list1 = userInterface.selectUserById(id);
        UserDTO userDTO = userInterface.selectUserByIdAsDTO(id);

        String[] boof = list1.split(" ");

        assertEquals(Integer.parseInt(boof[0]), userDTO.getUserId());
        assertEquals(boof[1], userDTO.getFactoryType());
        assertEquals(boof[2], userDTO.getLogin());
        assertEquals(boof[3], userDTO.getPassword());
        assertEquals(boof[4], userDTO.getRole());

        userInterface.deleteAllUsers();
    }
}