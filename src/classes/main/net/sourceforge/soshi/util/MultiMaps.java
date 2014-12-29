package net.sourceforge.soshi.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

public class MultiMaps
{
    public static MultiMap unmodifiableMultiMap(MultiMap m)
    {
        return new UnmodifiableMultiMap(m);
    }

    public static SortedMultiMap unmodifiableSortedMultiMap(SortedMultiMap sm)
    {
        return new UnmodifiableSortedMultiMap(sm);
    }

    private static class UnmodifiableMultiMap implements MultiMap
    {
        private MultiMap m;

        public UnmodifiableMultiMap(MultiMap m)
        {
            if (m == null) {
                throw new NullPointerException();
            }
            this.m = m;
        }

        public void clear()
        {
            throw new UnsupportedOperationException();
        }

        public Set coarseEntrySet()
        {
            return Collections.unmodifiableSet(m.coarseEntrySet());
        }

        public Collection coarseValues()
        {
            return Collections.unmodifiableCollection(m.coarseValues());
        }

        public boolean containsEntry(Object key, Object value)
        {
            return m.containsEntry(key, value);
        }

        public boolean containsKey(Object key)
        {
            return m.containsKey(key);
        }

        public boolean containsValue(Object value)
        {
            return m.containsValue(value);
        }

        public Set fineEntrySet()
        {
            return Collections.unmodifiableSet(m.fineEntrySet());
        }

        public Collection fineValues()
        {
            return Collections.unmodifiableCollection(m.fineValues());
        }

        public boolean equals(Object o)
        {
            return m.equals(o);
        }

        public Collection get(Object key)
        {
            return Collections.unmodifiableCollection(m.get(key));
        }

        public int hashCode()
        {
            return m.hashCode();
        }

        public boolean hasKeys()
        {
            return m.hasKeys();
        }

        public boolean hasValues()
        {
            return m.hasValues();
        }

        public Set keySet()
        {
            return Collections.unmodifiableSet(m.keySet());
        }

        public Collection put(Object key)
        {
            throw new UnsupportedOperationException();
        }

        public void put(Object key, Object value)
        {
            throw new UnsupportedOperationException();
        }

        public void putAll(Map map)
        {
            throw new UnsupportedOperationException();
        }

        public void putAll(MultiMap map)
        {
            throw new UnsupportedOperationException();
        }

        public Collection remove(Object key)
        {
            throw new UnsupportedOperationException();
        }

        public boolean remove(Object key, Object value)
        {
            throw new UnsupportedOperationException();
        }

        public int sizeKeys()
        {
            return m.sizeKeys();
        }

        public int sizeValues()
        {
            return m.sizeValues();
        }
    }

    private static class UnmodifiableSortedMultiMap extends UnmodifiableMultiMap implements SortedMultiMap
    {
        private SortedMultiMap sm;

        public UnmodifiableSortedMultiMap(SortedMultiMap sm)
        {
            super(sm);
            this.sm = sm;
        }

        public Comparator comparator()
        {
            return sm.comparator();
        }

        public Object firstKey()
        {
            return sm.firstKey();
        }

        public SortedMultiMap headMap(Object toKey)
        {
            return new UnmodifiableSortedMultiMap(sm.headMap(toKey));
        }

        public Object lastKey()
        {
            return sm.lastKey();
        }

        public SortedMultiMap subMap(Object fromKey, Object toKey)
        {
            return new UnmodifiableSortedMultiMap(sm.subMap(fromKey, toKey));
        }

        public SortedMultiMap tailMap(Object fromKey)
        {
            return new UnmodifiableSortedMultiMap(sm.tailMap(fromKey));
        }
    }
}
