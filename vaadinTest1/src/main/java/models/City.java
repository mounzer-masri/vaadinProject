package models;

/**
 * Created by mounzer.masri on 10.9.2016.
 */
public class City {
    private int id;
    private String name;

    public City(int id, String name){
        this.setId(id);
        this.setName(name);
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
