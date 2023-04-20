package dom.dec.carapp.dto;

import dom.dec.carapp.domain.Color;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CarDto {

    @NotNull(message = "Brand cannot be null")
    @Size(min = 2, message = "Brand cannot be less than 2 characters")
    private String brand;

    @NotNull(message = "Model cannot be null")
    @Size(min = 1, message = "Model cannot be less than 1 character")
    private String model;

    @NotNull(message = "Color cannot be null")
    private Color color;

    @Min(value = 1900, message = "Production year cannot be less than 1900")
    private long productionYear;

    public CarDto(String brand, String model, Color color, long productionYear) {
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.productionYear = productionYear;
    }

    public CarDto() {
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

}
