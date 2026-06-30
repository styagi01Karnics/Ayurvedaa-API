import java.sql.*;
public class DoshaSchema {
  public static void main(String[] a) throws Exception {
    try (Connection c = DriverManager.getConnection("jdbc:postgresql://103.174.103.250:5432/ayurveda_db?currentSchema=ayurveda","ayurveda","S@b8at1zeauyrveda")) {
      ResultSet rs = c.getMetaData().getColumns(null, "ayurveda", "doshas", null);
      while (rs.next()) System.out.println(rs.getString("COLUMN_NAME")+" "+rs.getString("TYPE_NAME")+" nullable="+rs.getString("IS_NULLABLE"));
      System.out.println("---rows---");
      rs = c.createStatement().executeQuery("SELECT * FROM ayurveda.doshas ORDER BY id");
      while (rs.next()) System.out.println(rs.getInt(1)+" | "+rs.getString(2)+" | "+rs.getString(3));
    }
  }
}
