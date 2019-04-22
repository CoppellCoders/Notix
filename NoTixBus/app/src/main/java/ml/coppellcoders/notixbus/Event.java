package ml.coppellcoders.notixbus;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import ml.coppellcoders.notixbus.blockchain.Block;

public class Event implements Serializable{
    String address;
    String category;
    String img;
    String name;
    long price;
    long quantity;
    long time;
    String venue;
    HashMap<String,Block> tickets;

    public Event(){

    }

    public Event(String address, String category, String img, String name, long price, long quantity, long time, String venue, HashMap<String,Block> tickets){
        this.address = address;
        this.category = category;
        this.img = img;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.time = time;
        this.venue = venue;
        this.tickets = tickets;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }
}
