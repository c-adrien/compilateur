import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static java.lang.System.exit;

public class MachineVirtuelle {

    public static ArrayList<Integer> VARIABLES_GLOBALES = new ArrayList<>();
    public static Object[] P_CODE = new Object[1000];

    public static Object[] PILEX = new Object[1000];
    // public static Object[] PILOP = new Object[1000];

    public static int CO = 0;
    private static int SOM_PILEX;

    //==============================================================

    public static void Interpreter(){
        CO = 0;
        SOM_PILEX = 0;
        //System.out.println(Arrays.asList(P_CODE));

        int vingt = 0;
        while(!P_CODE[CO].equals("STOP")){
            if ("ADDI".equals(P_CODE[CO])) {
                InterpreterADDI();
            } else if ("SOUS".equals(P_CODE[CO])) {
                InterpreterSOUS();
            } else if ("MULT".equals(P_CODE[CO])) {
                InterpreterMULT();
            } else if ("DIVI".equals(P_CODE[CO])) {
                InterpreterDIVI();
            } else if ("MOIN".equals(P_CODE[CO])) {
                InterpreterMOIN();
            } else if ("AFFE".equals(P_CODE[CO])) {
                InterpreterAFFE();
            } else if ("LIRE".equals(P_CODE[CO])) {
                InterpreterLIRE();
            } else if ("ECRL".equals(P_CODE[CO])) {
                InterpreterECRL();
            } else if ("ECRE".equals(P_CODE[CO])) {
                InterpreterECRE();
            } else if ("ECRC".equals(P_CODE[CO])) {
                InterpreterECRC();
            } else if ("EMPI".equals(P_CODE[CO])) {
                InterpreterEMPI();
            } else if ("CONT".equals(P_CODE[CO])) {
                InterpreterCONT();
            } else if ("ALLE".equals(P_CODE[CO])) {
                InterpreterALLE();
            } else if ("ALSN".equals(P_CODE[CO])) {
                InterpreterALSN();
            }
        }
    }

    public static void InterpreterADDI(){
        PILEX[SOM_PILEX-1] = (Integer) PILEX[SOM_PILEX-1] + (Integer) PILEX[SOM_PILEX];
        SOM_PILEX--;
        CO++;
    }

    public static void InterpreterSOUS(){
        PILEX[SOM_PILEX-1] = (Integer) PILEX[SOM_PILEX-1] - (Integer) PILEX[SOM_PILEX];
        SOM_PILEX--;
        CO++;
    }

    public static void InterpreterMULT(){
        PILEX[SOM_PILEX-1] = (Integer) PILEX[SOM_PILEX-1] * (Integer) PILEX[SOM_PILEX];
        SOM_PILEX--;
        CO++;
    }

    public static void InterpreterDIVI(){
        if((Integer) PILEX[SOM_PILEX] == 0){
            System.out.println("Division par 0 interdite");
            exit(0);
        }

        PILEX[SOM_PILEX-1] = (Integer) PILEX[SOM_PILEX-1] / (Integer) PILEX[SOM_PILEX];
        SOM_PILEX--;
        CO++;
    }

    public static void InterpreterMOIN(){
        PILEX[SOM_PILEX] = - (Integer) PILEX[SOM_PILEX];
        CO++;
    }

    public static void InterpreterAFFE(){
        VARIABLES_GLOBALES.set((Integer) PILEX[SOM_PILEX-1], (Integer) PILEX[SOM_PILEX]);
        SOM_PILEX -= 2;
        CO++;
    }

    public static void InterpreterLIRE(){
        try {
            System.out.println("Lecture :");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            Integer nb = Integer.parseInt(bufferedReader.readLine());

            VARIABLES_GLOBALES.set((Integer) PILEX[SOM_PILEX], nb);
            SOM_PILEX--;
            CO++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void InterpreterECRL(){
        System.out.println();
        CO++;
    }

    public static void InterpreterECRE(){
        System.out.println(PILEX[SOM_PILEX]);
        SOM_PILEX--;
        CO++;
    }

    public static void InterpreterECRC(){
        CO++;
        System.out.println(P_CODE[CO]);
        CO += 2;
    }

    public static void InterpreterEMPI(){
        SOM_PILEX++;
        PILEX[SOM_PILEX] = P_CODE[CO+1];
        CO += 2;
    }

    public static void InterpreterCONT(){
        PILEX[SOM_PILEX] = VARIABLES_GLOBALES.get((Integer) PILEX[SOM_PILEX]);
        CO++;
    }

    public static void InterpreterALLE() {
        CO = (int) P_CODE[CO+1];
    }

    public static void InterpreterALSN() {
        if (Objects.equals(PILEX[SOM_PILEX], 0)) {
            CO = (int) P_CODE[CO+1];
        }
        else {
            CO += 2;
        }
        SOM_PILEX--;
    }
}
