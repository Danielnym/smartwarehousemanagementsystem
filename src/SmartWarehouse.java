import java.util.*;

public class SmartWarehouse {
    private List<Item> inventory;
    private Map<String, List<Integer>> itemLocations;
    private int[][] warehouseLayout;

    public SmartWarehouse(int rows, int cols) {
        inventory = new ArrayList<>();
        itemLocations = new HashMap<>();
        warehouseLayout = new int[rows][cols];
        initializeWarehouse();
    }

    private void initializeWarehouse() {
        // Initialize the warehouse layout with random obstacles
        Random rand = new Random();
        for (int i = 0; i < warehouseLayout.length; i++) {
            for (int j = 0; j < warehouseLayout[i].length; j++) {
                warehouseLayout[i][j] = rand.nextInt(2); // 0 for empty, 1 for obstacle
            }
        }
    }

    // Inventory Management
    public void addItem(Item item, int row, int col) {
        inventory.add(item);
        itemLocations.computeIfAbsent(item.getName(), k -> new ArrayList<>()).add(row * warehouseLayout[0].length + col);
        Collections.sort(inventory);
    }

    public Item findItem(String name) {
        int index = Collections.binarySearch(inventory, new Item(name, 0), Comparator.comparing(Item::getName));
        return index >= 0 ? inventory.get(index) : null;
    }

    // Path Optimization
    public List<int[]> findOptimalPath(int startRow, int startCol, int endRow, int endCol) {
        int rows = warehouseLayout.length;
        int cols = warehouseLayout[0].length;
        int[][] distances = new int[rows][cols];
        boolean[][] visited = new boolean[rows][cols];
        int[][] prev = new int[rows][cols];

        for (int[] row : distances) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        distances[startRow][startCol] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[2]));
        pq.offer(new int[]{startRow, startCol, 0});

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int row = current[0], col = current[1];

            if (row == endRow && col == endCol) {
                break;
            }

            if (visited[row][col]) {
                continue;
            }
            visited[row][col] = true;

            for (int[] dir : directions) {
                int newRow = row + dir[0], newCol = col + dir[1];
                if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols && warehouseLayout[newRow][newCol] == 0) {
                    int newDist = distances[row][col] + 1;
                    if (newDist < distances[newRow][newCol]) {
                        distances[newRow][newCol] = newDist;
                        prev[newRow][newCol] = row * cols + col;
                        pq.offer(new int[]{newRow, newCol, newDist});
                    }
                }
            }
        }

        List<int[]> path = new ArrayList<>();
        if (distances[endRow][endCol] != Integer.MAX_VALUE) {
            for (int[] p = new int[]{endRow, endCol}; p[0] != startRow || p[1] != startCol; p = new int[]{prev[p[0]][p[1]] / cols, prev[p[0]][p[1]] % cols}) {
                path.add(p);
            }
            path.add(new int[]{startRow, startCol});
            Collections.reverse(path);
        }
        return path;
    }

    // Helper classes and methods
    static class Item implements Comparable<Item> {
        private String name;
        private int quantity;

        public Item(String name, int quantity) {
            this.name = name;
            this.quantity = quantity;
        }

        public String getName() {
            return name;
        }

        public int getQuantity() {
            return quantity;
        }

        @Override
        public int compareTo(Item other) {
            return this.name.compareTo(other.name);
        }
    }

    // Main method for demonstration
    public static void main(String[] args) {
        SmartWarehouse warehouse = new SmartWarehouse(10, 10);

        // Add some items to the warehouse
        warehouse.addItem(new Item("Book", 50), 2, 3);
        warehouse.addItem(new Item("Laptop", 20), 5, 7);
        warehouse.addItem(new Item("Phone", 100), 8, 1);

        // Find an item
        Item foundItem = warehouse.findItem("Laptop");
        System.out.println("Found item: " + (foundItem != null ? foundItem.getName() + " (Quantity: " + foundItem.getQuantity() + ")" : "Not found"));

        // Find optimal path
        List<int[]> path = warehouse.findOptimalPath(0, 0, 8, 8);
        System.out.println("Optimal path from (0,0) to (8,8):");
        for (int[] point : path) {
            System.out.println("(" + point[0] + "," + point[1] + ")");
        }
    }
}