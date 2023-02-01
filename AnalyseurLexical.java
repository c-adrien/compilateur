import java.io.*;
import java.util.Locale;

import static java.lang.System.exit;

public class AnalyseurLexical {

    private static BufferedReader reader = null;
    static {
        try {
            reader = new BufferedReader(new FileReader(Globales.SOURCE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //==============================================================

    /**
     * Procédure Erreur, print le code d'erreur et quitte
     */
    public static void Erreur(int errorCode){
        String errorMessage = switch (errorCode) {
            case 1 -> "Fin de fichier atteinte";
            case 2 -> "Entier trop large";
            case 3 -> "Chaine de caractères trop longue";
            default -> "Autre erreur";
        };

        System.out.printf("Erreur %d : %s, ligne %d\n",
                errorCode, errorMessage, Globales.NUM_LIGNE);

        terminer();
        if (errorCode == 1) throw new NoSuchFieldError();
        exit(0);
    }

    /**
     * Lit un caractère et gère les variables globales
     */
    public static int Lire_Car() {
        try {
            int read = reader.read();

            // EOF
            if(read == -1){
                Erreur(1);

            }
            // New line
            if(read == 10){
                Globales.NUM_LIGNE++;
            }

            Globales.CARLU = (char) read;
            return read;
        }
        catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Séparateurs : \space (unicode 32), \tab (unicode 9), \n (unicode 10, carriage return 13)
     * Commentaire : '{' (unicode 123), '}' (unicode 125)
     */
    public static void Sauter_Separateurs() {

        // Sauter commentaires
        if (Globales.CARLU == '{') {
            // Tant que '}' non lu
            while ((char) Lire_Car() != '}') {
                if (Globales.CARLU == '{') {
                    Sauter_Separateurs();
                }
            }
            Lire_Car();
        }

        // Sauter séparateurs
        else {
            while (Constantes.SEPARATORS_UNICODE.contains((int) Globales.CARLU)) {
                Lire_Car();
                if (Globales.CARLU == '{') {
                    Sauter_Separateurs();
                }
            }
        }
    }

    /**
     * Lit un entier
     */
    public static TUnilex Reco_Entier(){
        StringBuilder entierAReco = new StringBuilder();
        entierAReco.append(Globales.CARLU);
        Lire_Car();
        while (isInteger(entierAReco.append(Globales.CARLU).toString())) {
            Lire_Car();
        }
        entierAReco.deleteCharAt(entierAReco.length() - 1);
        long nombreLong = Long.parseLong(entierAReco.toString());
        if (nombreLong > Integer.MAX_VALUE ) {
            Erreur(2);
        }
        Globales.NOMBRE = (int) nombreLong;

        System.out.println("Reco_Entier : " + Globales.NOMBRE);

        return TUnilex.ent;
    }
    private static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }


    /**
     * Lit une chaîne de charactères
     */
    public static TUnilex Reco_Chaine(){
        StringBuilder chaineAReco = new StringBuilder();
        chaineAReco = boucleChaine(chaineAReco);
        Globales.CHAINE = chaineAReco.toString();
        System.out.println("Reco_Chaine : " + Globales.CHAINE);
        return TUnilex.ch;
    }
    private static StringBuilder boucleChaine(StringBuilder chaine) {
        Lire_Car();
        while (Globales.CARLU != '\'') {
            chaine.append(Globales.CARLU);
            Lire_Car();
        }
        chaine = testFinDeChaine(chaine);
        return chaine;
    }
    private static StringBuilder testFinDeChaine(StringBuilder chaine) {
        Lire_Car();
        if (Globales.CARLU == '\'') {
            chaine.append(Globales.CARLU);
            chaine = boucleChaine(chaine);
        }
        return chaine;
    }

    /**
     * Détermine si le mot est un identificateur ou un mot-clé
     */
    public static TUnilex Reco_Ident_Ou_Mot_Reserve(){

        Globales.CHAINE = String.valueOf(Globales.CARLU);

        for(;;){
            int read = Lire_Car();

            // Si char lettre, chiffre, ou '_'
            if ((48 <= read && read <= 57) || (65 <= read && read <= 90)
                    || (97 <= read && read <= 122) || read == 95) {
                if(Globales.CHAINE.length() < Constantes.LONG_MAX_INDENT){
                    Globales.CHAINE += Globales.CARLU;
                }
            }
            else {
                break;
            }
        }

        // Trim pour gérer un espace avant/après
        Globales.CHAINE = Globales.CHAINE.toUpperCase(Locale.ROOT).trim();

        System.out.println("Reco_Ident_Ou_Mot_Reserve : " + Globales.CHAINE);

        return Est_Mot_Reserve() ? TUnilex.motcle : TUnilex.ident;
    }

    private static boolean Est_Mot_Reserve(){
        for (String motReserve: Globales.TABLE_MOTS_RESERVES) {
            if (motReserve.equals(Globales.CHAINE)) return true;
        }
        return false;
    }

    /**
     * Reconnait les symboles
     * @return Type du symbole reconnu
     */
    public static TUnilex Reco_Symb(){

        char[] tmpChars = new char[2];

        tmpChars[0] = Globales.CARLU;
        Lire_Car();
        tmpChars[1] = Globales.CARLU;

        System.out.println("Reco_Symbole : " + tmpChars[0]);
        System.out.println("Symbole suivant : " + tmpChars[1]);

        // Symboles simples
        switch (tmpChars[0]) {
            case ',' : return TUnilex.virg;
            case ';' : return TUnilex.ptvirg;
            case '.' : return TUnilex.point;
            case '=' : return TUnilex.eg;
            case '+' : return TUnilex.plus;
            case '-' : return TUnilex.moins;
            case '*' : return TUnilex.mult;
            case '/' : return TUnilex.divi;
            case '(' : return TUnilex.parouv;
            case ')' : return TUnilex.parfer;
            default : Lire_Car();
        }

        // Symboles composés
        switch (tmpChars[0]){
            case '<' :
                if (tmpChars[1] == '=') return TUnilex.infe;
                if (tmpChars[1] == '>') return TUnilex.diff;
                return TUnilex.inf;
            case '>' :
                if (tmpChars[1] == '=') return TUnilex.supe;
                return TUnilex.sup;
            case ':' :
                if (tmpChars[1] == '=') return TUnilex.aff;
                return TUnilex.deuxpts;
            default :
                return null;
        }
    }

    public static TUnilex Analex() {

        Sauter_Separateurs();

        int character = Globales.CARLU;

        if (Constantes.SYMBOLES.contains((char) character)){
            return Reco_Symb();
        }

        if(48 <= character && character <= 57){
            return Reco_Entier();
        }

        if((char) character == '\''){
            return Reco_Chaine();
        }

        return Reco_Ident_Ou_Mot_Reserve();
    }

    //==============================================================

    public static void initialiser() {
        Globales.NUM_LIGNE = 1;
        Globales.TABLE_MOTS_RESERVES = new String[]{"PROGRAMME",
            "DEBUT", "FIN", "CONST", "VAR", "ECRIRE", "LIRE",
            "SI", "ALORS", "SINON", "TANTQUE", "FAIRE"};
        Globales.DERNIERE_ADRESSE_VAR_GLOB = -1;
    }

    public static void terminer(){
        try {
            reader.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
