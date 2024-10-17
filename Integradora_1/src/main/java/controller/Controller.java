package controller;

import com.google.gson.GsonBuilder;
import exceptions.*;
import model.Plantation;
import model.Chest;
import model.Season;
import model.Stack;
import structures.LinkedList;
import structures.Node;

import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Controller {

    private LinkedList<Plantation> plantations;
    protected LinkedList<Plantation> inventory;
    private LinkedList<Plantation> cropField;
    private LinkedList<Chest> chests;
    private ScheduledExecutorService scheduler;
    private Season currentSeason;
    private int daysInCurrentSeason;
    private File data;
    private File listJson;
    private static final String INVENTORY_JSON = "src/main/data/inventory.json";
    private static final String CROPFIELD_JSON = "src/main/data/cropField.json";
    private static final String CHESTS_JSON = "src/main/data/chests.json";

    private final int INVENTORY_LIMIT = 30;

    public Controller() {
        plantations = new LinkedList<>();
        inventory = new LinkedList<>();
        cropField = new LinkedList<>();
        chests = new LinkedList<>();
        currentSeason = Season.SPRING;
        daysInCurrentSeason = 0;
        startGrowthUpdateScheduler();
        File projectDir = new File(System.getProperty("user.dir"));
        data = new File(projectDir + File.separator + "src" + File.separator + "main" + File.separator + "data");
        listJson = new File(data.getAbsolutePath() + File.separator + "plantations.json");
        createResources();
        loadPlantationsFromJson();
        loadInventoryFromJson();
        loadChestsFromJson();
        loadCropFieldFromJson();
    }

    public void saveAllDataToJson() {
        saveInventoryToJson();
        saveCropFieldToJson();
        saveChestsToJson();
    }

    public void saveInventoryToJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(INVENTORY_JSON)) {
            gson.toJson(inventory, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveCropFieldToJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(CROPFIELD_JSON)) {
            gson.toJson(cropField, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveChestsToJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(CHESTS_JSON)) {
            gson.toJson(chests, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadInventoryFromJson() {
        Gson gson = new Gson();
        try (BufferedReader reader = new BufferedReader(new FileReader(INVENTORY_JSON))) {
            Type type = new TypeToken<LinkedList<Plantation>>() {
            }.getType();
            inventory = gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void loadCropFieldFromJson() {
        Gson gson = new Gson();
        try (BufferedReader reader = new BufferedReader(new FileReader(CROPFIELD_JSON))) {
            Type type = new TypeToken<LinkedList<Plantation>>() {
            }.getType();
            cropField = gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadChestsFromJson() {
        Gson gson = new Gson();
        try (BufferedReader reader = new BufferedReader(new FileReader(CHESTS_JSON))) {
            Type type = new TypeToken<LinkedList<Chest>>() {}.getType();
            chests = gson.fromJson(reader, type);
            if (chests == null) {
                chests = new LinkedList<>();
            }
        } catch (IOException e) {
            System.out.println("Error al cargar cofres: " + e.getMessage());
        }
    }


    public void createResources() {
        if (!data.exists()) {
            if (data.mkdir()) {
                System.out.println("Directorio de datos creado en: " + data.getAbsolutePath());
            } else {
                System.out.println("Error al crear el directorio de datos.");
            }
        }
    }

    public void loadPlantationsFromJson() {
        try (FileReader reader = new FileReader(listJson)) {
            System.out.println("Cargando inventario desde: " + listJson.getAbsolutePath());
            Type productListType = new TypeToken<List<Plantation>>() {
            }.getType();
            List<Plantation> productList = new Gson().fromJson(reader, productListType);

            if (productList == null) {
                System.out.println("El archivo JSON está vacío o mal formateado.");
                return;
            }

            for (Plantation plantation : productList) {
                plantations.add(new Plantation(plantation.getName(), plantation.getSeason()));
                System.out.println("Plantation cargada: " + plantation);
            }
        } catch (IOException e) {
            System.out.println("Error al cargar el inventario: " + e.getMessage());
        }
    }

    public boolean createPlantationByName(String name) throws IncorrectSeasonException {
        Node<Plantation> current = plantations.getHead();
        boolean plantationAdded = false;

        while (current != null) {
            Plantation plantation = current.getData();
            if (plantation.getName().equalsIgnoreCase(name)) {

                if (canAddPlantation(plantation)) {
                    Plantation copy = new Plantation(plantation.getName(), plantation.getSeason());
                    cropField.add(copy);
                    plantationAdded = true;
                } else {
                    throw new IncorrectSeasonException("Cannot plant in the current season: ");
                }
            }
            current = current.getNextNode();
        }

        return plantationAdded;
    }


    public String showCurrentSeason() {
        return currentSeason.toString();
    }

    private void startGrowthUpdateScheduler() {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::updateGrowthDays, 0, 1, TimeUnit.MINUTES);
    }

    private boolean canAddPlantation(Plantation plantation) {
        if (plantation.getSeason() == Season.OTHER) {
            return true;
        }
        return plantation.getSeason() == currentSeason;  // Coincidir con la estación actual
    }

    private void updateGrowthDays() {
        Node<Plantation> current = cropField.getHead();
        Node<Plantation> prev = null;

        while (current != null) {
            Plantation plantation = current.getData();
            plantation.incrementGrowthDays(28);  // Incrementar los días de crecimiento en 28

            if (plantation.hasExceededSeason() || (plantation.getSeason() != Season.OTHER && plantation.getSeason() != currentSeason)) {
                if (prev == null) {
                    cropField.removeFirst();
                } else {
                    prev.setNextNode(current.getNextNode());  // Eliminar el elemento actual
                }
            } else {
                prev = current;
            }

            current = current.getNextNode();
        }

        updateSeason();  // Actualizar la estación después de cada minuto
        System.out.println(cropField.printList());
    }

    private void updateSeason() {
        daysInCurrentSeason++;
        if (daysInCurrentSeason >= 28) {
            daysInCurrentSeason = 0;
            currentSeason = getNextSeason();
        }
    }

    public void setCurrentSeason(Season season) {
        currentSeason = season;
    }

    private Season getNextSeason() {
        switch (currentSeason) {
            case SPRING:
                return Season.SUMMER;
            case SUMMER:
                return Season.AUTUMN;
            case AUTUMN:
                return Season.WINTER;
            case WINTER:
                return Season.SPRING;
            default:
                return currentSeason;
        }
    }


    public String listAvaliblePlantations() {
        return plantations.printList();
    }

    public String listCropFieldReadyPlantations() {
        return cropField.printList();
    }


    public boolean movePlantationFromCropFieldToInventory(String plantationName) throws InventoryFullException {
        if (inventory.size() >= INVENTORY_LIMIT) {
            throw new InventoryFullException("The inventory is full! Cannot add more plantations.");
        }

        Node<Plantation> current = cropField.getHead();
        Node<Plantation> prev = null;

        while (current != null) {
            Plantation plantation = current.getData();
            if (plantation.getName().equalsIgnoreCase(plantationName)) {

                System.out.println("Plantation encontrada en cropField: " + plantation.getName());


                if (prev == null) {
                    cropField.removeFirst();
                    System.out.println("Primera plantación eliminada del cropField.");
                } else {
                    prev.setNextNode(current.getNextNode());
                    System.out.println("Plantación eliminada del cropField.");
                }

                inventory.add(plantation);
                System.out.println("Plantación añadida al inventario: " + plantation.getName());

                return true;
            }
            prev = current;
            current = current.getNextNode();
        }

        System.out.println("Plantación '" + plantationName + "' no encontrada en el cropField.");
        return false;
    }

    public boolean createChest(String chestName) throws InexistentChestException {

        Node<Chest> current = chests.getHead();
        while (current != null) {
            Chest chest = current.getData();
            if (chest.getName().equalsIgnoreCase(chestName)) {
                throw new InexistentChestException("El cofre con el nombre '" + chestName + "' ya existe.");
            }
            current = current.getNextNode();
        }


        Chest newChest = new Chest(chestName);
        chests.add(newChest);
        return true;
    }





    public void classifyChest(String chestName, int season) throws InexistentChestException {
        Node<Chest> currentChest = chests.getHead();
        if(currentChest == null){
            throw new InexistentChestException("Inexistent chest");
        }

        while (currentChest != null) {
            Chest chest = currentChest.getData();

            if (chest.getName().equalsIgnoreCase(chestName)) {
                chest.setClassificationType(Season.values()[season - 1]);
            }
            currentChest = currentChest.getNextNode();
        }

    }

    public Chest searchChest(String chestName) throws InexistentChestException {
        Node<Chest> currentChest = chests.getHead();

        while (currentChest != null) {
            Chest chest = currentChest.getData();

            if (chest.getName().equalsIgnoreCase(chestName)) {
                return chest;
            }
            currentChest = currentChest.getNextNode();
        }

        throw new InexistentChestException("Chest not found");
    }


    public String listChests() {
        if (chests.isEmpty()) {
            return "No chests available.";
        }

        StringBuilder sb = new StringBuilder();
        Node<Chest> current = chests.getHead();
        int count = 1;

        while (current != null) {
            Chest chest = current.getData();
            sb.append("Chest ").append(count).append(": ").append(chest.getName()).append("\n");
            current = current.getNextNode();
            count++;
        }

        return sb.toString();
    }




    public void movePlantationToChest(String chestName, String plantationName) throws InexistentChestException, InventoryEmptyException, ChestFullException, InexistentPlantationException {

        Chest chest = searchChestByName(chestName);
        if (chest == null) {
            throw new InexistentChestException("El cofre con el nombre '" + chestName + "' no fue encontrado.");
        }

        Plantation plantationToMove = null;
        Node<Plantation> current = inventory.getHead();
        while (current != null) {
            if (current.getData().getName().equalsIgnoreCase(plantationName)) {
                plantationToMove = current.getData();
                break;
            }
            current = current.getNextNode();
        }

        if (plantationToMove == null) {
            throw new InexistentPlantationException("La plantación '" + plantationName + "' no existe en el inventario.");
        }

        if (!chest.addPlantation(plantationToMove)) {
            throw new ChestFullException("El cofre está lleno o no se puede agregar la plantación.");
        }

        inventory.remove(plantationToMove);
        System.out.println("Plantación '" + plantationName + "' movida al cofre '" + chestName + "' con éxito.");
    }

    public boolean organizeChestByName() {
        Node<Chest> currentChest = chests.getHead();
        boolean organized = false;

        while (currentChest != null) {
            currentChest.getData().organizeByName();
            currentChest = currentChest.getNextNode();
            organized = true;
        }

        return organized;
    }


    public boolean organizeChestBySeason() {
        Node<Chest> currentChest = chests.getHead();
        boolean organized = false;

        while (currentChest != null) {
            currentChest.getData().organizeBySeason();
            currentChest = currentChest.getNextNode();
            organized = true;
        }

        return organized;
    }


    public boolean organizeChestByGrowthDays() {
        Node<Chest> currentChest = chests.getHead();
        boolean organized = false;

        while (currentChest != null) {
            currentChest.getData().organizeByGrowthDays();
            currentChest = currentChest.getNextNode();
            organized = true;
        }

        return organized;
    }


    public String listChestPlantations(Chest chest) {
        if (chest != null) {
            return chest.listPlantations();
        }
        return null;
    }

    public String listInventoryPlantations() {

        if (inventory.isEmpty()) {
            return "El inventario está vacío.";
        }

        StringBuilder sb = new StringBuilder();
        Node<Plantation> current = inventory.getHead();


        while (current != null) {
            Plantation plantation = current.getData();
            sb.append(plantation.getName()).append(" - Temporada: ").append(plantation.getSeason()).append("\n");
            current = current.getNextNode();
        }

        return sb.toString();
    }


    public boolean validateOrganizedChest(Chest chest) {

        return chest.isClassified(); // Retorna true si el cofre está clasificado
    }

    public String listChestPlantationsByPartialName(Chest chest, String partialName) {
        StringBuilder sb = new StringBuilder();
        for (Stack stack : chest.getStacks()) {
            for (Plantation plantation : stack.getPlantations()) {
                if (plantation.getName().toLowerCase().startsWith(partialName.toLowerCase())) {
                    sb.append(plantation.getName()).append("\n");
                }
            }
        }
        return sb.toString();
    }

    public void moveItemFromChestToInventory(Chest chest, String plantationName)
            throws InventoryFullException, InexistentPlantationException {

        Stack targetStack = null;
        Plantation plantationToMove = null;

        for (Stack stack : chest.getStacks()) {
            for (Plantation plantation : stack.getPlantations()) {
                if (plantation.getName().equalsIgnoreCase(plantationName)) {
                    targetStack = stack;
                    plantationToMove = plantation;
                    break;
                }
            }
            if (plantationToMove != null) break;
        }

        if (plantationToMove == null) {
            throw new InexistentPlantationException("Plantation not found in the chest.");
        }

        if (inventory.size() >= 30) {
            throw new InventoryFullException("Inventory is full.");
        }

        inventory.add(plantationToMove);
        targetStack.removePlantation(plantationToMove);

        System.out.println("Plantation moved successfully.");
    }

    public Plantation getPlantationFromInventory(String plantationName) throws InexistentPlantationException {

        Plantation plantation = inventory.searchElement(p -> p.getName().equalsIgnoreCase(plantationName));

        if (plantation == null) {
            throw new InexistentPlantationException("Plantation not found in the inventory.");
        }

        return plantation;
    }

    public Chest searchChestByName(String chestName) throws InexistentChestException {

        Node<Chest> current = chests.getHead();


        while (current != null) {
            Chest chest = current.getData();

            if (chest.getName().equalsIgnoreCase(chestName)) {
                return chest;
            }

            current = current.getNextNode();
        }


        throw new InexistentChestException("El cofre con el nombre '" + chestName + "' no fue encontrado.");
    }

}
