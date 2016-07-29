package connexionBD;

/**
 * Created by jrbebel on 20/07/16.
 *
 * @author jrbebel
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//import java.io.FileNotFoundException;

public class Connexion {


    /*
     Attributs
     */
    private static Connection icnx;

    public static Connection seConnecter(String asServeur, String asPort, String asBD, String asUser, String asPassword) throws ClassNotFoundException {
        try {

            String lsURL = "jdbc:mysql://" + asServeur + ":" + asPort + "/" + asBD + "";

            //Le pilote
            Class.forName("com.mysql.jdbc.Driver");

            // --- Test sur une connexion et un SELECT
            icnx = DriverManager.getConnection(lsURL, asUser, asPassword);
            icnx.setAutoCommit(false);

        } catch (SQLException ex) {
            // out.print(ex.getMessage());
            System.err.println(ex.getMessage());
        }
        return icnx;
    } /// seConnecter

    public static boolean seDeconnecter() {
        boolean lbOK = true;
        try {
            icnx.close();
        } catch (SQLException ex) {
            lbOK = false;
            System.err.println(ex.getMessage());
//            Logger.getLogger(Connexion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lbOK;
    } /// seDeconnecter

    /**
     * @return
     */
    public static boolean valider() {
        boolean lbOK = true;
        try {
            icnx.commit();
        } catch (SQLException ex) {
            System.out.println("Message" + ex.getMessage());
            lbOK = false;
            System.err.println(ex.getMessage());
//            Logger.getLogger(Connexion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lbOK;
    } /// valider

    /**
     * @return
     */
    public static boolean annuler() {
        boolean lbOK = true;
        try {
            icnx.rollback();
        } catch (SQLException ex) {
            lbOK = false;
            System.err.println(ex.getMessage());
//            Logger.getLogger(Connexion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lbOK;
    } /// annuler
}
