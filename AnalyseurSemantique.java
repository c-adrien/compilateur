public class AnalyseurSemantique {

    /**
     * Procédure Erreur, print le code d'erreur
     */
    public static void Erreur(int errorCode, String errorString){
        String errorMessage = switch (errorCode) {
            case 1 -> "la déclaration des constantes";
            case 2 -> "la déclaration des variables";
            case 3 -> "la déclaration des identificateurs";
            case 4 -> "le type de variable utilisé";
            default -> "Autre erreur sémantique";
        };

        Globales.DERNIERE_ERREUR = String.format("Erreur sémantique dans %s : %s, ligne %d\n",
                errorMessage, errorString, Globales.NUM_LIGNE);
    }

    //==============================================================

    public static boolean Definir_Constante(String nom, TUnilex unilex){

        if(TableIdentificateur.chercher(nom) != null){
            return false;
        }

        if(unilex == TUnilex.ent){
            TableIdentificateur.inserer(nom, TypeVariable.CONSTANTE, Globales.NOMBRE);
        }
        else {
            TableIdentificateur.inserer(nom, TypeVariable.CONSTANTE, Globales.CHAINE);
            Globales.NB_CONST_CHAINE++;
        }

        return true;
    }

    public static boolean Definir_Variable(String nom){
        Globales.DERNIERE_ADRESSE_VAR_GLOB++;
        if (TableIdentificateur.inserer(nom, TypeVariable.VARIABLE, Globales.DERNIERE_ADRESSE_VAR_GLOB)) {
            MachineVirtuelle.VARIABLES_GLOBALES.add(null);
            return true;
        }
        return false;
    }

    //==============================================================

    public static boolean Variable_Et_Declaree(String nom){
        Identificateur<?> identificateur = TableIdentificateur.chercher(nom);

        if(identificateur != null){
            if(identificateur.getTypeVariable() == TypeVariable.VARIABLE){
                return true;
            }
            else {
                AnalyseurSemantique.Erreur(4, "variable attendue");
                return false;
            }
        }
        else {
            AnalyseurSemantique.Erreur(2, "variable non déclarée");
            return false;
        }
    }

    public static boolean Ident_Declare_Et_Entier(String nom){
        Identificateur<?> identificateur = TableIdentificateur.chercher(nom);
        if(identificateur != null){

            if(identificateur.getValeur() instanceof Integer){
                return true;
            }
            AnalyseurSemantique.Erreur(4, "identificateur doit être un entier");
        }
        else {
            AnalyseurSemantique.Erreur(3, "identificateur non déclarée");
        }
        return false;
    }

    public static boolean Ident_Declare(String nom){
        Identificateur<?> identificateur = TableIdentificateur.chercher(nom);
        if(identificateur != null){
            return true;
        }
        else {
            AnalyseurSemantique.Erreur(3, "identificateur non déclarée");
        }
        return false;
    }

}
