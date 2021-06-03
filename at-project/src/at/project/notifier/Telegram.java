/*
 * Classe amb les URLs necessàries per treballar amb Telegram.
 * També conté els IDs dels nostres xats amb el bot.
*/

package at.project.notifier;

public class Telegram {
	
	private String tgURL = "https://api.telegram.org";
	private String sendMsgRequest = "/bot" + "1654903777:AAGBE15XXmRJoZipItTeoVBBjRgUEP18UNU" + "/sendMessage";
	static int chat_id = 731571954;
	
	//t.me/bicing_info_bot

	public String getTgURL() {
		return tgURL;
	}

	public void setTgURL(String tgURL) {
		this.tgURL = tgURL;
	}

	public String getSendMsgRequest() {
		return sendMsgRequest;
	}

	public void setSendMsgRequest(String sendMsgRequest) {
		this.sendMsgRequest = sendMsgRequest;
	}

	


}
