public class Main {

    public static void main(String[] args) {
        Part[] inventory = new Part[3];

        inventory[0] = new Part(
                "P001",
                "Bajaj Piston",
                "Bajaj",
                4500,
                15,
                "Engine"
        );

        inventory[1] = new Part(
                "P002",
                "Brake Pad",
                "TVS",
                1250,
                8,
                "Brakes"
        );

        inventory[2] = new Part(
                "P003",
                "Spark Plug",
                "NGK",
                850,
                50,
                "Electrical"
        );
		
		int lowStockThreshold = 10;

		for (int i = 0; i < inventory.length; i++) {

			inventory[i].displayPart();

			if (inventory[i].isLowStock(lowStockThreshold)) {
				System.out.println("*** LOW STOCK ***");
				System.out.println();
			}
		}
    }
}