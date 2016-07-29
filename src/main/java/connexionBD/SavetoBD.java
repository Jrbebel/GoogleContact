package connexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by jrbebel on 20/07/16.
 */
public class SavetoBD {


    /**
     * @param pcnx
     * @param psTable
     * @param mapInsert
     * @return
     */
    public static int insert(Connection pcnx, String psTable, Map<String, String> mapInsert) {
        int liAffectes = -1;

        StringBuilder lsbSQL = new StringBuilder();
        StringBuilder lsbColonnes = new StringBuilder();
        StringBuilder lsbParametresValeurs = new StringBuilder();

        Set<String> nomsColonnes = mapInsert.keySet();
        Collection<String> valeursColonnes = mapInsert.values();

        for (String nomColonne : nomsColonnes) {
            lsbColonnes.append(nomColonne);
            lsbColonnes.append(",");

            lsbParametresValeurs.append("?,");
        }
        lsbColonnes.deleteCharAt(lsbColonnes.length() - 1);
        lsbParametresValeurs.deleteCharAt(lsbParametresValeurs.length() - 1);

        /*
         Exemple :
         INSERT INTO villes(cp,nom_ville,site,photo,id_pays) VALUES(?,?,?,?,?)
         */
        lsbSQL.append("INSERT INTO ");
        lsbSQL.append(psTable);
        lsbSQL.append("(");
        lsbSQL.append(lsbColonnes);
        lsbSQL.append(")");
        lsbSQL.append(" VALUES");
        lsbSQL.append("(");
        lsbSQL.append(lsbParametresValeurs);
        lsbSQL.append(")");

//        System.out.println(lsbSQL);
        PreparedStatement pst;
        try {
            pst = pcnx.prepareStatement(lsbSQL.toString());
            int i = 1;
            for (String valeurColonne : valeursColonnes) {
                pst.setString(i, valeurColonne);
                i++;
            }

            liAffectes = pst.executeUpdate();
            pst.close();
        } catch (SQLException e) {
            System.out.println("message" + e.getMessage());
//            System.out.println("Erreur INSERT : " + e.getMessage());
        }

        return liAffectes;
    } /// insert

    public static void main(String[] args) throws ClassNotFoundException {

        Connection Cnx = new SavetoBD().MyConnected();
        Map<String, String> map = new HashMap<String, String>();
        map.put("email", "jeanraynal.bebel@gmail.com");

        String psTable = "email";
        insert(Cnx, psTable, map);
        Connexion.valider();
        Connexion.seDeconnecter();
    }

    public Connection MyConnected() throws ClassNotFoundException {

        /**
         * On prepare la connexion a partir de la classe SavetoBD*
         */
        String asServeur = "";
        String asPort = "3306";
        String asBD = "";
        String asUser = "";
        String asPassword = "";

        Connection Cnx = Connexion.seConnecter(asServeur, asPort, asBD, asUser, asPassword);

        System.out.println("Connecction " + Cnx);

        return Cnx;
    }

}
