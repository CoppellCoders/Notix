package ml.coppellcoders.notixclient;

public class BuyInfoModel {
    Long time;
    String img,name, venue, category, address;
    double price, quantity;
    int quant;

    public BuyInfoModel(Long time, String img, String name, String venue, String category, String address, double price, double quantity, int quant) {
        this.time = time;
        this.img = img;
        this.name = name;
        this.venue = venue;
        this.category = category;
        this.address = address;
        this.price = price;
        this.quantity = quantity;
        this.quant = quant;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
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

    public int getQuant() {
        return quant;
    }

    public void setQuant(int quant) {
        this.quant = quant;
    }
}
