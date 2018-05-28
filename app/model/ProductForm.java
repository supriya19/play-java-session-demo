package model;

import play.data.validation.Constraints;

public class ProductForm  implements Constraints.Validatable<String> {

    @Constraints.Required(message = "Invalid title")
    private String title;
    private String description;
    @Constraints.Required(message = "Invalid price")
    private Double price;
    private String image;

    @Override
    public String validate() {
        return null;
    }

    public ProductForm() {
    }

    public ProductForm(String title, String description, Double price, String image) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
