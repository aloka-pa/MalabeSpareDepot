public class Main {

    public static void main(String[] args) {
		Part piston = new Part(
        "P001",
        "Bajaj Piston",
        "Bajaj",
        4500,
        15,
        "Engine");
		
		System.out.println("Code: " + piston.getCode());
System.out.println("Name: " + piston.getName());
System.out.println("Brand: " + piston.getBrand());
System.out.println("Price: " + piston.getPrice());
System.out.println("Quantity: " + piston.getQuantity());
System.out.println("Category: " + piston.getCategory());
    }

}