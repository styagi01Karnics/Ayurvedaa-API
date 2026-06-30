import java.sql.*;

public class DbPermCheck {
  public static void main(String[] a) throws Exception {
    String url = "jdbc:postgresql://103.174.103.250:5432/ayurveda_db";
    String user = System.getenv("DB_USER");
    String pass = System.getenv("DB_PASSWORD");
    if (user == null || pass == null) {
      System.err.println("Set DB_USER and DB_PASSWORD environment variables.");
      System.exit(1);
    }
    try (Connection c = DriverManager.getConnection(url, user, pass)) {
      try (ResultSet rs = c.createStatement().executeQuery(
          "SELECT has_database_privilege(current_user, current_database(), 'CREATE')")) {
        if (rs.next()) System.out.println("database CREATE=" + rs.getBoolean(1));
      }
      try (ResultSet rs = c.createStatement().executeQuery(
          "SELECT nspname, has_schema_privilege(current_user, nspname, 'USAGE') AS usage, " +
          "has_schema_privilege(current_user, nspname, 'CREATE') AS create " +
          "FROM pg_namespace WHERE nspname IN ('public','ayurveda') ORDER BY 1")) {
        while (rs.next()) System.out.println(rs.getString(1) + " usage=" + rs.getBoolean(2) + " create=" + rs.getBoolean(3));
      }
      try {
        c.createStatement().execute("CREATE SCHEMA IF NOT EXISTS app_test AUTHORIZATION " + user);
        c.createStatement().execute("DROP SCHEMA app_test");
        System.out.println("OK - can create new schema");
      } catch (SQLException e) {
        System.out.println("CREATE SCHEMA failed: " + e.getMessage());
      }
      System.out.println("\npublic tables:");
      try (ResultSet rs = c.createStatement().executeQuery(
          "SELECT table_name FROM information_schema.tables WHERE table_schema='public' ORDER BY 1")) {
        while (rs.next()) System.out.println("  " + rs.getString(1));
      }
    }
  }
}
