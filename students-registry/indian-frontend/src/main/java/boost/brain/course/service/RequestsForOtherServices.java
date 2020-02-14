package boost.brain.course.service;

import boost.brain.course.Constants;
import boost.brain.course.common.auth.Credentials;
import boost.brain.course.common.auth.Session;
import boost.brain.course.common.users.UserDto;
import boost.brain.course.dto.UserDtoWithNormalDate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RequestsForOtherServices {

    /**
     * Получает список всех user из сервиса User-service
     *
     * @return список пользователей с преобразованными в нормальный вид полями -датами.
     */
    public static List<UserDtoWithNormalDate> getUserDtoList() {
        return Objects.requireNonNull(
                new RestTemplateBuilder()
                        .additionalMessageConverters(new MappingJackson2HttpMessageConverter())
                        .build()
                        .exchange(RequestEntity
                                        .get(URI.create(Constants.USER_SERVER + "api/users/users-all"))
                                        .build(),
                                new ParameterizedTypeReference<List<UserDto>>() {
                                }).getBody())
                .stream()
                .map(ClassConverterService::getUserDtoToUserDtoWithNormalDate)
                .collect(Collectors.toList());
    }

    public static ResponseEntity<Session> getSessionResponseEntityWhenLogin(Credentials credentials) {
        HttpEntity<Credentials> request = new HttpEntity<>(credentials);
        return new RestTemplateBuilder()
                .additionalMessageConverters(new MappingJackson2HttpMessageConverter())
                .build()
                .postForEntity(Constants.AUTH_SERVER + "api/login/login",
                        request, Session.class);
    }
}
