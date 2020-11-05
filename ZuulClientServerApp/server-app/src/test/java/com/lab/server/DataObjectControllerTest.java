package com.lab.server;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = DataObjectController.class)
public class DataObjectControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test()
    public void testServerDataWithManyId() throws Exception {
        for (long l = 0; l < 5; l++) {
            MvcResult result = mvc.perform(get("/data/{id}", l)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()).andReturn();
            String content = result.getResponse().getContentAsString();
            assertTrue(content.contains("\"id\":" + l));
            assertTrue(content.contains("\"name\":"));
        }
    }


}