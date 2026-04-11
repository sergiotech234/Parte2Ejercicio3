import java.sql.*;

public class Main {

    public static void main(String[] args) {
//Conexion a una base de datos
        String url="jdbc:oracle:thin:@localhost:1521:xe";
        String user="USUARIO";
        String password="ribera";
        try (Connection conn = DriverManager.getConnection(url, user, password)) {

            // Consulta SQL
            String sql = """
                SELECT c.nombre AS nombre_ciclista,
                       e.nombre AS nombre_equipo,
                       SUM(p.puntos) AS puntos_totales
                FROM ciclista c
                JOIN equipo e ON c.id_equipo = e.id_equipo
                JOIN participacion p ON c.id_ciclista = p.id_ciclista
                GROUP BY c.nombre, e.nombre
                ORDER BY puntos_totales DESC
                FETCH FIRST 10 ROWS ONLY
            """;

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            //Resultado en consola
            System.out.println("=== CLASIFICACIÓN GENERAL ===");

            while (rs.next()) {
                String nombre = rs.getString("nombre_ciclista");
                String equipo = rs.getString("nombre_equipo");
                int puntos = rs.getInt("puntos_totales");

                System.out.println(nombre + " - " + equipo + " - " + puntos);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
