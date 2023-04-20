package dom.dec.carapp.service;

import dom.dec.carapp.domain.Car;
import dom.dec.carapp.dto.CarDto;
import dom.dec.carapp.exception.ResourceNotFoundException;
import dom.dec.carapp.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarServiceImpl implements CarService {
    private CarRepository repository;

    @Autowired
    public CarServiceImpl(CarRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Car> findAll() {
        return repository.findAll();
    }

    @Override
    public Car saveCar(CarDto carDto) {
        Car car = new Car(carDto.getBrand(), carDto.getModel(), carDto.getColor(), carDto.getProductionYear());
        return repository.save(car);
    }

    @Override
    public List<Car> findAllByYear(long from, long to) {
        long min = Math.min(from, to);
        long max = Math.max(from, to);

        return repository.findAll()
                .stream()
                .filter(item -> item.getProductionYear() >= min)
                .filter(item -> item.getProductionYear() <= max)
                .collect(Collectors.toList());
    }

    @Override
    public Car updateCar(CarDto newCar, long id) {
        Car car = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Car", "id", id));

        car.setBrand(newCar.getBrand());
        car.setModel(newCar.getModel());
        car.setColor(newCar.getColor());
        car.setProductionYear(newCar.getProductionYear());

        return repository.save(car);
    }

    @Override
    public void deleteCar(long id) {
        Car car = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Car", "id", id));
        repository.delete(car);
    }
}
