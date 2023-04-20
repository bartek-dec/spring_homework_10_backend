package dom.dec.carapp.api;

import dom.dec.carapp.domain.Car;
import dom.dec.carapp.dto.CarDto;
import dom.dec.carapp.service.CarService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/cars")
@CrossOrigin
@Validated
public class CarApi {
    private CarService carService;

    @Autowired
    public CarApi(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public ResponseEntity<List<Car>> getAll() {
        List<Car> foundCars = carService.findAll();

        if (foundCars.size() == 0) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(foundCars);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Car>> getAllByYear(@RequestParam(name = "from") @Min(value = 1900, message = "From param cannot be less than 1900") long from,
                                                  @RequestParam(name = "to") @Min(value = 1900, message = "To param cannot be less than 1900") long to) {
        List<Car> foundCars = carService.findAllByYear(from, to);

        if (foundCars.size() == 0) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(foundCars);
    }

    @PostMapping
    public ResponseEntity<Car> addCar(@Valid @RequestBody CarDto carDto) {
        Car car = carService.saveCar(carDto);
        URI uri = URI.create("/cars/" + car.getCarId());
        return ResponseEntity.created(uri).body(car);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Car> updateCar(@Valid @RequestBody CarDto carDto, @PathVariable("id") long id) {
        Car car = carService.updateCar(carDto, id);
        return ResponseEntity.ok(car);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Car> deleteCar(@PathVariable("id") long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }
}
