package dom.dec.carapp.service;

import dom.dec.carapp.domain.Car;
import dom.dec.carapp.domain.Color;
import dom.dec.carapp.dto.CarDto;
import dom.dec.carapp.exception.ResourceNotFoundException;
import dom.dec.carapp.repository.CarRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class CarServiceImplTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarService carService = new CarServiceImpl(carRepository);


    @Test
    void shouldReturnAllCars() {
        //given
        Car car1 = new Car("Audi", "A4", Color.BLACK, 2020);
        car1.setCarId(1L);
        Car car2 = new Car("Fiat", "Punto", Color.RED, 2016);
        car2.setCarId(2L);
        List<Car> cars = Arrays.asList(car1, car2);

        //when
        when(carRepository.findAll()).thenReturn(cars);

        //then
        List<Car> actualCars = carService.findAll();

        assertEquals(cars, actualCars);
    }

    @Test
    public void shouldReturnEmptyListWhenNoCars() {
        // when
        when(carRepository.findAll()).thenReturn(new ArrayList<>());

        // then
        List<Car> actualCars = carService.findAll();

        assertTrue(actualCars.isEmpty());
    }

    @Test
    public void shouldReturnCreatedCar() {
        // given
        CarDto carDto = new CarDto("Fiat", "Punto", Color.RED, 2016);
        Car expectedCar = new Car("Fiat", "Punto", Color.RED, 2016);

        // when
        when(carRepository.save(any())).thenReturn(expectedCar);

        // then
        Car actualCar = carService.saveCar(carDto);

        verify(carRepository, times(1)).save(any());
        assertEquals(expectedCar.getBrand(), actualCar.getBrand());
        assertEquals(expectedCar.getModel(), actualCar.getModel());
        assertEquals(expectedCar.getColor(), actualCar.getColor());
        assertEquals(expectedCar.getProductionYear(), actualCar.getProductionYear());
    }

    @Test
    public void shouldReturnOneCarInTheGivenRange() {
        //given
        Car car1 = new Car("Audi", "A4", Color.BLACK, 2020);
        car1.setCarId(1L);
        Car car2 = new Car("Fiat", "Punto", Color.RED, 2016);
        car2.setCarId(2L);
        List<Car> cars = Arrays.asList(car1, car2);

        // when
        when(carRepository.findAll()).thenReturn(cars);

        // then
        List<Car> actualCars = carService.findAllByYear(2011, 2017);

        verify(carRepository, times(1)).findAll();
        assertEquals(1, actualCars.size());
    }

    @Test
    public void shouldReturnOneCarWhenRangeReversed() {
        //given
        Car car1 = new Car("Audi", "A4", Color.BLACK, 2020);
        car1.setCarId(1L);
        Car car2 = new Car("Fiat", "Punto", Color.RED, 2016);
        car2.setCarId(2L);
        List<Car> cars = Arrays.asList(car1, car2);

        // when
        when(carRepository.findAll()).thenReturn(cars);

        // then
        List<Car> actualCars = carService.findAllByYear(2017, 2011);

        verify(carRepository, times(1)).findAll();
        assertEquals(1, actualCars.size());
    }

    @Test
    public void shouldReturnOneCarWhenMinAndMaxTheSame() {
        //given
        Car car1 = new Car("Audi", "A4", Color.BLACK, 2020);
        car1.setCarId(1L);
        Car car2 = new Car("Fiat", "Punto", Color.RED, 2016);
        car2.setCarId(2L);
        List<Car> cars = Arrays.asList(car1, car2);

        // when
        when(carRepository.findAll()).thenReturn(cars);

        // then
        List<Car> actualCars = carService.findAllByYear(2016, 2016);

        verify(carRepository, times(1)).findAll();
        assertEquals(1, actualCars.size());
    }

    @Test
    public void shouldReturnEmptyListWhenNoCarsInRange() {
        //given
        Car car1 = new Car("Audi", "A4", Color.BLACK, 2020);
        car1.setCarId(1L);
        Car car2 = new Car("Fiat", "Punto", Color.RED, 2016);
        car2.setCarId(2L);
        List<Car> cars = Arrays.asList(car1, car2);

        // when
        when(carRepository.findAll()).thenReturn(cars);

        // then
        List<Car> actualCars = carService.findAllByYear(2010, 2015);

        verify(carRepository, times(1)).findAll();
        assertTrue(actualCars.isEmpty());
    }

    @Test
    public void shouldUpdateTheCar() {
        // given
        CarDto newCar = new CarDto("Brand2", "Model2", Color.BLACK, 2022);
        Car oldCar = new Car("Brand1", "Model1", Color.GREEN, 2020);

        // when
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(oldCar));
        when(carRepository.save(oldCar)).thenReturn(oldCar);

        // then
        Car actualCar = carService.updateCar(newCar, 1L);

        verify(carRepository, times(1)).findById(anyLong());
        verify(carRepository, times(1)).save(oldCar);
        assertEquals(newCar.getBrand(), actualCar.getBrand());
        assertEquals(newCar.getModel(), actualCar.getModel());
        assertEquals(newCar.getColor(), actualCar.getColor());
        assertEquals(newCar.getProductionYear(), actualCar.getProductionYear());
    }

    @Test
    public void shouldThrowExceptionWhenNoCarToUpdate() {
        // given
        CarDto carDto = new CarDto("Fiat", "Punto", Color.RED, 2016);

        // when
        when(carRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> carService.updateCar(carDto, anyLong()));
    }

    @Test
    public void shouldDeleteCar() {
        // given
        Car car = new Car("Audi", "A4", Color.BLACK, 2020);
        car.setCarId(1L);

        // when
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        carService.deleteCar(1L);

        verify(carRepository, times(1)).delete(car);
    }

    @Test
    public void shouldThrowExceptionWhenNoCarToDelete() {
        // when
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> carService.deleteCar(1L));
    }
}