package com.teamscale.aliasgenerator;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.google.common.base.Strings;
import com.google.gson.Gson;

/**
 * Generates Teamscale user aliases based on regular expressions.
 */
public class AliasGenerator {

	/** The Teamscale user API endpoint. */
	private static final String USER_ENDPOINT = "/users/";

	/** The logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(AliasGenerator.class);

	/** The configuration. */
	private Config config;

	/**
	 * The JSON deserializer.
	 */
	private final Gson gson = new Gson();

	/** The program entry point. */
	public static void main(String[] args) {
		Config config = new Config();
		JCommander command = JCommander.newBuilder().addObject(config).build();
		command.parse(args);

		if (config.help()) {
			command.usage();
			return;
		}

		new AliasGenerator(config).run();
	}

	public AliasGenerator(Config config) {
		this.config = config;
	}

	/** {@inheritDoc} */
	private void run() {
		if (this.config.dryRun()) {
			LOGGER.info("DRY RUN. Will not store changes.");
		}

		try {
			HttpClientContext context = this.config.getHttpContext();
			for (User user : getUsers(context)) {
				addAliases(context, user);
			}
		} catch (Exception e) {
			LOGGER.error("Error adding aliases: ", e);
		}
	}

	/** Retrieves all users from Teamscale. */

	private User[] getUsers(HttpClientContext context) throws IOException {

		HttpGet get = new HttpGet(config.serverUrl() + USER_ENDPOINT);
		get.setHeader("accept", "application/json");
		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
			HttpResponse response = client.execute(get, context);

			return this.gson.fromJson(new InputStreamReader(response.getEntity().getContent()), User[].class);
		}
	}

	/**
	 * Generates the user aliases and adds these to Teamscale if new ones are
	 * present.
	 */
	private void addAliases(HttpClientContext context, User user) throws IOException {
		Set<String> aliases = new HashSet<String>();
		for (AliasRule aliasRule : this.config.getAliasRules()) {
			aliases.addAll(aliasRule.getAliases(user.getUsername()));
		}

		if (this.config.addEmail() && !Strings.isNullOrEmpty(user.getEmailAddress())) {
			aliases.add(user.getEmailAddress());
		}

		aliases.removeAll(user.getAliases());
		if (aliases.size() == 0) {
			// skip if no aliases are to be generated.
			return;
		}

		LOGGER.info("New aliases for " + user.getUsername() + ": " + String.join(",", aliases));

		if (this.config.dryRun()) {
			return;
		}

		user.getAliases().addAll(aliases);

		HttpPut put = new HttpPut(this.config.serverUrl() + USER_ENDPOINT);
		put.setEntity(new StringEntity(this.gson.toJson(user), ContentType.APPLICATION_JSON));
		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
			client.execute(put, context);
		}
	}
}
