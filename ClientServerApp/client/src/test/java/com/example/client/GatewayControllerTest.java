package com.example.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = GatewayController.class)
public class GatewayControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private RestTemplate rt;

    @Test
    public void testDataMethod() throws Exception {
        mvc.perform(get("/gateway/data")
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello from gateway"));
    }

    @Test()
    public void testServerDataWithNoAnswer() throws Exception {
        Mockito.doThrow(new RestClientException("Error"))
                .when(rt).getForObject(new URI("https://localhost:8765/server/data"), String.class);
        mvc.perform(get("/gateway/server-data")
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("Exception occurred"));
    }

    @Test()
    public void testServerDataWithAnswer() throws Exception {
        Mockito.doReturn("Hello ^^")
                .when(rt).getForObject(new URI("https://localhost:8765/server/data"), String.class);
        mvc.perform(get("/gateway/server-data")
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello ^^"));
    }

}