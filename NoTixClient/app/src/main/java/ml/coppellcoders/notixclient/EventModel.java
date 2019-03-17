package ml.coppellcoders.notixclient;

import java.io.Serializable;

class EventModel implements Serializable {

    Long time;
    String img,name, venue, category, address;
    double price, quantity;

    public EventModel(Long time, String img, String name, String venue, String category, String address, double price, double quantity) {
        this.time = time;
        this.img = img;
        this.name = name;
        this.venue = venue;
        this.category = category;
        this.address = address;
        this.price = price;
        this.quantity = quantity;
    }

    public EventModel(){

    }

    public Long getDate() {
        return time;
    }

    public void setDate(Long date) {
        this.time = date;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
}
