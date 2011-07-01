package com.weiglewilczek.coop.client.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.weiglewilczek.coop.client.ui.messages"; //$NON-NLS-1$
	public static String update;
	public static String save;
	public static String all;
	public static String timeTracking;
	public static String applicationName;
	public static String login;
	public static String password;
	public static String rememberMyLogin;
	public static String rememberLoginAndAutoLogIn;
	public static String updateInterval;
	public static String shownAmountOfCoopEntries;
	public static String thirtyEntries;
	public static String defaultEntries;
	public static String today;
	public static String twoDays;
	public static String threeDays;
	public static String rightNow;
	public static String secondsAgo;
	public static String aboutOneMinuteAgo;
	public static String aboutNMinutesAgo;
	public static String aboutOneHourAgo;
	public static String aboutNHoursAgo;
	public static String yesterday;
	public static String aboutNDaysAgo;
	public static String overAYearAgo;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
