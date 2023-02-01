import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        Globales.SOURCE = "txtTests/testConditionsBoucles.txt";
        AnalyseurLexical.initialiser();

        AnalyseurSyntaxique.AnalyseSyntaxique();
        GenerateurCode.Creer_Fichier_Code();

        MachineVirtuelle.Interpreter();
    }
}
