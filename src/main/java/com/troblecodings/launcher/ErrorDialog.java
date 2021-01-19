package com.troblecodings.launcher;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class ErrorDialog {

	public static void createDialog(Throwable th) {
		JTextArea area = new JTextArea();
		area.setEditable(false);
		String message = "Message: " + th.getMessage() + System.lineSeparator() +
				"Exception: " + th.getClass().getName() + System.lineSeparator() +
				"Trace: ";
		for(StackTraceElement element : th.getStackTrace()) {
			message += element + System.lineSeparator();
		}
		if(th.getCause() != null) {
			createDialog(th.getCause());
		}
		JOptionPane.showMessageDialog(null, message, "Error!", JOptionPane.ERROR_MESSAGE);
	}
	
}
