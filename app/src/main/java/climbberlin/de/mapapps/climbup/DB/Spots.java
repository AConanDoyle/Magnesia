package climbberlin.de.mapapps.climbup.DB;

/**
 * Created by Hagen on 13.03.2017.
 */

public class Spots {

    // Price einfügen !!!!!!!!!!!
    private int id;
    private String name;
    private Double lat;
    private Double Long;
    private String use;
    private String typ;
    private String krouten;
    private String brouten;
    private String material;
    private String opening;
    private String price;
    private String address;
    private String web;

    public Spots() {
    }

    // constructor
    public Spots (int id, String name, Double lat, Double Long, String use, String typ, String krouten, String brouten,
                  String material, String opening, String price, String address, String web) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.Long = Long;
        this.use = use;
        this.typ = typ;
        this.krouten = krouten;
        this.brouten = brouten;
        this.material = material;
        this.opening = opening;
        this.price = price;
        this.address = address;
        this.web = web;
    }

    // Setter methods
    public void setId(int id) {
        this.id = id;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLong(Double aLong) {
        Long = aLong;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public void setOpening(String opening) {
        this.opening = opening;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public void setKrouten(String krouten) {
        this.krouten = krouten;
    }

    public void setBrouten(String brouten) {
        this.brouten = brouten;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    // Getter methods
    public int getId() {
        return id;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLong() {
        return Long;
    }

    public String getUse() {
        return use;
    }

    public String getTyp() {
        return typ;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public String getKrouten() {
        return krouten;
    }

    public String getBrouten() {
        return brouten;
    }

    public String getMaterial() {
        return material;
    }

    public String getOpening() {
        return opening;
    }

    public String getPrice() {
        return price;
    }

    public String getWeb() {
        return web;
    }
}