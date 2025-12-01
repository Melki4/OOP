# Базовый образ Tomcat
FROM tomcat:11.0

# Копируем наше приложение в директорию webapps Tomcat
COPY ./projectFor3Lab.war /usr/local/tomcat/webapps/

# Экспонируем необходимый порт для работы
EXPOSE 8080