package io.youngkoss.app.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StreamUtils;

import io.youngkoss.app.controller.TimetableCreationController;

@SuppressWarnings("nls")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MeetingRoomApplicationTest {

   @LocalServerPort
   private int port;

   @Value("classpath:input/*.test")
   private Resource[] allTestInputData;

   @Value("classpath:output/*.output")
   private Resource[] allTestOutputData;

   @Autowired
   private TimetableCreationController controller;

   @Autowired
   private TestRestTemplate restTemplate;

   @Test
   public void contexLoads() throws Exception {
      assertThat(controller).isNotNull();
   }

   @Test
   public void testPingPong() {
      assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/ping", String.class)).contains("pong");
   }

   @Test
   public void testFromRahul() throws IOException {
      final String request = StreamUtils.copyToString(allTestInputData[0].getInputStream(), Charset.defaultCharset());
      final String response = StreamUtils.copyToString(allTestOutputData[0].getInputStream(), Charset.defaultCharset());
      assertThat(this.restTemplate.postForObject("http://localhost:" + port + "/timetable-creation", request, String.class)).contains(response);
   }

   @Test
   public void testFromRahulModified() throws IOException {
      final String request = StreamUtils.copyToString(allTestInputData[1].getInputStream(), Charset.defaultCharset());
      final String response = StreamUtils.copyToString(allTestOutputData[1].getInputStream(), Charset.defaultCharset());
      assertThat(this.restTemplate.postForObject("http://localhost:" + port + "/timetable-creation", request, String.class)).contains(response);
   }
}
