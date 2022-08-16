package fr.iban.shop.storage;

import fr.iban.common.data.sql.DbAccess;
import fr.iban.shop.ShopItem;
import fr.iban.shop.utils.ItemStackSerializer;
import fr.iban.shop.utils.ShopAction;
import org.bukkit.inventory.ItemStack;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SqlStorage {

    private final DataSource ds = DbAccess.getDataSource();

    public SqlStorage() {
        init();
    }

    private void init() {
        String[] createStatements = new String[]{
                "CREATE TABLE IF NOT EXISTS shop_categories(" +
                        "id INTEGER PRIMARY KEY AUTO_INCREMENT ," +
                        "name VARCHAR(255)" +
                        "); ",
                "CREATE TABLE IF NOT EXISTS shop_items(" +
                        "id INTEGER PRIMARY KEY AUTO_INCREMENT ," +
                        "name VARCHAR(255) ," +
                        "itemstack TEXT UNIQUE ," +
                        "buyPrice FLOAT," +
                        "sellPrice FLOAT," +
                        "stock INTEGER," +
                        "maxStock INTEGER," +
                        "category_id INTEGER," +
                        "FOREIGN KEY (category_id) REFERENCES shop_categories(id) ON DELETE CASCADE" +
                        "); ",
                "CREATE TABLE IF NOT EXISTS shop_transactions(" +
                        "item_id INTEGER ," +
                        "uuid VARCHAR(36) ," +
                        "amount INTEGER," +
                        "price FLOAT," +
                        "type INTEGER," +
                        "createdAt DATETIME DEFAULT NOW()," +
                        "FOREIGN KEY (item_id) REFERENCES shop_items(id) ON DELETE CASCADE" +
                        "); ",
        };

        try (Connection connection = ds.getConnection()) {

            for (String createStatement : createStatements) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(createStatement)) {
                    preparedStatement.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ShopItem> getItems() {
        String sql = "SELECT I.id, I.name, itemStack, buyPrice, sellPrice, stock, maxStock, C.name " +
                "FROM shop_items I JOIN shop_categories C ON I.category_id=C.id;";
        List<ShopItem> items = new ArrayList<>();

        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        items.add(getItemFromResultSet(rs));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    public ShopItem getItem(int itemID) {
        String sql = "SELECT I.id, I.name, itemStack, buyPrice, sellPrice, stock, maxStock, C.name " +
                "FROM shop_items I JOIN shop_categories C ON I.category_id=C.id " +
                "WHERE I.id=?;";
        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, itemID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return getItemFromResultSet(rs);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ShopItem getItemFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("I.id");
        ItemStack itemStack = ItemStackSerializer.fromBase64(rs.getString("itemStack"));
        double buyPrice = rs.getDouble("buyPrice");
        double sellPrice = rs.getDouble("sellPrice");
        int stock = rs.getInt("stock");
        int maxStock = rs.getInt("maxStock");
        String category = rs.getString("C.name");
        return new ShopItem(id, buyPrice, sellPrice, itemStack, category, maxStock, stock);
    }


    public void addItem(ShopItem item) {
        String sql = "INSERT INTO shop_items(name, itemStack, buyPrice, sellPrice, stock, maxStock, category_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, (SELECT id FROM shop_categories WHERE name=?));";

        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ItemStack itemStack = item.getItemStack();
                if (itemStack.getItemMeta() != null && itemStack.getItemMeta().hasDisplayName()) {
                    ps.setString(1, itemStack.getItemMeta().getDisplayName());
                } else {
                    ps.setString(1, itemStack.getType().toString());
                }
                ps.setString(2, ItemStackSerializer.toBase64(itemStack));
                ps.setDouble(3, item.getBuyPrice());
                ps.setDouble(4, item.getSellPrice());
                ps.setInt(5, item.getStock());
                ps.setInt(6, item.getMaxStock());
                ps.setString(7, item.getCategory());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateItem(ShopItem item) {
        String sql = "UPDATE shop_items SET name=?, itemStack=?, buyPrice=?, sellPrice=?, stock=?, maxStock=? WHERE id=?;";
        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ItemStack itemStack = item.getItemStack();
                if (itemStack.getItemMeta() != null && itemStack.getItemMeta().hasDisplayName()) {
                    ps.setString(1, itemStack.getItemMeta().getDisplayName());
                } else {
                    ps.setString(1, itemStack.getType().toString());
                }
                ps.setString(2, ItemStackSerializer.toBase64(itemStack));
                ps.setDouble(3, item.getBuyPrice());
                ps.setDouble(4, item.getSellPrice());
                ps.setInt(5, item.getStock());
                ps.setInt(6, item.getMaxStock());
                ps.setInt(7, item.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteItem(int itemID) {
        String sql = "DELETE FROM shop_items WHERE id=?";
        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, itemID);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getStock(int itemID) {
        String sql = "SELECT stock FROM shop_items WHERE id=?";
        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, itemID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("stock");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void saveStock(ShopItem item) {
        String sql = "UPDATE shop_items SET stock=? WHERE id=?";
        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, item.getStock());
                ps.setInt(2, item.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getCategories() {
        String sql = "SELECT name FROM shop_categories;";
        List<String> categories = new ArrayList<>();

        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        categories.add(rs.getString("name"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

    public void addCategory(String name) {
        String sql = "INSERT INTO shop_categories(name) VALUES (?)";
        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCategory(String name) {
        String sql = "DELETE FROM shop_categories WHERE name=?";
        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addTransactionLog(ShopItem item, UUID uuid, int amount, double price, ShopAction action) {
        String sql = "INSERT INTO shop_transactions(item_id, uuid, amount, price, type) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, item.getId());
                ps.setString(2, uuid.toString());
                ps.setInt(3, amount);
                ps.setDouble(4, price);
                ps.setInt(5, action == ShopAction.BUY ? 0 : 1);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
