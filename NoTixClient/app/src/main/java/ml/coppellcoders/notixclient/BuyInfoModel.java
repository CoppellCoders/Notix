package ml.coppellcoders.notixclient;

public class BuyInfoModel {
    Long time;
    String img,name, venue, category, address, faceimg, guestname;
    double price, quantity;
    int quant;

    public BuyInfoModel(Long time, String img, String name, String venue, String category, String address, String faceimg, String guestname, double price, double quantity, int quant) {
        this.time = time;
        this.img = img;
        this.name = name;
        this.venue = venue;
        this.category = category;
        this.address = address;
        this.faceimg = faceimg;
        this.guestname = guestname;
        this.price = price;
        this.quantity = quantity;
        this.quant = quant;
    }

    public Long getTime() {
        return time;
    }

    public String getImg() {
        return img;
    }

    public String getName() {
        return name;
    }

    public String getVenue() {
        return venue;
    }

    public String getCategory() {
        return category;
    }

    public String getAddress() {
        return address;
    }

    public String getFaceimg() {
        return faceimg;
    }

    public String getGuestname() {
        return guestname;
    }

    public double getPrice() {
        return price;
    }

    public double getQuantity() {
        return quantity;
    }

    public int getQuant() {
        return quant;
    }
}
