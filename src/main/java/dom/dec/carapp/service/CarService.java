package dom.dec.carapp.service;

import dom.dec.carapp.domain.Car;
import dom.dec.carapp.dto.CarDto;

import java.util.List;


public interface CarService {

    List<Car> findAll();

    Car saveCar(CarDto carDto);

    List<Car> findAllByYear(long from, long to);

    Car updateCar(CarDto newCar, long id);

    void deleteCar(long id);
}
