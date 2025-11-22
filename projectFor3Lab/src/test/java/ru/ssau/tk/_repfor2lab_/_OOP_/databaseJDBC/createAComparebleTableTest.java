//package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Test;
//import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcMathFunctionRepository;
//import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcPointRepository;
//import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcSimpleFunctionRepository;
//import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcUserRepository;
//
//class createAComparebleTableTest {
//    @AfterEach
//    void tearDown() {
//        JdbcSimpleFunctionRepository s = new JdbcSimpleFunctionRepository();
//        s.deleteAllFunctions();
//        var p = new JdbcPointRepository();
//        p.deleteAllPoints();
//        var f = new JdbcMathFunctionRepository();
//        f.deleteAllFunctions();
//        JdbcUserRepository u = new JdbcUserRepository();
//        u.deleteAllUsers();
//    }
//
//    @Test
//    void test(){
//        createAComparebleTable fo_save = new createAComparebleTable();
//        fo_save.testWithATF();
//    }
//}