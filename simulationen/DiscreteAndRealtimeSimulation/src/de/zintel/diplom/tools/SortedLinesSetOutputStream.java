package de.zintel.diplom.tools;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.io.*;

/**
 * 
 */

/**
 * Should only be used for character-streams. This Filter sorts the lines
 * alphabeticaly before writing them. Close() should be called manujally after
 * EOS.
 * 
 * @author Friedemann Zintel
 * 
 */
public class SortedLinesSetOutputStream extends FilterOutputStream {

	protected LinkedList<String> comments = new LinkedList<String>();

	protected TreeSet<String> lines = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);

	protected boolean betweenNewlines = true;

	protected boolean isComment = false;

	protected StringWriter currentLine = null;

	protected StringWriter currentCommment = null;

	protected static final byte[] newLine = { '\n' };

	/**
	 * @param arg0
	 */
	public SortedLinesSetOutputStream(OutputStream arg0) {

		super(arg0);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.FilterOutputStream#close()
	 */
	@Override
	public void close() throws IOException {

		for ( String comment : comments ) {

			out.write(comment.getBytes());
			out.write(newLine);

		}

		for ( String line : lines ) {

			out.write(line.getBytes());
			out.write(newLine);

		}

		super.flush();

		super.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.FilterOutputStream#flush()
	 */
	@Override
	public void flush() throws IOException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.FilterOutputStream#write(byte[], int, int)
	 */
	@Override
	public void write(byte[] array, int index, int len) throws IOException {

		for ( int i = index; i < index + len; i++ )
			write((int) array[i]);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.FilterOutputStream#write(byte[])
	 */
	@Override
	public void write(byte[] array) throws IOException {

		for ( byte c : array )
			write((int) c);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.FilterOutputStream#write(int)
	 */
	@Override
	public void write(int c) throws IOException {

		if ( isLineSeparator(c) ) {

			if ( isComment == true )
				comments.add(currentCommment.toString());

			else if ( betweenNewlines == false ) {
				if ( currentLine != null ) {

					lines.add(currentLine.toString());
					currentLine = new StringWriter();

				}
			}

			isComment = false;
			betweenNewlines = true;

		} else {

			if ( betweenNewlines == true ) {

				if ( ((char) c) == '#' ) {

					isComment = true;
					currentCommment = new StringWriter();
					currentCommment.append('#');

				} else {

					currentLine = new StringWriter();
					currentLine.append((char) c);

				}

				betweenNewlines = false;

			} else if ( isComment == true ) {

				currentCommment.append((char) c);

			} else {

				currentLine.append((char) c);

			}
		}

	}

	protected boolean isLineSeparator(int c) {
		return (c == '\n' || c == '\r');
	}

}
