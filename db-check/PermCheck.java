import java.sql.*;

public class PermCheck {
  public static void main(String[] a) throws Exception {
    String url = "jdbc:postgresql://103.174.103.250:5432/ayurveda_db";
    try (Connection c = DriverManager.getConnection(url, "ayurveda", "S@b8at1zeauyrveda")) {
      System.out.println("=== Current user ===");
      try (ResultSet rs = c.createStatement().executeQuery("SELECT current_user, current_schema(), current_database()")) {
        if (rs.next()) System.out.println(rs.getString(1) + " | schema=" + rs.getString(2) + " | db=" + rs.getString(3));
      }
      System.out.println("\n=== Schema privileges ===");
      try (ResultSet rs = c.createStatement().executeQuery(
          "SELECT nspname, has_schema_privilege(current_user, nspname, 'USAGE') AS usage, " +
          "has_schema_privilege(current_user, nspname, 'CREATE') AS create " +
          "FROM pg_namespace WHERE nspname IN ('public','ayurveda') ORDER BY 1")) {
        while (rs.next()) System.out.println(rs.getString(1) + " usage=" + rs.getBoolean(2) + " create=" + rs.getBoolean(3));
      }
      System.out.println("\n=== Tables in ayurveda schema ===");
      try (ResultSet rs = c.createStatement().executeQuery(
          "SELECT table_name FROM information_schema.tables WHERE table_schema='ayurveda' ORDER BY 1")) {
        int n = 0;
        while (rs.next()) { System.out.println(rs.getString(1)); n++; }
        if (n == 0) System.out.println("(none)");
      }
      System.out.println("\n=== Try CREATE in ayurveda ===");
      try {
        c.createStatement().execute("CREATE TABLE IF NOT EXISTS ayurveda._perm_test (id int)");
        c.createStatement().execute("DROP TABLE ayurveda._perm_test");
        System.out.println("OK - can create tables in ayurveda");
      } catch (SQLException e) {
        System.out.println("FAIL: " + e.getMessage());
      }
      System.out.println("\n=== Try CREATE in public ===");
      try {
        c.createStatement().execute("CREATE TABLE IF NOT EXISTS public._perm_test (id int)");
        c.createStatement().execute("DROP TABLE public._perm_test");
        System.out.println("OK - can create tables in public");
      } catch (SQLException e) {
        System.out.println("FAIL: " + e.getMessage());
      }
    }
  }
}
