package net.sourceforge.soshi;

public class KeyPair
{
    private Key keyA;
    private Key keyB;

    public KeyPair(Key keyA, Key keyB)
    {
        this.keyA = keyA;
        this.keyB = keyB;
    }

    public Key getKey(int sideType)
    {
        Key key;
        switch (sideType) {
            case Side.SIDEA:
                key = keyA;
                break;
            case Side.SIDEB:
                key = keyB;
                break;
            default:
                throw new IllegalArgumentException();
        }

        return key;
    }

    public String toString(int sideType)
    {
        String result;

        switch (sideType) {
            case Side.SIDEA:
                result = keyA.toString();
                break;
            case Side.SIDEB:
                result = keyB.toString();
                break;
            default:
                throw new IllegalArgumentException();
        }

        return result;
    }

    public int hashCode()
    {
        return keyA.hashCode() + keyB.hashCode();
    }

    public boolean equals(Object o)
    {
        if (!(o instanceof KeyPair)) {
            return false;
        }

        KeyPair k = (KeyPair) o;

        if (keyA == null) {
            if (k.keyA != null) {
                return false;
            }
        } else {
            if (!keyA.equals(k.keyA)) {
                return false;
            }
        }

        if (keyB == null) {
            if (k.keyB != null) {
                return false;
            }
        } else {
            if (!keyB.equals(k.keyB)) {
                return false;
            }
        }

        return true;
    }
}
