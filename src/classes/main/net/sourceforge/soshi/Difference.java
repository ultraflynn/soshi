package net.sourceforge.soshi;

public class Difference
{
    private KeyPair keyPair;
    private String column;
    private String valueA;
    private String valueB;
    
    public Difference(KeyPair keyPair, String column, String valueA, String valueB)
    {
        this.keyPair = keyPair;
        this.column = column;
        this.valueA = valueA;
        this.valueB = valueB;
    }

    public KeyPair getKeyPair()
    {
        return keyPair;
    }
    
    public String getColumn()
    {
        return column;
    }
    
    public String getValueA()
    {
        return valueA;
    }
    
    public String getValueB()
    {
        return valueB;
    }
}
