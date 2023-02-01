import java.util.Arrays;
import java.util.List;

public class Constantes {

    public static final int LONG_MAX_INDENT = 20;
    public static final int LONG_MAX_CHAINE = 50;
    public static final int NB_MOTS_RESERVES = 12;

    // Séparateurs : \space (unicode 32), \tab (unicode 9), \n (unicode 10, carriage return 13)
    public static final List<Integer> SEPARATORS_UNICODE = Arrays.asList(9, 10, 13, 32);

    public static final List<Character> SYMBOLES = Arrays.asList(
            ',',';','.','=','+','-','*','/','(',')','<','>',':');
}
