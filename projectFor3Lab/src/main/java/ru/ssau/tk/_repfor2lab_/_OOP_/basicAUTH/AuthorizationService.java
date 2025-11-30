package ru.ssau.tk._repfor2lab_._OOP_.basicAUTH;

import ru.ssau.tk._repfor2lab_._OOP_.databaseEnteties.Users;

import java.util.logging.Logger;

public class AuthorizationService {
    private static final Logger logger = Logger.getLogger(AuthorizationService.class.getName());

    public static boolean hasAccess(Users user, String method, String path) {
        String userRole = user.getRole();

        // ADMIN - полный доступ
        if ("admin".equals(userRole)) {
            logger.info("Администратору " + user.getLogin() + " предоставлен доступ для " + method + " " + path);
            return true;
        }

        // USER - ограниченный доступ
        if ("user".equals(userRole)) {
            return checkUserAccess(user, method, path);
        }

        logger.warning("Доступ запрещен для роли " + userRole + " к " + method + " " + path);
        return false;
    }

    public static boolean hasAdminAccess(Users user, String method, String path) {
        String userRole = user.getRole();

        // ADMIN - полный доступ
        if ("admin".equals(userRole)) {
            logger.info("Администратору " + user.getLogin() + " предоставлен доступ для " + method + " " + path);
            return true;
        }

        logger.warning("Доступ запрещен для роли " + userRole + " к " + method + " " + path);
        return false;
    }

    private static boolean checkUserAccess(Users user, String method, String path) {
        // GET запросы - чтение данных
        if ("GET".equals(method)) {
            if (path.matches("/(users|math-functions|points|simple-functions).*") ||
                    path.matches("/projectFor3Lab/(users|math-functions|points|simple-functions).*")) {
                logger.info("Пользователю " + user.getLogin() + " предоставлен GET доступ к: " + path);
                return true;
            }
        }

        // PUT запросы - обновление данных (только своих)
        if ("PUT".equals(method)) {
            if (path.matches("/users/\\d+/(password|login|factoryType|role)") ||
                    path.matches("/projectFor3Lab/users/\\d+/(password|login|factoryType|role)")) {
                logger.info("Пользователю " + user.getLogin() + " предоставлен PUT доступ к: " + path);
                return true;
            }
        }

        // DELETE запросы - удаление данных (только своих)
        if ("DELETE".equals(method)) {
            if (path.matches("/users/\\d+") ||
                    path.matches("/projectFor3Lab/users/\\d+")) {
                logger.info("Пользователю " + user.getLogin() + " предоставлен DELETE доступ к: " + path);
                return true;
            }
        }

        logger.warning("Пользователю " + user.getLogin() + " запрещен доступ для " + method + " " + path);
        return false;
    }

    public static boolean canAccessUserData(Users currentUser, Integer targetUserId, String targetLogin) {
        // ADMIN имеет доступ ко всем данным
        if ("admin".equals(currentUser.getRole())) {
            return true;
        }

        // USER имеет доступ только к своим данным
        if (targetUserId != null && targetUserId.equals(currentUser.getUserId())) {
            return true;
        }

        if (targetLogin != null && targetLogin.equals(currentUser.getLogin())) {
            return true;
        }

        logger.warning("Пользователю " + currentUser.getLogin() + " запрещен доступ к данным: userId=" + targetUserId + ", login=" + targetLogin);
        return false;
    }

    public static boolean canAccessUserDataByLogin(Users currentUser, String targetLogin) {
        // ADMIN имеет доступ ко всем данным
        if ("admin".equals(currentUser.getRole())) {
            return true;
        }

        if (targetLogin != null && targetLogin.equals(currentUser.getLogin())) {
            return true;
        }

        logger.warning("Пользователю " + currentUser.getLogin() + " запрещен доступ к данным: login=" + targetLogin);
        return false;
    }

    public static boolean canAccessUserDataById(Users currentUser, Integer targetUserId) {
        // ADMIN имеет доступ ко всем данным
        if ("admin".equals(currentUser.getRole())) {
            return true;
        }

        // USER имеет доступ только к своим данным
        if (targetUserId != null && targetUserId.equals(currentUser.getUserId())) {
            return true;
        }

        logger.warning("Пользователю " + currentUser.getLogin() + " запрещен доступ к данным: userId=" + targetUserId);
        return false;
    }
}