package com.alan10607.ag;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ForumControllerTest {
    @Autowired
    private MockMvc mockMvc;

//
//    @InjectMocks
//    private YourController yourController;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }

    @Test
    public void testYourController() throws Exception {
        // Mock yourService method
//        when(yourService.yourMethod()).thenReturn("Mocked response");

        // Build the request
        RequestBuilder request = MockMvcRequestBuilders.get("/forum/id");

        // Perform the request
        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        // Verify the response
        String response = result.getResponse().getContentAsString();
        // Add your assertions here
    }
}