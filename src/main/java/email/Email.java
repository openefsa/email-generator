package email;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;

public class Email {

	private String[] recipients;
	private String recipientsSep;
	private String subject;
	private String body;
	private Collection<File> attachments;

	public Email(String subject, String body, String recipientsSep, String... recipients) {
		this.recipients = recipients;
		this.recipientsSep = recipientsSep;
		this.subject = subject;
		this.body = body;
		this.attachments = new ArrayList<>();
	}

	/**
	 * Check if the user can open the mail client
	 * @return
	 */
	public boolean isSupported() {
		return Desktop.isDesktopSupported();
	}
	
	public void openEmailClient() throws IOException, URISyntaxException {
		
		if (!Desktop.isDesktopSupported()) {
			throw new RuntimeException("Cannot send e-mail. Desktop is not supported");
		}
		
		String mailTo = getEmailLink();
		Desktop.getDesktop().mail(new URI(mailTo));
	}
	
	/*
	private void addAttachment(File file) {  // NOT SUPPORTED YET
		this.attachments.add(file);
	}*/

	/**
	 * Format the e-mail recipients/subject/body into a mailto link
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	private String getEmailLink() throws UnsupportedEncodingException {
		
		String link = String.format("mailto:%s?subject=%s&body=%s", paste(recipientsSep, recipients), 
				urlEncode(subject), urlEncode(body));
		
		// add attachments
		for (File attachment : attachments) {
			link += "&attached=" + attachment.getPath();
		}
		
		return link;
	}

	/**
	 * Encode the content of a string into a usable string for the mailto link
	 * @param str
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	private final String urlEncode(String str) throws UnsupportedEncodingException {
		
		String encoded = URLEncoder.encode(str, "UTF-8")
				.replace("+", "%20")
				.replace("%5Cr%5Cn", "%0d%0a")
				.replace("%5Cr", "%0d%0a")
				.replace("%5Cn", "%0d%0a");
		
		return encoded;
	}

	/**
	 * Join a list of strings into a single one, separated by sep
	 * @param sep
	 * @param objs
	 * @return
	 */
	private final String paste(String sep, String... objs) {

		StringBuilder sb = new StringBuilder();

		for(Object obj : objs) {
			if (sb.length() > 0) sb.append(sep);
			sb.append(obj);
		}

		return sb.toString();
	}
}
