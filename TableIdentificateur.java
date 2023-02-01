import java.util.Map;
import java.util.TreeMap;

public class TableIdentificateur {

    private static final TreeMap<String, Identificateur<?>> table = new TreeMap<>();

    public static Identificateur<?> chercher(String nomIdentificateur){
        return table.get(nomIdentificateur);
    }

    //==============================================================

    // Test inserer
    public static boolean inserer(String nomIdentificateur){
        if(table.containsKey(nomIdentificateur)){
            System.out.println("Identificateur déjà présent dans la table");
            return false;
        }

        Identificateur<?> identificateur = new Identificateur<>();
        table.put(nomIdentificateur, identificateur);
        return true;
    }

    // Pour type CONST/VAR, integer
    public static boolean inserer(String nomIdentificateur, TypeVariable typeVariable, int valeur){
        if(table.containsKey(nomIdentificateur)){
            System.out.println("Identificateur déjà présent dans la table");
            return false;
        }

        Identificateur<Integer> identificateur = new Identificateur<>();
        identificateur.setTypeVariable(typeVariable);
        identificateur.setValeur(valeur);

        table.put(nomIdentificateur, identificateur);
        Affiche_Table_Ident();
        return true;
    }

    // Pour type CONST, string
    public static boolean inserer(String nomIdentificateur, TypeVariable typeVariable, String valeur){
        if(table.containsKey(nomIdentificateur)){
            System.out.println("Identificateur déjà présent dans la table");
            return false;
        }

        Identificateur<String> identificateur = new Identificateur<>();
        identificateur.setTypeVariable(typeVariable);
        identificateur.setValeur(valeur);

        table.put(nomIdentificateur, identificateur);
        Affiche_Table_Ident();
        return true;
    }

    public static void Affiche_Table_Ident(){
        System.out.println("\nContenu de la table d'identificateurs : ");
        System.out.println("--------------------------------------- ");

        for(Map.Entry<String, Identificateur<?>> pair : table.entrySet()){
            System.out.println(pair.getKey() + " :\t type = " +
                    pair.getValue().getTypeVariable() + "\tvaleur = "+ pair.getValue().getValeur());
        }
        System.out.println();
    }

}
