package sunset.spring.webclient.external;

import sunset.spring.webclient.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ExternalUserApiController {

    public static final String GET_USER = "/external-api/users/{user_id}";
    public static final String GET_FIRST_SAME_NAME_USER = "/external-api/users";
    public static final String POST_ADD_USER = "/external-api/users";

    @GetMapping(GET_USER)
    public ResponseEntity<User> getUser(@PathVariable("user_id") int userId) {
        if (userId <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else if (userId >= 10000) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>(User.builder().id(userId).name("아무개").build(), HttpStatus.OK);
        }
    }

    @GetMapping(GET_FIRST_SAME_NAME_USER)
    public ResponseEntity<User> getFirstSameNameUser(@RequestParam(value = "name", required = false, defaultValue = "") String name) {
        if (name.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else if (name.length() > 3) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>(User.builder().id(1).name(name).build(), HttpStatus.OK);
        }
    }

    @PostMapping(POST_ADD_USER)
    public ResponseEntity<User> addUser(@RequestBody User user) {
        if (user.getId() <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else if (user.getId() >= 10000) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
    }
}
