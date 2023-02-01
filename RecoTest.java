import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RecoTest {

    @Test
    public void testEntier() {
        Globales.SOURCE = "txtTests/testEntier.txt";
        AnalyseurLexical.Lire_Car();
        AnalyseurLexical.Reco_Entier();
        int expected = 1234;
        assertEquals(expected, Globales.NOMBRE);
    }
    @Test
    public void testChaine1() {
        Globales.SOURCE = "txtTests/testChaine1.txt";
        AnalyseurLexical.Lire_Car();
        AnalyseurLexical.Reco_Chaine();
        String expected = "nfgnerne";
        assertEquals(expected, Globales.CHAINE);
    }
    @Test
    public void testChaine2() {
        Globales.SOURCE = "txtTests/testChaine2.txt";
        AnalyseurLexical.Lire_Car();
        AnalyseurLexical.Reco_Chaine();
        String expected = "L'adresse";
        assertEquals(expected, Globales.CHAINE);
    }
    @Test
    public void testChaine3() {
        Globales.SOURCE = "txtTests/testChaine3.txt";
        AnalyseurLexical.Lire_Car();
        AnalyseurLexical.Reco_Chaine();
        String expected = "'";
        assertEquals(expected, Globales.CHAINE);
    }
}
