public class Identificateur<T>{

    private TypeVariable typeVariable;
    private T valeur;

    //==============================================================

    public void setTypeVariable(TypeVariable typeVariable) {
        this.typeVariable = typeVariable;
    }

    public void setValeur(T valeur) {
        this.valeur = valeur;
    }

    public TypeVariable getTypeVariable() {
        return typeVariable;
    }

    public T getValeur() {
        return valeur;
    }
}
