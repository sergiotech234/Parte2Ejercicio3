import java.sql.*;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String user = "RIBERA";
        String password = "ribera";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {

            System.out.println("=== CLASIFICACIÓN GENERAL ===");
            clasificacionGeneral(conn);

            System.out.println("\n=== CLASIFICACIÓN POR EQUIPOS ===");
            clasificacionEquipos(conn);

            System.out.println("\n=== ETAPAS MÁS LARGAS ===");
            etapasLargas(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Clasificación general por puntos
    public static void clasificacionGeneral(Connection conn) throws SQLException {

        String sql = """
            SELECT c.NOMBRE AS nombre_ciclista,
                   e.NOMBRE AS nombre_equipo,
                   SUM(p.PUNTOS) AS puntos_totales
            FROM CICLISTA c
            JOIN EQUIPO e ON c.ID_EQUIPO = e.ID_EQUIPO
            JOIN PARTICIPACION p ON c.ID_CICLISTA = p.ID_CICLISTA
            GROUP BY c.ID_CICLISTA, c.NOMBRE, e.NOMBRE
            ORDER BY puntos_totales DESC
            FETCH FIRST 10 ROWS ONLY
        """;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(
                        rs.getString("nombre_ciclista") + " - " +
                                rs.getString("nombre_equipo") + " - " +
                                rs.getInt("puntos_totales")
                );
            }
        }
    }

    // Clasificación por equipos
    public static void clasificacionEquipos(Connection conn) throws SQLException {

        String sql = """
            SELECT e.NOMBRE AS nombre_equipo,
                   e.PAIS,
                   SUM(p.PUNTOS) AS puntos_equipo
            FROM EQUIPO e
            JOIN CICLISTA c ON e.ID_EQUIPO = c.ID_EQUIPO
            JOIN PARTICIPACION p ON c.ID_CICLISTA = p.ID_CICLISTA
            GROUP BY e.ID_EQUIPO, e.NOMBRE, e.PAIS
            ORDER BY puntos_equipo DESC
        """;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(
                        rs.getString("nombre_equipo") + " (" +
                                rs.getString("pais") + ") - " +
                                rs.getInt("puntos_equipo")
                );
            }
        }
    }

    // Ranking de etapas largas
    public static void etapasLargas(Connection conn) throws SQLException {

        String sql = """
            SELECT NUMERO, ORIGEN, DESTINO, DISTANCIA_KM, FECHA
            FROM ETAPA
            ORDER BY DISTANCIA_KM DESC
            FETCH FIRST 3 ROWS ONLY
        """;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(
                        "Etapa " + rs.getInt("NUMERO") + ": " +
                                rs.getString("ORIGEN") + " -> " +
                                rs.getString("DESTINO") + " | " +
                                rs.getDouble("DISTANCIA_KM") + " km | " +
                                rs.getDate("FECHA")
                );
            }
        }

        // Etapas por encima de la media
        System.out.println("\n--- Etapas por encima de la media ---");

        String sqlMedia = """
            SELECT NUMERO, ORIGEN, DESTINO, DISTANCIA_KM, FECHA
            FROM ETAPA
            WHERE DISTANCIA_KM > (
                SELECT AVG(DISTANCIA_KM) FROM ETAPA
            )
            ORDER BY DISTANCIA_KM DESC
        """;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlMedia)) {

            while (rs.next()) {
                System.out.println(
                        "Etapa " + rs.getInt("NUMERO") + ": " +
                                rs.getString("ORIGEN") + " -> " +
                                rs.getString("DESTINO") + " | " +
                                rs.getDouble("DISTANCIA_KM") + " km | " +
                                rs.getDate("FECHA")
                );
            }
        }
    }
}
