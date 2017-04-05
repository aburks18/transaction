package com.aburks.tranasaction.constants;

public class Requests {
	public static final String REQ_GET_TRANS = ""
			+ "'{'\"args\": "
			+ "'{'\"uid\":  {0},"
			+ " \"token\":  \"{1}\", "
			+ "\"api-token\":  \"{2}\", "
			+ "\"json-strict-mode\": false, "
			+ "\"json-verbose-response\": false'}}'";
	
	public static final String REQ_BAL = ""
			+ "'{'\"args\": "
			+ "'{'\"uid\":  {0},"
			+ " \"token\":  \"{1}\", "
			+ "\"api-token\":  \"{2}\", "
			+ "\"json-strict-mode\": false, "
			+ "\"json-verbose-response\": false'}}'";
}
