package com.teamscale.aliasgenerator;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;

import com.beust.jcommander.Parameter;

public class Config {
	/** Flag that indicates whether this is a dry run. */
	@Parameter(names = { "-d", "--dry" }, arity = 0, description = "Enables dry-run.")
	private boolean dryRun = false;

	/**
	 * Flag that indicates whether the email address is added to the list of
	 * aliases.
	 */
	@Parameter(names = { "-e", "--email" }, arity = 0, description = "Adds the email addresses as alias.")
	private boolean addEmail = false;

	/** Set alias configuration. */
	@Parameter(names = { "-c", "--config" }, required = true, description = "Sets the path to the alias config file.")
	private String configFile = null;

	/** The server URL. */
	@Parameter(names = { "-s", "--server-url" }, required = true, description = "Sets the url of the server.")
	private String serverUrl;

	/** The user name for the server. */
	@Parameter(names = { "-u", "--user" }, required = true, description = "Sets the server user.")
	private String serverUser;

	/** The user password for the server. */
	@Parameter(names = { "-t", "--token" }, required = true, description = "Sets the user token.")
	private String serverToken;

	/** Whether to display help. */
	@Parameter(names = { "-h", "--help" }, help = true)
	private boolean help;

	/**
	 * Parses the alias rules from the specified config file.
	 */
	public AliasRule[] getAliasRules() throws IOException {
		return AliasRule.rulesFromJson(this.configFile);
	}

	/** Initializes the {@link HttpClient} */
	public HttpClientContext getHttpContext() throws URISyntaxException {
		URI uri = new URI(this.serverUrl);
		HttpHost targetHost = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()),
				new UsernamePasswordCredentials(this.serverUser, this.serverToken));

		// Create AuthCache instance
		AuthCache authCache = new BasicAuthCache();
		// Generate BASIC scheme object and add it to the local auth cache
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(targetHost, basicAuth);

		// Add AuthCache to the execution context
		HttpClientContext context = HttpClientContext.create();
		context.setCredentialsProvider(credsProvider);
		context.setAuthCache(authCache);
		return context;
	}

	/** Returns the server url without trailing slash, */
	public String serverUrl() {
		if (this.serverUrl.endsWith("/")) {
			return this.serverUrl.substring(0, this.serverUrl.length() - 1);
		}

		return this.serverUrl + "/";
	}

	/** @see #dryRun */
	public boolean dryRun() {
		return this.dryRun;
	}

	/** @see #addEmail */
	public boolean addEmail() {
		return this.addEmail;
	}

	/** @see #help */
	public boolean help() {
		return this.help;
	}
}
