package Deserialization;

public class Dish {
    public String id;
    public String name;
    public String caloric;
    public String type;
    public String fat;
    public String carbon;
    public String protein;
    public String category_id;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCaloric() {
        return caloric;
    }

    public void setCaloric(String caloric) {
        this.caloric = caloric;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public String getCarbon() {
        return carbon;
    }

    public void setCarbon(String carbon) {
        this.carbon = carbon;
    }

    public String getProtein() {
        return protein;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }


    public Dish(){

    }

    public Dish(String id, String name, String caloric, String type, String fat, String carbon, String protein, String category_id) {
        this.id = id;
        this.name = name;
        this.caloric = caloric;
        this.type = type;
        this.fat = fat;
        this.carbon = carbon;
        this.protein = protein;
        this.category_id = category_id;
    }
}
