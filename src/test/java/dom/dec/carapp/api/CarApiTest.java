package dom.dec.carapp.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import dom.dec.carapp.domain.Car;
import dom.dec.carapp.domain.Color;
import dom.dec.carapp.dto.CarDto;
import dom.dec.carapp.exception.ResourceNotFoundException;
import dom.dec.carapp.service.CarService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(CarApi.class)
class CarApiTest {
    private static final String END_POINT_PATH = "/cars";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CarService service;

    @Test
    public void addCarShouldReturnBadRequestWhenInvalidCarDto() throws Exception {
        CarDto carDto = new CarDto("", "", null, -1);

        String requestBody = objectMapper.writeValueAsString(carDto);

        mockMvc.perform(post(END_POINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verify(service, times(0)).saveCar(any(CarDto.class));
    }

    @Test
    public void addCarShouldCreateCarWhenValidCarDto() throws Exception {
        CarDto carDto = new CarDto("Audi", "A4", Color.GREEN, 2020);
        Car car = new Car("Audi", "A4", Color.GREEN, 2020);
        car.setCarId(1L);

        when(service.saveCar(any(CarDto.class))).thenReturn(car);

        String requestBody = objectMapper.writeValueAsString(carDto);

        mockMvc.perform(post(END_POINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/cars/1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId", is(1)))
                .andExpect(jsonPath("$.brand", is("Audi")))
                .andExpect(jsonPath("$.model", is("A4")))
                .andExpect(jsonPath("$.color", is("GREEN")))
                .andExpect(jsonPath("$.productionYear", is(2020)))
                .andDo(print());

        verify(service, times(1)).saveCar(any(CarDto.class));
    }

    @Test
    public void getAllByYearShouldReturnNotFoundWhenNoCars() throws Exception {
        long from = 2015L;
        long to = 2020L;
        String URI = END_POINT_PATH + "/filter?from=" + from + "&to=" + to;

        when(service.findAllByYear(from, to)).thenReturn(new ArrayList<>());

        mockMvc.perform(get(URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());

        verify(service, times(1)).findAllByYear(from, to);
    }

    @Test
    public void getAllByYearShouldReturnNotFoundWhenInvalidFilterParams() throws Exception {
        long from = 1899;
        long to = -1;
        String URI = END_POINT_PATH + "/filter?from=" + from + "&to=" + to;

        when(service.findAllByYear(from, to)).thenReturn(new ArrayList<>());

        mockMvc.perform(get(URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verify(service, times(0)).findAllByYear(from, to);
    }

    @Test
    public void getAllByYearShouldReturnOkWhenValidFilterParams() throws Exception {
        long from = 2015;
        long to = 2020;
        String URI = END_POINT_PATH + "/filter?from=" + from + "&to=" + to;

        when(service.findAllByYear(from, to)).thenReturn(Arrays.asList(new Car()));

        mockMvc.perform(get(URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        verify(service, times(1)).findAllByYear(from, to);
    }

    @Test
    public void getAllShouldReturnNoContentWhenNoCars() throws Exception {
        when(service.findAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get(END_POINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(service, times(1)).findAll();
    }

    @Test
    public void getAllShouldReturnOkWhenFindCars() throws Exception {
        when(service.findAll()).thenReturn(Arrays.asList(new Car(), new Car()));

        mockMvc.perform(get(END_POINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        verify(service, times(1)).findAll();
    }

    @Test
    public void updateShouldReturnNotFoundWhenCarNotFound() throws Exception {
        long id = 10L;
        String URI = END_POINT_PATH + "/" + id;
        CarDto carDto = new CarDto("Audi", "A4", Color.GREEN, 2020);

        String requestBody = objectMapper.writeValueAsString(carDto);

        when(service.updateCar(any(CarDto.class), anyLong())).thenThrow(new ResourceNotFoundException("Car", "id", id));

        mockMvc.perform(put(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andDo(print());

        verify(service, times(1)).updateCar(any(CarDto.class), anyLong());
    }

    @Test
    public void updateShouldReturnBadRequestWhenInvalidCarDto() throws Exception {
        long id = 10L;
        String URI = END_POINT_PATH + "/" + id;
        CarDto carDto = new CarDto("", "", null, -1);

        String requestBody = objectMapper.writeValueAsString(carDto);

        mockMvc.perform(put(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verify(service, times(0)).updateCar(any(CarDto.class), anyLong());
    }

    @Test
    public void updateShouldReturnUpdatedCarWhenValidCarDto() throws Exception {
        long id = 10;
        String URI = END_POINT_PATH + "/" + id;
        CarDto carDto = new CarDto("BMW", "330i", Color.BLACK, 2020);
        Car car = new Car("BMW", "330i", Color.BLACK, 2020);
        car.setCarId(id);

        System.out.println(car);
        when(service.updateCar(any(CarDto.class), anyLong())).thenReturn(car);

        String requestBody = objectMapper.writeValueAsString(carDto);

        mockMvc.perform(put(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.carId", is(10)))
                .andExpect(jsonPath("$.brand", is("BMW")))
                .andExpect(jsonPath("$.model", is("330i")))
                .andExpect(jsonPath("$.color", is("BLACK")))
                .andExpect(jsonPath("$.productionYear", is(2020)))
                .andDo(print());

        verify(service, times(1)).updateCar(any(CarDto.class), anyLong());
    }

    @Test
    public void deleteShouldReturnNotFoundWhenNoCar() throws Exception {
        long id = 10;
        String URI = END_POINT_PATH + "/" + id;

        doThrow(new ResourceNotFoundException("Car", "id", id)).when(service).deleteCar(id);

        mockMvc.perform(delete(URI))
                .andExpect(status().isNotFound())
                .andDo(print());

        verify(service, times(1)).deleteCar(id);
    }

    @Test
    public void deleteShouldReturnNoContentWhenCarDeleted() throws Exception {
        long id = 10;
        String URI = END_POINT_PATH + "/" + id;

        doNothing().when(service).deleteCar(id);

        mockMvc.perform(delete(URI))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(service, times(1)).deleteCar(id);
    }
}
























