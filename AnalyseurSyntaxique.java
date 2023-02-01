import java.util.Objects;

import static java.lang.System.exit;

public class AnalyseurSyntaxique {

    // Idée pour améliorer Instruction()
    private static boolean fatal_error;

    /**
     * Procédure Erreur, print le code d'erreur
     */
    public static void Erreur(int errorCode, String expectedString){
        String errorMessage = switch (errorCode) {
            case 1 -> "d'affectation";
            case 2 -> "de lecture";
            case 3 -> "d'écriture";
            case 4 -> "de PROG";
            case 5 -> "de déclaration de constante";
            case 6 -> "de déclaration de variable";
            case 7 -> "de BLOC";
            case 8 -> "d'opérateur binaire";
            case 9 -> "de terme";
            case 10 -> "de suite de terme";
            case 11 -> "d'instruction conditionnelle";
            case 12 -> "d'instruction";
            case 13 -> "d'instruction non conditionnelle";
            case 14 -> "d'instruction répétitive";
            default -> "Autre erreur syntaxique";
        };

        Globales.DERNIERE_ERREUR = String.format("Erreur syntaxique dans une instruction %s : %s, ligne %d\n",
                errorMessage, expectedString, Globales.NUM_LIGNE);
    }

    //==============================================================

    public static boolean Prog(){
        if(Globales.UNILEX == TUnilex.motcle && Objects.equals(Globales.CHAINE, "PROGRAMME")){
            updateUnilex();

            if(Globales.UNILEX == TUnilex.ident){
                updateUnilex();

                if(Globales.UNILEX == TUnilex.ptvirg){
                    updateUnilex();

                    if(Globales.UNILEX == TUnilex.motcle && Objects.equals(Globales.CHAINE, "CONST")){
                        if(!Decl_Const()){
                            return false;
                        }
                        updateUnilex();
                    }

                    if(Globales.UNILEX == TUnilex.motcle && Objects.equals(Globales.CHAINE, "VAR")){
                        if(!Decl_Var()){
                            return false;
                        }
                        updateUnilex();
                    }

                    if(Bloc()){
                        GenerateurCode.genStop();
                        return true;
                    }
                    return false;
                }
                else {
                    Erreur(4, "; attendu");
                    return false;
                }
            }
            else {
                Erreur(4, "identificateur attendu");
                return false;
            }
        }

        else {
            Erreur(4, "mot-clé PROGRAMME attendu");
            return false;
        }
    }

    private static boolean Decl_Const_Helper(){
        String nomConstante;

        if(Globales.UNILEX == TUnilex.ident){

            nomConstante = Globales.CHAINE;
            updateUnilex();

            if(Globales.UNILEX == TUnilex.aff){
                updateUnilex();

                if(Globales.UNILEX == TUnilex.ent || Globales.UNILEX == TUnilex.ch){
                    // Ajout analyse sémantique
                    if(AnalyseurSemantique.Definir_Constante(nomConstante, Globales.UNILEX)){
                        updateUnilex();
                        return true;
                    }
                    else {
                        AnalyseurSemantique.Erreur(1, "identificateur déjà déclaré");
                        return false;
                    }
                }
                else {
                    Erreur(5, "chaine de caractères ou entier attendu");
                    return false;
                }
            }
            else {
                Erreur(5, ":= attendu");
                return false;
            }
        }
        else {
            Erreur(5, "identificateur attendu");
            return false;
        }
    }

    public static boolean Decl_Const(){
        boolean end, error;

        // MOT CLE
        if(Globales.UNILEX == TUnilex.motcle && Objects.equals(Globales.CHAINE, "CONST")){
            updateUnilex();

            // si ident := (CH|ENT)
            if(Decl_Const_Helper()){
                end = false;
                error = false;

                while(!end){
                    // si virgule, on continue
                    if(Globales.UNILEX == TUnilex.virg){
                        updateUnilex();

                        // si erreur dans ident := (CH|ENT), stop
                        error = !Decl_Const_Helper();
                        if(error) end = true;
                    }
                    else {
                        end = true;
                    }
                }

                if(error){
                    return false;
                }

                // updateUnilex();

                if (Globales.UNILEX == TUnilex.ptvirg){
                    return true;
                }

                else {
                    Erreur(5, "; attendu");
                    return false;
                }
            }
            else {
                return false;
            }
        }
        else {
            // Erreur(5, "mot-clé CONST attendu");
            return false;
        }
    }

    public static boolean Decl_Var(){
        boolean end, error, error_decl_var;

        // MOT CLE
        if(Globales.UNILEX == TUnilex.motcle && Objects.equals(Globales.CHAINE, "VAR")) {
            updateUnilex();

            if(Globales.UNILEX == TUnilex.ident){
                if(AnalyseurSemantique.Definir_Variable(Globales.CHAINE)){
                    updateUnilex();

                    end = false;
                    error = false;
                    error_decl_var = false;

                    if(Globales.UNILEX == TUnilex.ptvirg){
                        return true;
                    }

                    while(!end){
                        // si virgule, on continue
                        if(Globales.UNILEX == TUnilex.virg){
                            updateUnilex();

                            if(Globales.UNILEX != TUnilex.ident){
                                error = true;
                                end = true;
                            }
                            else if(!error_decl_var){
                                error_decl_var = !AnalyseurSemantique.Definir_Variable(Globales.CHAINE);
                            }
                        }
                        else {
                            end = true;
                        }
                    }

                    if(error){
                        Erreur(6, "identificateur attendu");
                        return false;
                    }

                    if(error_decl_var){
                        AnalyseurSemantique.Erreur(2, "identificateur déjà déclaré");
                        return false;
                    }

                    updateUnilex();

                    if(Globales.UNILEX == TUnilex.ptvirg){
                        return true;
                    }

                    else {
                        Erreur(6, "; attendu");
                        return false;
                    }
                }

                else {
                    AnalyseurSemantique.Erreur(2, "identificateur déjà déclaré");
                    return false;
                }
            }

            else {
                Erreur(6, "identificateur attendu");
                return false;
            }
        }
        else {
            // Erreur(6, "mot-clé VAR attendu");
            return false;

        }
    }

    public static boolean Bloc(){
        boolean end;

        // MOT CLE
        if(Globales.UNILEX == TUnilex.motcle && Objects.equals(Globales.CHAINE, "DEBUT")) {
            updateUnilex();

            if(!Instruction()) return false;

            end = false;
            while(!end){
                if(Globales.UNILEX == TUnilex.ptvirg){
                    updateUnilex();

                    if (!Instruction()) return false;
                }
                else {
                    end = true;
                }
            }

            // updateUnilex();

            if(Globales.UNILEX == TUnilex.motcle && Objects.equals(Globales.CHAINE, "FIN")){
                updateUnilex();
                return true;
            }

            else {
                Erreur(7, "mot-clé FIN attendu");
                return false;
            }
        }

        else {
            // Erreur(7, "mot-clé DEBUT attendu");
            return false;
        }
    }

    public static boolean Instruction() {
        return (Inst_Non_Cond() || Inst_Cond());
        // Erreur(12, "Instruction conditionnelle ou non conditionnelle attendue");
    }

    public static boolean Inst_Non_Cond(){
        return (Bloc()|| Affectation() || Lecture() || Ecriture() || Inst_Repe());
        // Erreur(13, "Affectation, Lecture, Ecriture, Bloc ou Instruction répétitive attendue");
    }

    public static boolean Inst_Cond() {
        if (Globales.UNILEX == TUnilex.motcle && Objects.equals(Globales.CHAINE, "SI")) {
            updateUnilex();

            if (!Exp()) return false;

            if (Globales.UNILEX == TUnilex.motcle && Objects.equals(Globales.CHAINE, "ALORS")) {
                GenerateurCode.genInst_Cond_Debut();
                updateUnilex();
                if (Inst_Cond())  {
                    GenerateurCode.genInst_Cond_Milieu();
                    GenerateurCode.genInst_Cond_Fin();
                    return true;
                }

                if (Inst_Non_Cond()) {
                    GenerateurCode.genInst_Cond_Milieu();
                    boolean end = false;
                    while (!end) {
                        if (Globales.UNILEX == TUnilex.motcle && Objects.equals(Globales.CHAINE, "SINON")) {
                            updateUnilex();
                            if (!Instruction()) {
                                Erreur(11, "instruction après le SINON incorrecte");
                                return false;
                            }
                        }
                        else {
                            end = true;
                        }
                    }
                    GenerateurCode.genInst_Cond_Fin();
                    return true;
                }
                Erreur(11, "instruction après le ALORS incorrecte");
                return false;
            }
            Erreur(11, "mot-clé ALORS attendu");
            return false;
        }
        return false;
    }

    public static boolean Inst_Repe() {
        if (Globales.UNILEX == TUnilex.motcle && Objects.equals(Globales.CHAINE, "TANTQUE")) {
            updateUnilex();

            GenerateurCode.genInst_Repe_Debut();

            if (!Exp()) return false;

            if (Globales.UNILEX == TUnilex.motcle && Objects.equals(Globales.CHAINE, "FAIRE")) {
                GenerateurCode.genInst_Repe_Milieu();
                updateUnilex();

                if (Instruction()) {
                    GenerateurCode.genInst_Repe_Fin();
                    return true;
                }
                return false;
            }
            Erreur(14, "mot-clé FAIRE attendu");
            return false;
        }
        return false;
    }

    public static boolean Affectation(){
        if(Globales.UNILEX == TUnilex.ident){
            if(AnalyseurSemantique.Variable_Et_Declaree(Globales.CHAINE)){
                GenerateurCode.genAffectationDebut(Globales.CHAINE);
                updateUnilex();
                    if(Globales.UNILEX == TUnilex.aff){
                        updateUnilex();
                        if(Exp()){
                            GenerateurCode.genAffectationFin();
                            return true;
                        }
                        else {
                            return false;
                        }
                    }
                    else {
                        Erreur(1, ":= attendu");
                        return false;
                    }
            }
            else {
                return false;
            }
        }

        else {
            // Erreur(1, "identificateur attendu");
            return false;
        }
    }

    public static boolean Lecture(){
        boolean end, error, error_ident;

        if(Globales.UNILEX == TUnilex.motcle && Objects.equals(Globales.CHAINE, "LIRE")){
            updateUnilex();

            if(Globales.UNILEX == TUnilex.parouv){
                updateUnilex();

                if(Globales.UNILEX == TUnilex.ident){
                    if(AnalyseurSemantique.Variable_Et_Declaree(Globales.CHAINE)){

                        // gen code
                        GenerateurCode.genLecture(Globales.CHAINE);

                        updateUnilex();
                        end = false;
                        error = false;
                        error_ident = false;

                        while(!end){
                            if(Globales.UNILEX == TUnilex.virg){
                                updateUnilex();

                                if(Globales.UNILEX == TUnilex.ident){
                                    if(AnalyseurSemantique.Variable_Et_Declaree(Globales.CHAINE)){
                                        // gen code
                                        GenerateurCode.genLecture(Globales.CHAINE);
                                        updateUnilex();
                                    }
                                    else {
                                        end = true;
                                        error = true;
                                        error_ident = true;
                                    }
                                }
                                else {
                                    end = true;
                                    error = true;
                                }
                            }
                            else {
                                end = true;
                            }
                        }

                        if(error_ident){
                            return false;
                        }

                        if(error){
                            Erreur(2, "identificateur attendu");
                            return false;
                        }

                        // updateUnilex();
                        if(Globales.UNILEX == TUnilex.parfer){
                            updateUnilex();
                            return true;
                        }
                        else {
                            Erreur(2, ") attendu");
                            return false;
                        }
                    }
                    else {
                        return false;
                    }
                }
                else {
                    Erreur(2, "identificateur attendu");
                    return false;
                }
            }
            else {
                Erreur(2, "( attendu");
                return false;
            }
        }
        else {
            // Erreur(2, "mot-clé LIRE attendu");
            return false;
        }
    }

    public static boolean Ecriture(){
        boolean end, error;

        if(Globales.UNILEX == TUnilex.motcle && Objects.equals(Globales.CHAINE, "ECRIRE")){
            updateUnilex();

            if(Globales.UNILEX == TUnilex.parouv){
                updateUnilex();
                error = false;

                if(Ecr_Exp()){ // si expression
                    if(Globales.UNILEX == TUnilex.parfer){
                        updateUnilex();
                        return true;
                    }
                    // updateUnilex();
                    end = false;

                    while(!end){
                        if(Globales.UNILEX == TUnilex.virg){
                            updateUnilex();
                            error = !Ecr_Exp();
                            if(error) end = true;
                        }
                        else {
                            end = true;
                        }
                    }
                }

                else { // pas d'expression
                    GenerateurCode.genEcritureNoExp();
                }

                if(error){
                    Erreur(3, "expression incorrecte");
                    return false;
                }

                if(Globales.UNILEX == TUnilex.parfer){
                    updateUnilex();
                    return true;
                }
                else {
                    Erreur(3, ") attendu");
                    return false;
                }
            }
            else {
                Erreur(3, "( attendu");
                return false;
            }
        }
        else {
            // Erreur(3, "mot-clé ECRIRE attendu");
            return false;
        }
    }

    public static boolean Ecr_Exp(){
        if(Exp()){
            GenerateurCode.genEcritureExp();
            return true;
        }
        if(Globales.UNILEX == TUnilex.ch){
            GenerateurCode.genEcr_ExpChaine(Globales.CHAINE);
            updateUnilex();
            return true;
        }
        return false;
    }

    public static boolean Exp(){
        return Exp(false);
    }

    public static boolean Exp(boolean mustBeInteger){
        if(Terme(mustBeInteger)){
            updateUnilex();

            return Suite_Terme();
        }
        else {
            return false;
        }
    }

    public static boolean Terme(){
        return Terme(false);
    }

    public static boolean Terme(boolean mustBeInteger){
        if(Globales.UNILEX == TUnilex.ent){
            GenerateurCode.genTerme_Entier(Globales.NOMBRE);
            return true;
        }

        if(Globales.UNILEX == TUnilex.ident){
            if(mustBeInteger){
                if(AnalyseurSemantique.Ident_Declare_Et_Entier(Globales.CHAINE)){
                    GenerateurCode.genTerme_Ident(Globales.CHAINE);
                    return true;
                }
                else {
                    return false;
                }
            }
            else {
                if(AnalyseurSemantique.Ident_Declare(Globales.CHAINE)){
                    GenerateurCode.genTerme_Ident(Globales.CHAINE);
                    return true;
                }
                else {
                    return false;
                }
            }
        }

        if(Globales.UNILEX == TUnilex.parouv){
            updateUnilex();

            if(Exp()){
                // updateUnilex();

                if(Globales.UNILEX == TUnilex.parfer) return true;
                else {
                    Erreur(9, ") attendu");
                    return false;
                }
            }
            else {
                Erreur(9, "expression attendue");
                return false;
            }
        }

        if(Globales.UNILEX == TUnilex.moins){
            updateUnilex();
            if(Terme()){
                GenerateurCode.genTerme_Moins();
                return true;
            }
            else {
                return false;
            }
        }

        Erreur(9, "terme attendu");
        return false;
    }

    public static boolean Suite_Terme(){
        if(Op_Bin()){
            updateUnilex();
            if(Exp(true)){
                GenerateurCode.genOP_BIN();
                return true;
            }
            else {
                // Erreur(10, "EXP attendu");
                return false;
            }
        }
        else {
            return true;
        }
    }

    public static boolean Op_Bin(){
        if(Globales.UNILEX == TUnilex.plus || Globales.UNILEX == TUnilex.moins
        || Globales.UNILEX == TUnilex.mult || Globales.UNILEX == TUnilex.divi){
            GenerateurCode.genStoreOP_BIN(Globales.UNILEX);
            return true;
        }

        else {
            Erreur(8, "opérateur attendu");
            return false;
        }
    }

    //==============================================================

    private static void updateUnilex() {
        try {
            Globales.UNILEX = AnalyseurLexical.Analex();
        }
        catch (NoSuchFieldError e){
            // System.out.println("Erreur syntaxique : fin de fichier atteinte, mot manquant");
            AnalyseurLexical.terminer();
            exit(0);
        }
    }

    public static void AnalyseSyntaxique(){
        updateUnilex();

        if(Prog()){
            System.out.println("Le programme est syntaxiquement & sémantiquement correct");
        }
        else {
            System.out.println(Globales.DERNIERE_ERREUR);
            AnalyseurLexical.terminer();
            exit(0);
        }
    }

}
