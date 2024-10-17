package ui;

import java.util.Scanner;
import controller.Controller;
import exceptions.*;
import model.Plantation;
import model.Season;
import model.Chest;

public class Main {

    // Atributos
    private Scanner reader;

    // Relaciones
    private Controller controller;

    public static void main(String[] args) throws InexistentChestException, DuplicatedChestException {
        Main exe = new Main();
        exe.menu();
    }

    public Main() {
        reader = new Scanner(System.in);
        controller = new Controller();
    }

    public void menu() throws InexistentChestException, DuplicatedChestException {
        boolean flag = true;

        while (flag) {
            System.out.println("Welcome to Stardew Valley");
            System.out.println("What would you like to do?");
            System.out.println("[1] Plant");
            System.out.println("[2] Harvest Plantation");
            System.out.println("[3] Craft a chest");
            System.out.println("[4] Save an item from your inventory to a chest");
            System.out.println("[5] Take an item from a chest to your inventory");
            System.out.println("[6] Organize chest items");
            System.out.println("[8] Exit Game");

            int option = reader.nextInt();
            reader.nextLine(); // Clear buffer

            switch (option) {
                case 1:
                    createPlantation();
                    break;
                case 2:
                    harvestPlantation();
                    break;
                case 3:
                    createNewChest();
                    break;
                case 4:
                    savePlantationInChest();
                    break;
                case 5:
                    takeItemFromChest();
                    break;
                case 6:
                    organizeChest();
                    break;
                case 7:
                    flag = false;
                    controller.saveAllDataToJson();
                    System.out.println("Thank you for playing Stardew Valley!");
                    break;
                default:
                    System.out.println("Invalid option, please try again.");
                    break;
            }
        }
    }

    public void createPlantation() {
        System.out.println("What Plantation do you want to create?");
        System.out.println(controller.listAvaliblePlantations());
        System.out.println("The actual season is " + controller.showCurrentSeason());
        String plantationName = reader.nextLine();
        try {
            if (controller.createPlantationByName(plantationName)) {
                System.out.println("Plantation created successfully.");
            }
        } catch (IncorrectSeasonException e) {
            System.out.println(e.getMessage());
        }
    }

    public void harvestPlantation() {
        System.out.println("Available plantations to harvest:");
        System.out.println(controller.listCropFieldReadyPlantations());

        System.out.println("Enter the name of the plantation you want to harvest:");
        String plantationName = reader.nextLine();

        try {
            if (controller.movePlantationFromCropFieldToInventory(plantationName)) {
                System.out.println("Plantation successfully harvested and stored in your inventory.");
            } else {
                System.out.println("The specified plantation could not be found in the crop field.");
            }
        } catch (InventoryFullException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createNewChest() throws DuplicatedChestException, InexistentChestException {
        System.out.println("Ingrese el nombre del nuevo cofre:");
        String chestName = reader.nextLine();
        try{
            controller.createChest(chestName);
        } catch ( InexistentChestException e) {
            System.out.println ("Error" + e.getMessage());
        }

        System.out.println("¿do you want to classify the chest? [1] Yes [2] No");
        int classifyOption = reader.nextInt();
        reader.nextLine();

        if (classifyOption == 1) {
            classifyNewChest(chestName);
        }
        System.out.println("Chest  created succesfully.");
    }

    public void savePlantationInChest() throws InexistentChestException {
        System.out.println("Type the chest name:");
        System.out.println(controller.listChests());
        String chestName = reader.nextLine();
        Chest chest = null;

        try {
            chest = controller.searchChest(chestName);  // Buscar el cofre
        } catch (InexistentChestException e) {
            System.out.println("Error: " + e.getMessage());  // Imprimir mensaje de error si no se encuentra el cofre
            return;  // Salir si el cofre no existe
        }

        if (chest != null) {
            System.out.println("Type the plantation name:");
            System.out.println(controller.listInventoryPlantations());
            String plantationName = reader.nextLine();

            try {
                controller.movePlantationToChest(chestName, plantationName);  // Mover la plantación al cofre
                System.out.println("The plantation is successfully saved in the chest.");
            } catch (InexistentChestException | InventoryEmptyException | ChestFullException | InexistentPlantationException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("Inexistent chest");
        }
    }

    public void takeItemFromChest() throws InexistentChestException {
        System.out.println("Enter the chest name where you are going to take an item:");
        System.out.println(controller.listChests());
        String chestName = reader.nextLine();
        Chest chest = controller.searchChestByName(chestName);

        if (chest != null) {
            if (controller.validateOrganizedChest(chest)) {
                System.out.println("Do you want to search the plantation in the chest by its name? [1]Yes, [2]No");
                int option = reader.nextInt();
                reader.nextLine();

                switch (option) {
                    case 1:
                        System.out.println("Type the initial string of the plantation name to search:");
                        String partialName = reader.nextLine();
                        System.out.println("Matching plantations in the chest:");
                        System.out.println(controller.listChestPlantationsByPartialName(chest, partialName));

                        System.out.println("Type the full plantation name to take out from the chest:");
                        String fullName = reader.nextLine();
                        try {
                            controller.moveItemFromChestToInventory(chest, fullName);
                            System.out.println("Plantation successfully moved to inventory.");
                        } catch (InventoryFullException e) {
                            System.out.println("Cannot move plantation: " + e.getMessage());
                        } catch (InexistentPlantationException e) {
                            System.out.println("Cannot move plantation: " + e.getMessage());
                        }
                        break;

                    case 2:
                        System.out.println("Type the plantation name to take out from the chest:");
                        System.out.println(controller.listChestPlantations(chest));
                        String plantationName = reader.nextLine();
                        try {
                            controller.moveItemFromChestToInventory(chest, plantationName);
                            System.out.println("Plantation successfully moved to inventory.");
                        } catch (InventoryFullException e) {
                            System.out.println("Cannot move plantation: " + e.getMessage());
                        } catch (InexistentPlantationException e) {
                            throw new RuntimeException(e);
                        }
                        break;

                    default:
                        System.out.println("Invalid option, please try again.");
                        break;
                }
            }else {
                System.out.println("Type the plantation name to take out from the chest:");
                System.out.println(controller.listChestPlantations(chest));
                String plantationName = reader.nextLine();
                try {
                    controller.moveItemFromChestToInventory(chest, plantationName);
                    System.out.println("Plantation successfully moved to inventory.");
                } catch (InventoryFullException e) {
                    System.out.println("Cannot move plantation: " + e.getMessage());
                } catch (InexistentPlantationException e) {
                    throw new RuntimeException(e);
                }
            }
        }else{
            System.out.println("Invalid option, please try again.");
        }
    }

    public void organizeChests() {
        System.out.println("Choose how you want to organize the chests:");
        System.out.println("[1] By Name");
        System.out.println("[2] By Season");
        System.out.println("[3] By Growth Days");
        int option = reader.nextInt();
        reader.nextLine();  // Limpiar el buffer

        boolean organized = false;

        switch (option) {
            case 1:
                organized = controller.organizeChestByName();
                if (organized) {
                    System.out.println("All chests have been organized by name.");
                } else {
                    System.out.println("No chests to organize.");
                }
                break;

            case 2:
                organized = controller.organizeChestBySeason();
                if (organized) {
                    System.out.println("All chests have been organized by season.");
                } else {
                    System.out.println("No chests to organize.");
                }
                break;

            case 3:
                organized = controller.organizeChestByGrowthDays();
                if (organized) {
                    System.out.println("All chests have been organized by growth days.");
                } else {
                    System.out.println("No chests to organize.");
                }
                break;

            default:
                System.out.println("Invalid option. Please try again.");
                break;
        }
    }


    public void classifyNewChest(String chestName) throws InexistentChestException {

        System.out.println("Elija la temporada para clasificar el cofre:");
        System.out.println("[1] SPRING");
        System.out.println("[2] SUMMER");
        System.out.println("[3] AUTUMN");
        System.out.println("[4] WINTER");
        int option = reader.nextInt();
        reader.nextLine();
        try {
            controller.classifyChest(chestName, option);
        }catch(InexistentChestException e){
            System.out.println(e.getMessage());
        }
        System.out.println("Chest '" + chestName + "is now classified");
    }

    public void organizeChest(){

        System.out.println("Choose how you want to organize the chest:");
        System.out.println("[1] By Name");
        System.out.println("[2] By Season");
        System.out.println("[3] By Growth Days");
        int option = reader.nextInt();
        reader.nextLine();
        boolean organized = false;
        switch (option) {
            case 1:
                organized = controller.organizeChestByName();
                if (organized) {
                    System.out.println("All chests have been organized by name.");

                }
                break;
                case 2:
                    organized = controller.organizeChestBySeason();
                    if (organized) {
                        System.out.println("All chests have been organized by season.");
                    }
                    break;
                    case 3:
                        organized = controller.organizeChestByGrowthDays();
                        if (organized) {
                            System.out.println("All chests have been organized by growth days.");
                        }
                        break;
                        default:
                            System.out.println("Invalid option. Please try again.");
        }

    }


}
