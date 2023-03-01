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
