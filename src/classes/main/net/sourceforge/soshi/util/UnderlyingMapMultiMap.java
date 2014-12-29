package net.sourceforge.soshi.util;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

abstract class UnderlyingMapMultiMap implements MultiMap
{
    protected abstract Map getMap();

    public void clear()
    {
        getMap().clear();
    }

    public Set coarseEntrySet()
    {
        return getMap().entrySet();
    }

    public Collection coarseValues()
    {
        return getMap().values();
    }

    public boolean containsEntry(Object key, Object value)
    {
        Collection c = (Collection) getMap().get(key);
        if (c == null) {
            return false;
        }

        return c.contains(value);
    }

    public boolean containsKey(Object key)
    {
        return getMap().containsKey(key);
    }

    public boolean containsValue(Object value)
    {
        for (Iterator i = getMap().values().iterator(); i.hasNext();) {
            Collection c = (Collection) i.next();
            if (c.contains(value)) {
                return true;
            }
        }

        return false;
    }

    public Set fineEntrySet()
    {
        return new EntrySet();
    }

    public Collection fineValues()
    {
        return new ValueCollection();
    }

    // Not implementing equals(Object o)

    public Collection get(Object o)
    {
        return (Collection) getMap().get(o);
    }

    // Not implementing hashCode()

    public boolean hasKeys()
    {
        return !getMap().isEmpty();
    }

    public boolean hasValues()
    {
        return (sizeValues() != 0);
    }

    public Set keySet()
    {
        return getMap().keySet();
    }

    public Collection put(Object key)
    {
        return (Collection) getMap().put(key, new HashSet());
    }

    public void put(Object key, Object value)
    {
        Collection collection = (Collection) getMap().get(key);
        if (collection == null) {
            collection = new HashSet();
            getMap().put(key, collection);
        }
        collection.add(value);
    }

    public void putAll(Map map)
    {
        for (Iterator i = map.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            put(key, value);
        }
    }

    public void putAll(MultiMap map)
    {
        for (Iterator i = map.fineEntrySet().iterator(); i.hasNext();) {
            MultiMap.Entry entry = (MultiMap.Entry) i.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            put(key, value);
        }
    }

    public Collection remove(Object key)
    {
        return (Collection) getMap().remove(key);
    }

    public boolean remove(Object key, Object value)
    {
        Collection c = (Collection) getMap().get(key);
        if (c == null) {
            return false;
        }
        return c.remove(value);
    }

    public int sizeKeys()
    {
        return getMap().size();
    }

    public int sizeValues()
    {
        int result = 0;
        
        for (Iterator i = getMap().values().iterator(); i.hasNext();) {
            Collection value = (Collection) i.next();
            result += value.size();
        }
        
        return result;
    }

    private class Entry implements MultiMap.Entry
    {
        private Object key;
        private Object value;

        Entry(Object key, Object value)
        {
            this.key = key;
            this.value = value;
        }

        public Object getKey()
        {
            return key;
        }

        public Object getValue()
        {
            return value;
        }
    }

    private class EntrySet extends AbstractSet
    {
        public boolean add(Object o)
        {
            throw new UnsupportedOperationException();
        }

        public boolean isEmpty()
        {
            return !hasValues();
        }

        public Iterator iterator()
        {
            return new EntrySetItr();
        }

        public int size()
        {
            return sizeValues();
        }

        private class EntrySetItr implements Iterator
        {
            private Iterator outerIterator;
            private Iterator innerIterator;
            private Object currentKey;

            EntrySetItr()
            {
                outerIterator = getMap().entrySet().iterator();
                if (outerIterator.hasNext()) {
                    Collection c = getNextCollection();
                    innerIterator = c.iterator();
                } else {
                    innerIterator = null;
                }
            }

            public boolean hasNext()
            {
                if (innerIterator == null) {
                    return false;
                }

                if (innerIterator.hasNext()) {
                    return true;
                }

                while (outerIterator.hasNext()) {
                    Collection c = getNextCollection();
                    innerIterator = c.iterator();
                    if (innerIterator.hasNext()) {
                        return true;
                    }
                }

                return false;
            }

            public Object next()
            {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                Object value = innerIterator.next();
                return new Entry(currentKey, value);
            }

            public void remove()
            {
                throw new UnsupportedOperationException();
            }

            private Collection getNextCollection()
            {
                Map.Entry entry = (Map.Entry) outerIterator.next();
                currentKey = entry.getKey();
                return (Collection) entry.getValue();
            }
        }
    }

    public class ValueCollection extends AbstractCollection
    {
        public boolean add(Object o)
        {
            throw new UnsupportedOperationException();
        }

        public Iterator iterator()
        {
            return new ValueCollectionItr();
        }

        public int size()
        {
            return sizeValues();
        }

        private class ValueCollectionItr implements Iterator
        {
            private Iterator outerIterator;
            private Iterator innerIterator;

            ValueCollectionItr()
            {
                outerIterator = getMap().values().iterator();
                if (outerIterator.hasNext()) {
                    Collection c = (Collection) outerIterator.next();
                    innerIterator = c.iterator();
                } else {
                    innerIterator = null;
                }
            }

            public boolean hasNext()
            {
                if (innerIterator == null) {
                    return false;
                }

                if (innerIterator.hasNext()) {
                    return true;
                }

                while (outerIterator.hasNext()) {
                    Collection c = (Collection) outerIterator.next();
                    innerIterator = c.iterator();
                    if (innerIterator.hasNext()) {
                        return true;
                    }
                }

                return false;
            }

            public Object next()
            {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                return innerIterator.next();
            }

            public void remove()
            {
                throw new UnsupportedOperationException();
            }
        }
    }
}
