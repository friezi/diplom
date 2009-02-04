package de.zintel.diplom.tools;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.*;

/**
 * 
 */

/**
 * At the end of file the file's SHA1-value will be appended for later
 * integrity-check.
 * 
 * @author Friedemann Zintel
 * 
 */
public class IntegrityFileOutputStream extends OutputStream {

	private FileOutputStream file;

	private DigestOutputStream digestout;

	/**
	 * @param filename
	 *            name of the file
	 * @throws FileNotFoundException
	 * @throws NoSuchAlgorithmException
	 */
	public IntegrityFileOutputStream(String filename) throws FileNotFoundException, NoSuchAlgorithmException {

		file = new FileOutputStream(filename);
		digestout = new DigestOutputStream(file, MessageDigest.getInstance("SHA-1"));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.FileOutputStream#close()
	 */
	@Override
	public void close() throws IOException {

		// before closing get digest and append it to file
		byte[] digest = digestout.getMessageDigest().digest();
		
		digestout.write(digest);
		digestout.close();
		
		super.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	@Override
	public void write(byte[] arg0, int arg1, int arg2) throws IOException {
		digestout.write(arg0, arg1, arg2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#write(byte[])
	 */
	@Override
	public void write(byte[] arg0) throws IOException {
		digestout.write(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int arg0) throws IOException {
		digestout.write(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#flush()
	 */
	@Override
	public void flush() throws IOException {

		digestout.flush();
		super.flush();

	}

}
