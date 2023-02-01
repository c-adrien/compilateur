import org.junit.jupiter.api.Test;

public class TestIdentificateur {

    @Test
    public void test1(){
        for (int i = 0; i < 100; i++) {
            TableIdentificateur.inserer(String.format("testIdent%d", i));
        }

        TableIdentificateur.Affiche_Table_Ident();
    }
}
