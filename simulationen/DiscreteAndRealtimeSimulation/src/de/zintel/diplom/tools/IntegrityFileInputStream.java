package de.zintel.diplom.tools;
import java.io.*;
import java.security.*;

/**
 * 
 */

/**
 * The integrity of the file will be checked by comparing the appended
 * SHA-1-value with the SHA-1-value of the file.
 * 
 * @author Friedemann Zintel
 * 
 */
public class IntegrityFileInputStream extends InputStream {

	@SuppressWarnings("serial")
	public class IntegrityException extends Exception {
	}

	private FileInputStream file;

	private int datasize;

	private long offset = 0;

	private long mark = 0;

	private static final int SHA1_BYTES = 20;

	/**
	 * @param filename
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws IntegrityException
	 */
	public IntegrityFileInputStream(String filename) throws NoSuchAlgorithmException, IOException, IntegrityException {

		super();

		// first calculate the digest of the file-content
		file = new FileInputStream(filename);

		DigestInputStream digestin = new DigestInputStream(file, MessageDigest.getInstance("SHA-1"));

		datasize = (int) (file.getChannel().size() - SHA1_BYTES);

		byte[] buffer = new byte[datasize];

		digestin.read(buffer, 0, datasize);

		byte[] digest = digestin.getMessageDigest().digest();

		byte[] saved_digest = new byte[SHA1_BYTES];

		// compare it to the saved digest
		digestin.read(saved_digest, 0, SHA1_BYTES);
		
		digestin.close();

		if ( new String(digest).equals(new String(saved_digest)) == false )
			throw new IntegrityException();

		// almost normal reading of data; on reading we have to guarantee that the digest is not a part of the normal datas.
		file = new FileInputStream(filename);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {

		if ( offset < datasize ) {

			int res = file.read();
			offset++;
			return res;

		} else
			return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#available()
	 */
	@Override
	public int available() throws IOException {
		return file.available() - SHA1_BYTES;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#close()
	 */
	@Override
	public void close() throws IOException {

		file.close();
		super.close();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#mark(int)
	 */
	@Override
	public synchronized void mark(int readlimit) {

		file.mark(readlimit);
		mark = offset;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#markSupported()
	 */
	@Override
	public boolean markSupported() {
		return file.markSupported();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] buf, int offs, int len) throws IOException {

		if ( offset + len < datasize ) {

			offset += len;
			return file.read(buf, offs, len);

		} else {

			int diff = (int) (datasize - offset);
			offset = datasize;
			file.read(buf, offs, diff);
			return diff;

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read(byte[])
	 */
	@Override
	public int read(byte[] buf) throws IOException {
		return read(buf, 0, buf.length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#reset()
	 */
	@Override
	public synchronized void reset() throws IOException {

		file.reset();
		offset = mark;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#skip(long)
	 */
	@Override
	public long skip(long num) throws IOException {

		if ( offset + num < datasize ) {

			offset += num;
			return file.skip(num);

		} else {

			int diff = (int) (datasize - offset);
			offset = datasize;
			file.skip(diff);
			return diff;

		}
	}

}
