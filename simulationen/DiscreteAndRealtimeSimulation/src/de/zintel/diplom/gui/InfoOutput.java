package de.zintel.diplom.gui;
import java.io.PrintStream;

import javax.swing.JTextArea;

/**
 * Variable target-output.
 */

public class InfoOutput {

	JTextArea textarea = null;

	PrintStream stream = System.out;

	protected enum Type {
		STREAM, TEXTAREA
	};

	private InfoOutput.Type type = Type.STREAM;

	public InfoOutput() {
	}

	public void setOutput(JTextArea textarea) {
		this.textarea = textarea;
		type = Type.TEXTAREA;
	}

	public void setOutput(PrintStream stream) {
		this.stream = stream;
		type = Type.STREAM;
	}

	public void write(String text) {

		if ( type == Type.TEXTAREA )
			textarea.append(text);
		else
			stream.print(text);
	}

}