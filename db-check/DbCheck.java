import java.sql.*;
public class DbCheck {
  public static void main(String[] a) throws Exception {
    String url = "jdbc:postgresql://103.174.103.250:5432/ayurveda_db?currentSchema=public";
    try (Connection c = DriverManager.getConnection(url, "ayurveda", "S@b8at1zeauyrveda")) {
      Statement s = c.createStatement();
      ResultSet rs = s.executeQuery("SELECT table_schema, table_name FROM information_schema.tables WHERE table_schema IN ('public','ayurveda') ORDER BY 1,2");
      int n = 0;
      while (rs.next()) { System.out.println(rs.getString(1)+"."+rs.getString(2)); n++; }
      System.out.println("TOTAL="+n);
    }
  }
}
