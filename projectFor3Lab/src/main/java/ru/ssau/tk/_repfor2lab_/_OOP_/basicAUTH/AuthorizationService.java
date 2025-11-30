package ru.ssau.tk._repfor2lab_._OOP_.basicAUTH;

import ru.ssau.tk._repfor2lab_._OOP_.databaseEnteties.Users;

import java.util.logging.Logger;

public class AuthorizationService {
    private static final Logger logger = Logger.getLogger(AuthorizationService.class.getName());

    public static boolean hasAccess(Users user, String method, String path) {
        String userRole = user.getRole();

        // ADMIN - полный доступ
        if ("admin".equals(userRole)) {
            logger.info("Admin " + user.getLogin() + " access granted for " + method + " " + path);
            return true;
        }

        // USER - ограниченный доступ
        if ("user".equals(userRole)) {
            return checkUserAccess(user, method, path);
        }

        logger.warning("Access denied for role " + userRole + " to " + method + " " + path);
        return false;
    }

    public static boolean hasAdminAccess(Users user, String method, String path) {
        String userRole = user.getRole();

        // ADMIN - полный доступ
        if ("admin".equals(userRole)) {
            logger.info("Admin " + user.getLogin() + " access granted for " + method + " " + path);
            return true;
        }

        logger.warning("Access denied for role " + userRole + " to " + method + " " + path);
        return false;
    }

    private static boolean checkUserAccess(Users user, String method, String path) {
        // GET запросы - чтение данных
        if ("GET".equals(method)) {
            if (path.matches("/(users|math-functions|points|simple-functions).*") ||
                    path.matches("/projectFor3Lab/(users|math-functions|points|simple-functions).*")) {
                logger.info("User " + user.getLogin() + " GET access granted for: " + path);
                return true;
            }
        }

        // PUT запросы - обновление данных (только своих)
        if ("PUT".equals(method)) {
            if (path.matches("/users/\\d+/(password|login|factoryType|role)") ||
                    path.matches("/projectFor3Lab/users/\\d+/(password|login|factoryType|role)")) {
                logger.info("User " + user.getLogin() + " PUT access granted for: " + path);
                return true;
            }
        }

        // DELETE запросы - удаление данных (только своих)
        if ("DELETE".equals(method)) {
            if (path.matches("/users/\\d+") ||
                    path.matches("/projectFor3Lab/users/\\d+")) {
                logger.info("User " + user.getLogin() + " DELETE access granted for: " + path);
                return true;
            }
        }

        logger.warning("User " + user.getLogin() + " access denied for " + method + " " + path);
        return false;
    }

    public static boolean canAccessUserData(Users currentUser, Integer targetUserId, String targetLogin) {
        // ADMIN может доступ ко всем данным
        if ("admin".equals(currentUser.getRole())) {
            return true;
        }

        // USER может доступ только к своим данным
        if (targetUserId != null && targetUserId.equals(currentUser.getUserId())) {
            return true;
        }

        if (targetLogin != null && targetLogin.equals(currentUser.getLogin())) {
            return true;
        }

        logger.warning("User " + currentUser.getLogin() + " access denied to data: userId=" + targetUserId + ", login=" + targetLogin);
        return false;
    }

    public static boolean canAccessUserDataByLogin(Users currentUser, String targetLogin) {
        // ADMIN может доступ ко всем данным
        if ("admin".equals(currentUser.getRole())) {
            return true;
        }

        if (targetLogin != null && targetLogin.equals(currentUser.getLogin())) {
            return true;
        }

        logger.warning("User " + currentUser.getLogin() + " access denied to data: login=" + targetLogin);
        return false;
    }

    public static boolean canAccessUserDataById(Users currentUser, Integer targetUserId) {
        // ADMIN может доступ ко всем данным
        if ("admin".equals(currentUser.getRole())) {
            return true;
        }

        // USER может доступ только к своим данным
        if (targetUserId != null && targetUserId.equals(currentUser.getUserId())) {
            return true;
        }

        logger.warning("User " + currentUser.getLogin() + " access denied to data: userId=" + targetUserId);
        return false;
    }
}