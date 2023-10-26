package risk.controler;
import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class ConnexionDB {
    private Connection conn;

    // méthode permettant la connexion à la DB
    public ConnexionDB(String url, String user, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // connexion
            conn = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Erreur connexion : " + e.getMessage());
        }
    }

//    public void generateTable() {
//        try {
//            Statement statement = conn.createStatement();
//
//            // vérification table existe
//            ResultSet resultSetJoueur = statement.executeQuery("SHOW TABLES LIKE 'joueur'");
//            if (!resultSetJoueur.next()) {
//                String generateJoueur = "CREATE TABLE joueur ("
//                        + "id INT AUTO_INCREMENT PRIMARY KEY,"
//                        + "vNomJoueur VARCHAR(50),"
//                        + "vPrenomJoueur VARCHAR(50),"
//                        + "dtNaissance VARCHAR(50)"
//                        + ")";
//                statement.executeUpdate(generateJoueur);
//            }
//
//            // Ajoutez des blocs similaires pour les autres tables que vous avez besoin de créer
//
//            // Fermez le statement
//            statement.close();
//
//            System.out.println("Tables créées avec succès.");
//        } catch (SQLException e) {
//            System.err.println("Erreur lors de la création des tables : " + e.getMessage());
//        }
//    }
    
    // méthode permettant la lecture d'une requete sql
    public void readQuery(String sql) {
        try {
            Statement statement = conn.createStatement();

            // execute query
            ResultSet resultSet = statement.executeQuery(sql);
            
         // get nb column
            ResultSetMetaData metaData = resultSet.getMetaData();
            int nbColumn = metaData.getColumnCount();

         // display results
            while (resultSet.next()) {
                for (int i = 1; i <= nbColumn; i++) {
                    String resultat = resultSet.getString(i);
                    System.out.print(resultat + " ");
                }
                System.out.println(); //
            }
        } catch (SQLException e) {
            System.err.println("Erreur d'exécution : " + e.getMessage());
        }
    }

    
    // méthode permettant l'insertion de joueurs
    public void insertJoueur(String nom, String prenom, String dateNaissance) {
        try {
            // requete paramétrée
            String insertSQL = "INSERT INTO joueur (nom, prenom, dateNaissance) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertSQL);

            // associer paramêtres
            preparedStatement.setString(1, nom);
            preparedStatement.setString(2, prenom);
            preparedStatement.setString(3, dateNaissance);

            // execute query
            preparedStatement.executeUpdate();

            System.out.println("Joueur enregistré");
        } catch (SQLException e) {
            System.err.println("Erreur d'insertion : " + e.getMessage());
        }
    }
    
    // méthode permettant à l'utilisateur de saisir les informations demandées
    public void insertJoueurByScanner() {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Nom du joueur : ");
            String nom = scanner.nextLine();

            System.out.print("Prénom du joueur : ");
            String prenom = scanner.nextLine();

            System.out.print("Date de naissance (format YYYY-MM-DD) : ");
            String dateNaissance = scanner.nextLine();
            
            System.out.println("Enregistrer la saisie ? (oui/non)");
            String confirmation = scanner.next();

            if (confirmation.equalsIgnoreCase("oui")) {
                insertJoueur(nom, prenom, dateNaissance);
                System.out.println("Joueur enregistré");
            } else {
                System.out.println("Enregistrement annulé");
            }

        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }
    
    // méthode permettant la création d'une équipe
    public void createEquipe(String nomEquipe, int idJoueur1, int idJoueur2, int idJoueur3, int idJoueur4) {
        try {
            String insertSQL = "INSERT INTO Equipe (nomEquipe, idJoueur1, idJoueur2, idJoueur3, idJoueur4) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertSQL);

            preparedStatement.setString(1, nomEquipe);
            preparedStatement.setInt(2, idJoueur1);
            preparedStatement.setInt(3, idJoueur2);
            preparedStatement.setInt(4, idJoueur3);
            preparedStatement.setInt(5, idJoueur4);

            preparedStatement.executeUpdate();
            System.out.println("Équipe créée !");
        } catch (SQLException e) {
            System.err.println("Erreur de création d'équipe : " + e.getMessage());
        }
    }
    
    public void createCompetition(int annee, Date dateDebut, Date dateFin) {
        try {
            // select last id
            String sql = "SELECT MAX(idCompetition) FROM competition";
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            
            int lastID = 0;
            if (resultSet.next()) {
            	lastID = resultSet.getInt(1);
            }

            // auto incrémentation du nom de la compétition
            String newRISK = "RISK" + (lastID + 1);

            // insert de la nouvelle compétition dans la table compétition
            String insertSQL = "INSERT INTO Competition (nom, annee, dateDebut, dateFin) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertSQL);

            preparedStatement.setString(1, newRISK);
            preparedStatement.setInt(2, annee);
            preparedStatement.setDate(3, dateDebut);
            preparedStatement.setDate(4, dateFin);

            preparedStatement.executeUpdate();

            System.out.println("Compétition créée : " + newRISK);
        } catch (SQLException e) {
            System.err.println("Erreur de création de la compétition : " + e.getMessage());
        }
    }

    // méthode permettant de fermer la connexion
    public void closeConnexion() {
        try {
            if (conn != null) {
                conn.close();
                System.out.println("Fin connexion");
            }
        } catch (SQLException e) {
            System.err.println("Erreur de fin de connexion : " + e.getMessage());
        }
    }
    
    // vérifier si le joueur existe déja
    public boolean joueurExiste(String nom, String prenom) {
        try {
            String query = "SELECT COUNT(*) FROM joueur WHERE nom = ? AND prenom = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, nom);
            preparedStatement.setString(2, prenom);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de l'existence du joueur : " + e.getMessage());
        }

        return false;
    }
    
    
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/risk";
        String user = "root";
        String password = "";

        ConnexionDB dbRisk = new ConnexionDB(url, user, password);

        // insert values
        //dbRisk.insertJoueurByScanner();
        
        // display query
        dbRisk.readQuery("SELECT * FROM joueur");
        
        // creation d'équipe
        //dbRisk.createEquipe("Kikou", 1, 2, 3, 4);

        // creation de la compétition
        //dbRisk.createCompetition(2023, Date.valueOf("2023-11-01"), Date.valueOf("2023-11-30"));
        
        // display classement
        dbRisk.readQuery("select nom, prenom, score from joueur order by score DESC;");
        
        // close connexion
        dbRisk.closeConnexion();
    } 
}