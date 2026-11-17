package cs321.btree;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * Disk-based B-Tree storing SSH keys and their frequencies.

 */
public class BTree implements BTreeInterface {

    private static final int DEFAULT_DEGREE = 2;
    private static final int MAX_KEY_LENGTH = 32;
    private static final int METADATA_SIZE = Long.BYTES + Integer.BYTES + Long.BYTES + Long.BYTES + Integer.BYTES; // 32 bytes
    private static final int NODE_HEADER_SIZE = Long.BYTES + Byte.BYTES + Integer.BYTES; // 13 bytes

    private RandomAccessFile file;
    private final int degree;
    private final int nodeByteSize;

    private long rootOffset;
    private long size;
    private long numNodes;
    private int height;
    private BTreeNode root;

    /** Represents a single B-Tree node stored on disk. */
    private class BTreeNode {
        long offset;
        boolean isLeaf;
        int numKeys;
        TreeObject[] keys;
        long[] children;

        BTreeNode(long offset, boolean isLeaf) {
            this.offset = offset;
            this.isLeaf = isLeaf;
            this.numKeys = 0;
            this.keys = new TreeObject[2 * degree - 1];
            this.children = new long[2 * degree];
            for (int i = 0; i < children.length; i++) {
                this.children[i] = -1L;
            }
        }
    }

    /** Creates an empty B-Tree using the default degree of 2. */
    public BTree(String fileName) throws BTreeException {
        this(DEFAULT_DEGREE, fileName);
    }

    /** Creates an empty B-Tree with the given degree and initializes its disk file. */
    public BTree(int degree, String fileName) throws BTreeException {
        if (fileName == null || fileName.isEmpty()) {
            throw new BTreeException("BTree file name cannot be null or empty.");
        }
        if (degree < 2) {
            throw new BTreeException("BTree degree must be at least 2.");
        }

        this.degree = degree;
        this.nodeByteSize = NODE_HEADER_SIZE + (2 * degree - 1) * TreeObject.BYTES + (2 * degree) * Long.BYTES;

        try {
            this.file = new RandomAccessFile(new File(fileName), "rw");
            this.file.setLength(0);
            this.size = 0;
            this.numNodes = 0;
            this.height = 0;
            this.root = allocateNode(true);
            this.rootOffset = this.root.offset;

            writeMetadata();
            diskWrite(this.root);
        } catch (IOException e) {
            throw new BTreeException("Failed to initialize BTree file: " + e.getMessage());
        }
    }

    private BTreeNode allocateNode(boolean isLeaf) {
        long offset = METADATA_SIZE + numNodes * (long) nodeByteSize;
        numNodes++;
        return new BTreeNode(offset, isLeaf);
    }

    private void writeMetadata() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(METADATA_SIZE);
        buffer.putLong(rootOffset);
        buffer.putInt(degree);
        buffer.putLong(size);
        buffer.putLong(numNodes);
        buffer.putInt(height);

        file.seek(0);
        file.write(buffer.array());
    }

    /** Writes a BTreeNode to its fixed byte offset on disk (keys padded to 32 chars). */
    public void diskWrite(BTreeNode node) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(nodeByteSize);
        buffer.putLong(node.offset);
        buffer.put((byte) (node.isLeaf ? 1 : 0));
        buffer.putInt(node.numKeys);

        for (int i = 0; i < 2 * degree - 1; i++) {
            if (i < node.numKeys && node.keys[i] != null) {
                String key = truncateKey(node.keys[i].getKey());
                for (int c = 0; c < MAX_KEY_LENGTH; c++) {
                    buffer.putChar(c < key.length() ? key.charAt(c) : '\0');
                }
                buffer.putLong(node.keys[i].getCount());
            } else {
                for (int c = 0; c < MAX_KEY_LENGTH; c++) {
                    buffer.putChar('\0');
                }
                buffer.putLong(0L);
            }
        }

        for (int i = 0; i < 2 * degree; i++) {
            buffer.putLong(node.children[i]);
        }

        file.seek(node.offset);
        file.write(buffer.array());
    }


    /** Reads a BTreeNode from the given byte offset on disk and strips null padding. */
    public BTreeNode diskRead(long offset) throws IOException {
        if (root != null && root.offset == offset) {
            return root;
        }

        byte[] bytes = new byte[nodeByteSize];
        file.seek(offset);
        file.readFully(bytes);

        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        BTreeNode node = new BTreeNode(buffer.getLong(), buffer.get() == 1);
        node.numKeys = buffer.getInt();

        for (int i = 0; i < 2 * degree - 1; i++) {
            StringBuilder sb = new StringBuilder(MAX_KEY_LENGTH);
            boolean terminated = false;
            for (int c = 0; c < MAX_KEY_LENGTH; c++) {
                char ch = buffer.getChar();
                if (ch == '\0') {
                    terminated = true;
                } else if (!terminated) {
                    sb.append(ch);
                }
            }
            long count = buffer.getLong();
            if (i < node.numKeys) {
                node.keys[i] = new TreeObject(sb.toString(), count);
            }
        }

        for (int i = 0; i < 2 * degree; i++) {
            node.children[i] = buffer.getLong();
        }

        return node;
    }


    private static String truncateKey(String key) {
        return key.length() > MAX_KEY_LENGTH ? key.substring(0, MAX_KEY_LENGTH) : key;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public int getDegree() {
        return degree;
    }

    @Override
    public long getNumberOfNodes() {
        return numNodes;
    }

    @Override
    public int getHeight() {
        return height;
    }




    @Override
    public void insert(TreeObject obj) throws IOException {
        // TODO: Task #5
    }

    /** Returns keys in sorted order for BTreeTest validation. */
    public String[] getSortedKeyArray() throws IOException {
        // TODO: Task #5
        return new String[0];
    }






    @Override
    public TreeObject search(String key) throws IOException {
        // Sprint 2
        return null;
    }

    @Override
    public void dumpToFile(PrintWriter out) throws IOException {
        // Sprint 2
    }

    @Override
    public void dumpToDatabase(String dbName, String tableName) throws IOException {
        // Sprint 2
    }

    @Override
    public void delete(String key) {
        // Not implemented 
    }
}