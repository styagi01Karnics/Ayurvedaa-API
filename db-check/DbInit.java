import java.nio.file.*;
import java.sql.*;
import java.util.*;

public class DbInit {
  public static void main(String[] a) throws Exception {
    String url = "jdbc:postgresql://103.174.103.250:5432/ayurveda_db";
    String user = env("DB_USER");
    String pass = env("DB_PASSWORD");
    String adminUser = System.getenv("DB_ADMIN_USER");
    String adminPass = System.getenv("DB_ADMIN_PASSWORD");
    Path root = Paths.get(System.getProperty("user.dir"));
    if (!Files.isDirectory(root.resolve("db-init"))) {
      root = root.getParent();
    }

    try (Connection c = DriverManager.getConnection(url, user, pass)) {
      c.setAutoCommit(true);
      System.out.println("Connected as " + user);

      if (adminUser != null && !adminUser.isBlank() && adminPass != null) {
        try (Connection admin = DriverManager.getConnection(url, adminUser, adminPass)) {
          admin.setAutoCommit(true);
          System.out.println("Connected as admin " + adminUser);
          setupSchemaAsAdmin(admin);
          movePublicTables(admin);
          runMigrations(admin, root.resolve("patient-service/src/main/resources/db/migration"));
          runMigrations(admin, root.resolve("doctor-service/src/main/resources/db/migration"));
          runMigrations(admin, root.resolve("therapist-service/src/main/resources/db/migration"));
          runMigrations(admin, root.resolve("appointment-service/src/main/resources/db/migration"));
          runMigrations(admin, root.resolve("file-upload-service/src/main/resources/db/migration"));
        } catch (SQLException e) {
          System.out.println("Admin connection failed: " + e.getMessage());
          System.out.println("Run db-init/00_setup_ayurveda_schema.sql as postgres on the database server.");
        }
      } else {
        if (!tryGrant(c)) {
          System.out.println("Could not apply grants (need superuser). Set DB_ADMIN_USER and DB_ADMIN_PASSWORD.");
        }
        movePublicTables(c);
        runMigrations(c, root.resolve("patient-service/src/main/resources/db/migration"));
        runMigrations(c, root.resolve("doctor-service/src/main/resources/db/migration"));
        runMigrations(c, root.resolve("therapist-service/src/main/resources/db/migration"));
        runMigrations(c, root.resolve("appointment-service/src/main/resources/db/migration"));
        runMigrations(c, root.resolve("file-upload-service/src/main/resources/db/migration"));
      }

      listTables(c);
    }
  }

  private static String env(String k) {
    String v = System.getenv(k);
    if (v == null || v.isBlank()) throw new IllegalStateException("Set " + k);
    return v;
  }

  private static void setupSchemaAsAdmin(Connection admin) throws SQLException {
    admin.createStatement().execute("ALTER SCHEMA ayurveda OWNER TO ayurveda");
    admin.createStatement().execute("GRANT USAGE, CREATE ON SCHEMA ayurveda TO ayurveda");
    admin.createStatement().execute("GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA ayurveda TO ayurveda");
    admin.createStatement().execute("GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA ayurveda TO ayurveda");
    System.out.println("Schema ownership and grants applied.");
  }

  private static boolean tryGrant(Connection c) {
    String[] grants = {
        "ALTER SCHEMA ayurveda OWNER TO ayurveda",
        "GRANT USAGE, CREATE ON SCHEMA ayurveda TO ayurveda",
        "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA ayurveda TO ayurveda",
        "ALTER DEFAULT PRIVILEGES IN SCHEMA ayurveda GRANT ALL ON TABLES TO ayurveda"
    };
    boolean ok = true;
    for (String sql : grants) {
      try {
        c.createStatement().execute(sql);
      } catch (SQLException e) {
        System.out.println("Grant skipped: " + e.getMessage());
        ok = false;
      }
    }
    return ok;
  }

  private static boolean movePublicTables(Connection c) throws SQLException {
    String[] tables = {
        "mst_patient", "mst_doctor", "mst_therapist", "mst_treatment_category", "mst_therapy",
        "appointment_bookings", "appointment_consultation_types", "appointment_therapies",
        "appointment_therapy_recommendations", "appointment_ayurvedic_assessments",
        "appointment_physical_examinations", "appointment_medical_histories",
        "appointment_lifestyle_information", "appointment_systemic_examinations",
        "appointment_treatment_plans", "appointment_documents"
    };
    boolean moved = false;
    for (String t : tables) {
      if (!exists(c, "public", t)) continue;
      if (exists(c, "ayurveda", t)) {
        System.out.println("Skip move " + t + " (already in ayurveda)");
        continue;
      }
      try {
        c.createStatement().execute("ALTER TABLE public." + t + " SET SCHEMA ayurveda");
        System.out.println("Moved public." + t + " -> ayurveda." + t);
        moved = true;
      } catch (SQLException e) {
        System.out.println("Move failed for " + t + ": " + e.getMessage());
      }
    }
    return moved;
  }

  private static boolean exists(Connection c, String schema, String table) throws SQLException {
    try (PreparedStatement ps = c.prepareStatement(
        "SELECT 1 FROM information_schema.tables WHERE table_schema=? AND table_name=?")) {
      ps.setString(1, schema);
      ps.setString(2, table);
      try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
    }
  }

  private static void runMigrations(Connection c, Path dir) throws Exception {
    if (!Files.isDirectory(dir)) {
      System.out.println("Skip missing dir: " + dir);
      return;
    }
    List<Path> files = Files.list(dir).filter(p -> p.toString().endsWith(".sql")).sorted().toList();
    for (Path file : files) {
      String sql = Files.readString(file);
      System.out.println("Running " + file.getFileName());
      try (Statement s = c.createStatement()) {
        s.execute("SET search_path TO ayurveda");
        s.execute(sql);
        System.out.println("  OK");
      } catch (SQLException e) {
        System.out.println("  " + e.getMessage());
      }
    }
  }

  private static void listTables(Connection c) throws SQLException {
    System.out.println("\n=== Tables in ayurveda schema ===");
    try (ResultSet rs = c.createStatement().executeQuery(
        "SELECT table_name FROM information_schema.tables WHERE table_schema='ayurveda' ORDER BY 1")) {
      int n = 0;
      while (rs.next()) {
        System.out.println(rs.getString(1));
        n++;
      }
      System.out.println("TOTAL=" + n);
    }
  }
}
