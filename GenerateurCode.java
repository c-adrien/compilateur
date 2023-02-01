import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;

public class GenerateurCode {
    private static final ArrayList<String> output = new ArrayList<>();

    private static final Stack<TUnilex> operatorsStack = new Stack<>();

    private static final Stack<Integer> jumpsStack = new Stack<>();

    private static int ajouterPCode(Object var) {
        MachineVirtuelle.P_CODE[MachineVirtuelle.CO] = var;
        MachineVirtuelle.CO++;
        return MachineVirtuelle.CO - 1;
    }

    //==============================================================

    public static void genAffectationDebut(String variable) {
        output.add("EMPI " + TableIdentificateur.chercher(variable).getValeur());

        ajouterPCode("EMPI");
        ajouterPCode(TableIdentificateur.chercher(variable).getValeur());
    }

    public static void genAffectationFin() {
        output.add("AFFE");
        ajouterPCode("AFFE");
    }



    public static void genLecture(String variable){
        output.add("EMPI " + TableIdentificateur.chercher(variable).getValeur());
        output.add("LIRE");

        ajouterPCode("EMPI");
        ajouterPCode(TableIdentificateur.chercher(variable).getValeur());
        ajouterPCode("LIRE");
    }

    public static void genEcritureNoExp(){
        output.add("ECRL");
        ajouterPCode("ECRL");
    }

    public static void genEcritureExp(){
        output.add("ECRE");
        ajouterPCode("ECRE");
    }

    public static void genEcr_ExpChaine(String chaine){
        char[] chars = chaine.toCharArray();

        StringBuilder sb = new StringBuilder();
        for (char ch : chars) {
            sb.append(String.format("'%c'", ch));
        }
        output.add("ECRC " + sb + " FINC");

        ajouterPCode("ECRC");
        ajouterPCode(chaine);
        ajouterPCode("FINC");
    }

    public static void genTerme_Entier(int entier){
        output.add("EMPI " + entier);
        ajouterPCode("EMPI");
        ajouterPCode(entier);
    }

    @SuppressWarnings("unchecked cast")
    public static void genTerme_Ident(String ident){
        Identificateur<Integer> identificateur = (Identificateur<Integer>) TableIdentificateur.chercher(ident);

        output.add("EMPI " + identificateur.getValeur());

        ajouterPCode("EMPI");
        ajouterPCode(identificateur.getValeur());

        if(identificateur.getTypeVariable() == TypeVariable.VARIABLE){
            output.add("CONT");
            ajouterPCode("CONT");
        }
    }

    public static void genTerme_Moins(){
        output.add("MOIN");
        ajouterPCode("MOIN");
    }

    public static void genStoreOP_BIN(TUnilex op){
        operatorsStack.push(op);
    }

    public static void genOP_BIN(){
        TUnilex op;
        try {
            while ((op = operatorsStack.pop()) != null){
                switch (op){
                    case divi -> {
                        output.add("DIVI");
                        ajouterPCode("DIVI");
                    }
                    case plus -> {
                        output.add("ADDI");
                        ajouterPCode("ADDI");
                    }
                    case moins -> {
                        output.add("SOUS");
                        ajouterPCode("SOUS");
                    }
                    case mult -> {
                        output.add("MULT");
                        ajouterPCode("MULT");
                    }
                }
            }
        } catch (EmptyStackException ignored){}
    }


    public static void genStop(){
        output.add("STOP");
        ajouterPCode("STOP");
    }

    public static void genInst_Cond_Debut() {
        jumpsStack.push(output.size());
        ajouterPCode("ALSN");
        jumpsStack.push(ajouterPCode(" "));
    }

    public static void genInst_Cond_Milieu() {
        MachineVirtuelle.P_CODE[jumpsStack.pop()] = ajouterPCode("ALLE") + 2;

        output.add(jumpsStack.pop(), "ALSN " + (output.size() + 4));
        jumpsStack.push(output.size());

        jumpsStack.push(ajouterPCode(" "));
    }

    public static void genInst_Cond_Fin() {
        MachineVirtuelle.P_CODE[jumpsStack.pop()] = MachineVirtuelle.CO;

        output.add(jumpsStack.pop(), "ALLE " + (output.size() + 3));
    }

    public static void genInst_Repe_Debut() {
        jumpsStack.push(output.size()); // destination ALLE du fichier code
        jumpsStack.push(MachineVirtuelle.CO); // destination ALLE de la VM
    }

    public static void genInst_Repe_Milieu() {
        jumpsStack.push(output.size()); // position ALSN du fichier code
        ajouterPCode("ALSN");
        jumpsStack.push(ajouterPCode(" ")); // position op ALSN de la VM
    }

    public static void genInst_Repe_Fin() {
        // donner la fin à ALSN
        MachineVirtuelle.P_CODE[jumpsStack.pop()] = MachineVirtuelle.CO + 2;
        output.add(jumpsStack.pop(), "ALSN " + (output.size() + 4));
        // produire ALLE et lui donner le début
        ajouterPCode("ALLE");
        ajouterPCode(jumpsStack.pop());
        output.add("ALLE " + (jumpsStack.pop() + 2));
    }

    //==============================================================

    public static void Creer_Fichier_Code(){
        String[] filename = Globales.SOURCE.split("\\.");
        filename[filename.length-1] = "cod";

        File file = new File(String.join(".", filename));
        try {
            Files.writeString(file.toPath(), Creer_Sortie());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static String Creer_Sortie(){
        // System.out.println("\n=== Code généré ===");
        // System.out.println(String.join("\n", output) + "\n");
        String header = String.format("%d mot(s) réservé(s) pour les variables globales",
                Globales.DERNIERE_ADRESSE_VAR_GLOB+1);

        return header + "\n" + String.join("\n", output);
    }


}
