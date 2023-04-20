package dom.dec.carapp.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import dom.dec.carapp.domain.Car;
import dom.dec.carapp.domain.Color;
import dom.dec.carapp.dto.CarDto;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CarApiIntegrationTest {
    private static final String END_POINT_PATH = "/cars";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Flyway flyway;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getAllShouldReturnAllCars() throws Exception {
        MvcResult result = mockMvc.perform(get(END_POINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Car[] cars = objectMapper.readValue(result.getResponse().getContentAsString(), Car[].class);

        assertEquals(7, cars.length);
        assertEquals("Alfa Romeo", cars[0].getBrand());
    }

    @Test
    public void getAllByYearShouldReturnTwoCarsWhenFrom2014To2019() throws Exception {
        long from = 2014L;
        long to = 2019L;
        String URI = END_POINT_PATH + "/filter?from=" + from + "&to=" + to;

        MvcResult result = mockMvc.perform(get(URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Car[] cars = objectMapper.readValue(result.getResponse().getContentAsString(), Car[].class);

        assertEquals(2, cars.length);
        assertEquals("Volvo", cars[0].getBrand());
        assertEquals("Audi", cars[1].getBrand());
    }

    @Test
    public void getAllByYearShouldReturnNotFoundWhenCarsOutsideRange() throws Exception {
        long from = 2000L;
        long to = 2005L;
        String URI = END_POINT_PATH + "/filter?from=" + from + "&to=" + to;

        mockMvc.perform(get(URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllByYearShouldReturnBadRequestWhenInvalidParams() throws Exception {
        long from = 1899L;
        long to = -1L;
        String URI = END_POINT_PATH + "/filter?from=" + from + "&to=" + to;

        mockMvc.perform(get(URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addCarShouldReturnCreatedCar() throws Exception {
        CarDto carDto = new CarDto("Honda", "Civic", Color.BLACK, 2006);
        String requestBody = objectMapper.writeValueAsString(carDto);

        MvcResult result = mockMvc.perform(post(END_POINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn();

        Car car = objectMapper.readValue(result.getResponse().getContentAsString(), Car.class);

        assertEquals(8, car.getCarId());
        assertEquals("Honda", car.getBrand());
        assertEquals("Civic", car.getModel());
        assertEquals(Color.BLACK, car.getColor());
        assertEquals(2006, car.getProductionYear());
    }

    @Test
    public void addCarShouldReturnBadRequestWhenInvalidCarDto() throws Exception {
        CarDto carDto = new CarDto("", "", Color.BLACK, -1);
        String requestBody = objectMapper.writeValueAsString(carDto);

        mockMvc.perform(post(END_POINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateCarShouldUpdateCar() throws Exception {
        long id = 1L;
        String URI = END_POINT_PATH + "/" + id;
        CarDto carDto = new CarDto("Honda", "Accord", Color.BLACK, 2006);
        String requestBody = objectMapper.writeValueAsString(carDto);

        MvcResult result = mockMvc.perform(put(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        Car car = objectMapper.readValue(result.getResponse().getContentAsString(), Car.class);

        assertEquals(1, car.getCarId());
        assertEquals("Honda", car.getBrand());
        assertEquals("Accord", car.getModel());
        assertEquals(Color.BLACK, car.getColor());
        assertEquals(2006, car.getProductionYear());
    }

    @Test
    public void updateCarShouldReturnNotFoundWhenInvalidId() throws Exception {
        long id = 10L;
        String URI = END_POINT_PATH + "/" + id;
        CarDto carDto = new CarDto("Honda", "Accord", Color.BLACK, 2006);
        String requestBody = objectMapper.writeValueAsString(carDto);

        mockMvc.perform(put(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateCarShouldReturnBadRequestWhenInvalidInputs() throws Exception {
        long id = 1L;
        String URI = END_POINT_PATH + "/" + id;
        CarDto carDto = new CarDto("", "", Color.BLACK, -1);
        String requestBody = objectMapper.writeValueAsString(carDto);

        mockMvc.perform(put(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteCarShouldReturnNoContentWhenCarDeleted() throws Exception {
        long id = 1L;
        String URI = END_POINT_PATH + "/" + id;

        mockMvc.perform(delete(URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteCarShouldReturnNotFoundWhenInvalidId() throws Exception {
        long id = 10L;
        String URI = END_POINT_PATH + "/" + id;

        mockMvc.perform(delete(URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @AfterEach
    public void reset() {
        flyway.clean();
        flyway.migrate();
    }
}
