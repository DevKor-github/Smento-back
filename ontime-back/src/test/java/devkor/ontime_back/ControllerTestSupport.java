package devkor.ontime_back;

import com.fasterxml.jackson.databind.ObjectMapper;
import devkor.ontime_back.controller.UserAuthController;
import devkor.ontime_back.repository.UserRepository;
import devkor.ontime_back.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
        controllers = UserAuthController.class
)
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected UserAuthService userAuthService;

    @MockBean
    protected UserRepository userRepository;

}
