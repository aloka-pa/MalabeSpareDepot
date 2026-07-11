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

        System.out.println("========= INVENTORY =========");
        System.out.println();

        for (int i = 0; i < inventory.length; i++) {

            System.out.println("Code: " + inventory[i].getCode());
            System.out.println("Name: " + inventory[i].getName());
            System.out.println("Brand: " + inventory[i].getBrand());
            System.out.println("Price: " + inventory[i].getPrice());
            System.out.println("Quantity: " + inventory[i].getQuantity());
            System.out.println("Category: " + inventory[i].getCategory());

            System.out.println("----------------------------");
            System.out.println();
        }

        System.out.println("Total Parts: " + inventory.length);
    }
}