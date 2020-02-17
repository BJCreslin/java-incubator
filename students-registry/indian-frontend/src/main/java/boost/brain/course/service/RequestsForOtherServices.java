package boost.brain.course.service;

import boost.brain.course.Constants;
import boost.brain.course.common.auth.Credentials;
import boost.brain.course.common.auth.Session;
import boost.brain.course.common.projects.ProjectDto;
import boost.brain.course.common.register.UserRegDto;
import boost.brain.course.common.users.UserDto;
import boost.brain.course.dto.UserDtoWithNormalDate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RequestsForOtherServices {

    /**
     * Получает список всех user из сервиса User-service
     *
     * @param session
     * @return список пользователей с преобразованными в нормальный вид полями -датами.
     */
    public static List<UserDtoWithNormalDate> getUserDtoList(Session session) {
        return Objects.requireNonNull(
                new RestTemplateBuilder()
                        .additionalMessageConverters(new MappingJackson2HttpMessageConverter())
                        .build()
                        .exchange(RequestEntity
                                        .get(URI.create(Constants.USER_SERVER + "api/users/users-all/"))
                                        .build(),
                                new ParameterizedTypeReference<List<UserDto>>() {
                                }).getBody())
                .stream()
                .map(ClassConverterService::getUserDtoToUserDtoWithNormalDate)
                .collect(Collectors.toList());
    }


    /**
     * Получает ResponseEntity<Session> из api метода login сервиса Auth-service
     *
     * @param credentials Объект : email , пароль.
     * @return ResponseEntity<Session>
     */
    public static ResponseEntity<Session> getSessionResponseEntityWhenLogin(Credentials credentials) {
        HttpEntity<Credentials> request = new HttpEntity<>(credentials);
        return new RestTemplateBuilder()
                .additionalMessageConverters(new MappingJackson2HttpMessageConverter())
                .build()
                .postForEntity(Constants.AUTH_SERVER + "api/login/login/",
                        request, Session.class);
    }

    /**
     * Создание нового пользователя в сервиссе User
     *
     * @param userRegDto данные рользователя
     * @return ResponseEntity<Session>
     */
    public static ResponseEntity<UserDto> registrationInTheServiceUser(UserRegDto userRegDto) {
        HttpEntity<UserRegDto> request = new HttpEntity<>(userRegDto);
        return new RestTemplateBuilder()
                .additionalMessageConverters(new MappingJackson2HttpMessageConverter())
                .build()
                .postForEntity(Constants.USER_SERVER + "api/users/create/",
                        request, UserDto.class);
    }

    /**
     * Создание нового пользователя в сервиссе Auth
     *
     * @param userRegDto данные рользователя
     * @return ResponseEntity<Session>
     */
    public static ResponseEntity<Boolean> registrationInTheServiceAuth(UserRegDto userRegDto) {
        HttpEntity<Credentials> request = new HttpEntity<>(ClassConverterService.getCredentialsFromUserReg(userRegDto));
        return new RestTemplateBuilder()
                .additionalMessageConverters(new MappingJackson2HttpMessageConverter())
                .build()
                .postForEntity(Constants.AUTH_SERVER + "api/credentials/create/",
                        request, Boolean.class);
    }

    public static List<ProjectDto> getAllUserDtoList(Session session) {
        try {
            return Objects.requireNonNull(
                    new RestTemplateBuilder()
                            .additionalMessageConverters(new MappingJackson2HttpMessageConverter())
                            .build()
                            .exchange(RequestEntity
                                            .get(URI.create(Constants.USER_SERVER +
                                                    "/projects-all"))
                                            .header(Constants.SECURE_HEADER, session.getSessionId())
                                            .build(),
                                    new ParameterizedTypeReference<List<ProjectDto>>() {
                                    }).getBody());
        } catch (RestClientException | NullPointerException e) {
            return Collections.EMPTY_LIST;
        }
    }

}
