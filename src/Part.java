public class Part {

    private String code;
    private String name;
    private String brand;
    private double price;
    private int quantity;
    private String category;

    public Part(String code,
                String name,
                String brand,
                double price,
                int quantity,
                String category) {

        this.code = code;
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getCategory() {
        return category;
    }
	
	public boolean isLowStock(int threshold) {
    return quantity < threshold;
	}
	
	public void displayPart() {

    System.out.println("Code: " + code);
    System.out.println("Name: " + name);
    System.out.println("Brand: " + brand);
    System.out.println("Price: " + price);
    System.out.println("Quantity: " + quantity);
    System.out.println("Category: " + category);
    System.out.println("----------------------------");
    System.out.println();
	}
	
	
}