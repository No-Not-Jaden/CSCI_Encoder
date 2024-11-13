package student;

import provided.BinarySequence;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class mimics a Hashmap with no remove function.
 */
public class HuffmanCodeBook implements Iterable<Character> {

    /**
     * Bucket holding all the characters and binary sequences.
     */
    private CodeSet[] bucket;
    /**
     * How many elements are in the bucket array.
     */
    private int bucketSize;

    /**
     * Create a new HuffmanCodeBook data structure.
     */
    public HuffmanCodeBook() {
        bucket = new CodeSet[16];
        bucketSize = 0;
    }

    /**
     * Initialize a Code Book with a previous bucket and size.
     *
     * @param bucket     Bucket to use.
     * @param bucketSize The number of objects in the bucket array that are not null.
     */
    private HuffmanCodeBook(CodeSet[] bucket, int bucketSize) {
        this.bucket = bucket;
        this.bucketSize = bucketSize;
    }

    /**
     * Get the index of a character in the bucket.
     *
     * @param c Character to find the index of.
     * @return The index of where the character is or should be located.
     */
    private int getIndex(char c) {
        return c % bucket.length;
    }

    /**
     * Store a sequence.
     *
     * @param c   Character that the sequence represents.
     * @param seq Sequence of binary that represents the character.
     */
    public void addSequence(char c, BinarySequence seq) {
        int index = getIndex(c);
        if (bucket[index] == null) {
            // create list at index if it doesn't exist already
            bucket[index] = new CodeSet();
            bucketSize++;
        }
        bucket[index].add(new Node(c, seq));

        if (getLoadFactor() > 0.6 || bucket[index].getSize() > 4) {
            increaseSize();
        }
    }

    /**
     * Increase the size of the bucket.
     */
    private void increaseSize() {
        HuffmanCodeBook oldBucket = new HuffmanCodeBook(bucket, bucketSize);
        bucket = new CodeSet[bucket.length * 3 / 2 + 1];
        bucketSize = 0;

        for (char c : oldBucket) {
            addSequence(c, oldBucket.getSequence(c));
        }
    }

    /**
     * Get the load factor of the bucket.
     *
     * @return The number of nonnull objects in the bucket divided by the length of the bucket.
     */
    private double getLoadFactor() {
        return (double) bucketSize / bucket.length;
    }

    /**
     * Check if the letter has been added.
     *
     * @param letter Letter to look for.
     * @return true if the letter has a binary sequence associated with it.
     */
    public boolean contains(char letter) {
        int index = getIndex(letter);
        return bucket[index] != null && bucket[index].containsKey(letter);
    }

    /**
     * Check if all the letters in a string can be encoded.
     *
     * @param letters Letters to check.
     * @return True if all the letters can be encoded.
     */
    public boolean containsAll(String letters) {
        for (int i = 0; i < letters.length(); i++) {
            if (!contains(letters.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get the binary sequence associated with this character.
     *
     * @param c The character.
     * @return A binary sequence or null if the sequence hasn't been added yet.
     */
    public BinarySequence getSequence(char c) {
        int index = getIndex(c);
        if (bucket[index] == null) {
            return null;
        }
        Node node = bucket[index].get(c);
        return node != null ? node.data() : null;
    }

    /**
     * Encode a message into binary.
     *
     * @param s Message to encode.
     * @return The encoded message.
     */
    public BinarySequence encode(String s) {
        BinarySequence sequence = new BinarySequence();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (contains(c)) {
                sequence.append(getSequence(c));
            }
        }
        return sequence;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[\n");
        for (int i = 0; i < bucket.length; i++) {
            if (bucket[i] != null) {
                if (i > 0) {
                    builder.append(",\n");
                }
                builder.append(bucket[i].toString());
            }
        }
        builder.append("\n]");
        return builder.toString();
    }

    @Override
    public Iterator<Character> iterator() {
        return new HuffmanCodeBookIterator();
    }

    /**
     * Allows users to iterate through the characters in this data structure.
     * See {@link HuffmanCodeBook#increaseSize()} for implementation example.
     */
    private class HuffmanCodeBookIterator implements Iterator<Character> {

        /**
         * Current index in the bucket.
         */
        private int currentBucketIndex = 0;

        /**
         * Current index of a list inside the bucket.
         */
        private int currentListIndex = 0;

        /**
         * Number of elements that have been iterated through.
         */
        private int iteratedElements = 0;

        @Override
        public boolean hasNext() {
            return iteratedElements < bucketSize;
        }

        @Override
        public Character next() {
            // find the next nonnull element
            while (bucket[currentBucketIndex] == null) {
                currentBucketIndex++;
                if (currentBucketIndex >= bucket.length) {
                    // reached the end of the list
                    throw new NoSuchElementException();
                }
            }
            // get a character at the current index.
            Character character = bucket[currentBucketIndex].get(currentListIndex).key();
            // increment indexes for next call
            currentListIndex++;
            if (currentListIndex >= bucket[currentBucketIndex].getSize()) {
                currentListIndex = 0;
                currentBucketIndex++;
                iteratedElements++;
            }
            return character;
        }
    }
}

/**
 * This object holds a sorted list of nodes.
 * Node keys cannot be repeated.
 * Adding a new node with the same key as a previous one will overwrite the old node.
 */
class CodeSet {

    /**
     * List of nodes being stored.
     */
    private Node[] list;
    /**
     * Number of elements in the list.
     */
    private int size;

    /**
     * Initialize a new list of codes.
     */
    CodeSet() {
        size = 0;
        list = new Node[2];
    }

    /**
     * Add a node to the list.
     *
     * @param node Node to add.
     */
    public void add(Node node) {
        // Inserts an element in a sorted position.
        if (size == 0) {
            // no elements to compare to
            list[0] = node;
            size = 1;
            return;
        }
        // find where the element should be inserted
        int index = getInsertIndex(node.key());
        if (index == size || list[index].key() != node.key()) {
            // shift to the right to make space
            shiftRight(index);
        }
        list[index] = node;
    }

    /**
     * Get the index where this key should be inserted to keep the order of the list.
     *
     * @param key Key to insert.
     * @return The index where this key should be inserted.
     */
    private int getInsertIndex(char key) {
        for (int i = 0; i < size; i++) {
            if (key <= list[i].key()) {
                return i;
            }
        }
        return size;
    }

    /**
     * Searches for a key in the list.
     * Returns the index of where the key is positioned in the list,
     * or -1 if the key doesn't exist.
     *
     * @param key The key to search for.
     * @return The location in the list.
     */
    private int binarySearch(char key) {
        return binarySearch(key, size - 1, 0);
    }

    /**
     * Searches for a key in the list.
     * Returns the index of where the key is positioned in the list,
     * or -1 if the key doesn't exist in the list.
     *
     * @param key  The key to search for.
     * @param high The highest index to search through.
     * @param low  The lowest index to search through.
     * @return The location in the list.
     */
    private int binarySearch(char key, int high, int low) {
        int mid = (high + low) / 2;
        if (high < low) {
            return -1;
        } else if (list[mid].key() > key) {
            // search lower
            return binarySearch(key, mid - 1, low);
        } else if (list[mid].key() < key) {
            // search upper
            return binarySearch(key, high, mid + 1);
        } else {
            return mid;
        }
    }

    /**
     * Shift the list right at the specified index.
     * The object at index and every object after it will be shifted to the right by 1.
     *
     * @param index Index to shift.
     */
    private void shiftRight(int index) {
        if (size == list.length) {
            // increase size
            increaseSize();
        }
        // iterate starting one element after the end to one after the specified index
        // the item at index is safe to replace because the item directly after is a duplicate.
        for (int i = size; i > index; i--) {
            list[i] = list[i - 1];
        }
        size++;
    }

    /**
     * Increases the size of the list dynamically.
     * O(n) complexity.
     */
    private void increaseSize() {
        Node[] newList = new Node[list.length * 3 / 2 + 1];
        System.arraycopy(list, 0, newList, 0, list.length);
        list = newList;
    }

    /**
     * Get a node from the list.
     *
     * @param index Index of the node in the list.
     * @return The node at that index, or null if the index is out of bounds.
     */
    public Node get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return list[index];
    }

    /**
     * Get the node from a certain key.
     *
     * @param key Key to search for.
     * @return The node corresponding to the key, or null if it doesn't exist.
     */
    public Node get(char key) {
        if (size == 0) {
            return null;
        }
        int index = binarySearch(key);
        return index == -1 ? null : list[index];
    }

    /**
     * Check if a key exists in the list.
     *
     * @param key Key to look for.
     * @return True if the key exists.
     */
    public boolean containsKey(char key) {
        if (size == 0) {
            return false;
        }
        // Binary search is O(logN) for the array in this object,
        // but because the length of any CodeSet array is capped at 4 (See HuffmanCodeBook#addSequence()),
        // the worst case runtime of this call is O(log4) which is a constant.
        return binarySearch(key) != -1;
    }

    /**
     * Get the number of elements stored in the list.
     *
     * @return The number of elements stored in the list.
     */
    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(list[i]);
        }
        builder.append(']');
        return builder.toString();
    }
}

/**
 * Record used to store encoding information.
 *
 * @param key  Character to be encoded.
 * @param data Binary sequence that represents the key.
 */
record Node(char key, BinarySequence data) {
}
