package com.webresponsive.surfapps.surfstoked.mail;

import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailHelper extends javax.mail.Authenticator
{
    private String _user;
    private String _pass;

    private String[] _to;
    private String _from;

    private String _port;
    private String _sport;

    private String _host;

    private String _subject;
    private String _body;

    private boolean _auth;

    private boolean _debuggable;

    private Multipart _multipart;

    private String[] _filenames;

    public MailHelper()
    {
	_host = "smtp.gmail.com";
	_port = "465";
	_sport = "465";

	_user = "";
	_pass = "";
	_from = "";
	_subject = "";
	_body = "";

	_debuggable = true;
	_auth = true;

	_multipart = new MimeMultipart();

	// There is something wrong with MailCap, javamail can not find a
	// handler for the multipart/mixed part, so this bit needs to be added.
	MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
	mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
	mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
	mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
	mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
	mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
	CommandMap.setDefaultCommandMap(mc);
    }

    public MailHelper(String user, String pass)
    {
	this();

	_user = user;
	_pass = pass;
    }

    public boolean send() throws Exception
    {
	Properties props = _setProperties();

	if (!_user.equals("") && !_pass.equals("") && _to.length > 0 && !_from.equals("") && !_subject.equals("")
		&& !_body.equals(""))
	{
	    Session session = Session.getInstance(props, this);

	    MimeMessage msg = new MimeMessage(session);

	    msg.setFrom(new InternetAddress(_from));

	    InternetAddress[] addressTo = new InternetAddress[_to.length];
	    for (int i = 0; i < _to.length; i++)
	    {
		if ((_to[i] != null) && (!_to[i].equals("")))
		{
		    addressTo[i] = new InternetAddress(_to[i]);
		}
	    }
	    msg.setRecipients(MimeMessage.RecipientType.BCC, addressTo);

	    msg.setSubject(_subject);
	    msg.setSentDate(new Date());

	    BodyPart messageBodyPart = new MimeBodyPart();
	    messageBodyPart.setText(_body);

	    _multipart.addBodyPart(messageBodyPart);

	    for (int i = 0; i < _filenames.length; i++)
	    {
		messageBodyPart = new MimeBodyPart();
		DataSource source = new FileDataSource(_filenames[i]);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(_filenames[i]);
		_multipart.addBodyPart(messageBodyPart);
	    }

	    msg.setContent(_multipart);

	    Transport.send(msg);

	    return true;
	} else
	{
	    return false;
	}
    }

    public void addAttachment(String filename) throws Exception
    {
	BodyPart messageBodyPart = new MimeBodyPart();
	DataSource source = new FileDataSource(filename);
	messageBodyPart.setDataHandler(new DataHandler(source));
	messageBodyPart.setFileName(filename);

	_multipart.addBodyPart(messageBodyPart);
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication()
    {
	return new PasswordAuthentication(_user, _pass);
    }

    private Properties _setProperties()
    {
	Properties props = new Properties();

	props.put("mail.smtp.host", _host);

	if (_debuggable)
	{
	    props.put("mail.debug", "true");
	}

	if (_auth)
	{
	    props.put("mail.smtp.auth", "true");
	}

	props.put("mail.smtp.port", _port);
	props.put("mail.smtp.socketFactory.port", _sport);
	props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	props.put("mail.smtp.socketFactory.fallback", "false");

	return props;
    }

    public final String getUser()
    {
	return _user;
    }

    public final void setUser(String _user)
    {
	this._user = _user;
    }

    public final String getPass()
    {
	return _pass;
    }

    public final void setPass(String _pass)
    {
	this._pass = _pass;
    }

    public String getBody()
    {
	return _body;
    }

    public void setBody(String _body)
    {
	this._body = _body;
    }

    public final String[] getTo()
    {
	return _to;
    }

    public final void setTo(String[] _to)
    {
	this._to = _to;
    }

    public final String getSubject()
    {
	return _subject;
    }

    public final void setSubject(String _subject)
    {
	this._subject = _subject;
    }

    public final String getFrom()
    {
	return _from;
    }

    public final void setFrom(String _from)
    {
	this._from = _from;
    }

    public final String getHost()
    {
	return _host;
    }

    public final void setHost(String _host)
    {
	this._host = _host;
    }

    public final String getPort()
    {
	return _port;
    }

    public final void setPort(String _port)
    {
	this._port = _port;
    }

    public final boolean getAuth()
    {
	return _auth;
    }

    public final void setAuth(boolean _auth)
    {
	this._auth = _auth;
    }

    public final String[] getFilenames()
    {
	return _filenames;
    }

    public final void setFileNames(String[] _filenames)
    {
	this._filenames = _filenames;
    }
}