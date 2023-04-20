package dom.dec.carapp.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long carId;
    private String brand;
    private String model;
    @Enumerated(value = EnumType.STRING)
    private Color color;
    private long productionYear;

    public Car(String brand, String model, Color color, long productionYear) {
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.productionYear = productionYear;
    }

    public Car() {
    }

    public long getCarId() {
        return carId;
    }

    public void setCarId(long carId) {
        this.carId = carId;
    }


    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }


    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }


    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public long getProductionYear() {
        return productionYear;
    }

    public void setProductionYear(long productionYear) {
        this.productionYear = productionYear;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Car car = (Car) o;

        return carId == car.carId;
    }

    @Override
    public int hashCode() {
        return (int) (carId ^ (carId >>> 32));
    }

    @Override
    public String toString() {
        return "Car{" +
                "carId=" + carId +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", color=" + color +
                ", productionYear=" + productionYear +
                '}';
    }
}
