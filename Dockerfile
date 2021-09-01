FROM openjdk:11.0.8-slim as build

RUN mkdir -p /tmp
WORKDIR /tmp

# Переместим исходники приложения в образ
COPY ./ /tmp

# Соберем приложение
RUN chmod +x ./gradlew
RUN ./gradlew --no-daemon bootJar


FROM openjdk:11.0.8-jre-slim as final

RUN mkdir -p /tmp
WORKDIR /tmp

# Перенесем в этот образ jar файл
COPY --from=build /tmp/build/libs/berth-booking-3.0.jar /tmp/berth-booking-3.0.jar

# Запустим jar файл
CMD java -jar berth-booking-3.0.jar