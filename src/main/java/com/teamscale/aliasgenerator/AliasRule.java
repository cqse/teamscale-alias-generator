package com.teamscale.aliasgenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;

/**
 * Rule for generating Teamscale user aliases.
 */
public class AliasRule {
	/**
	 * The regex that has to match an username in order to apply the transformation.
	 */
	private String match;

	/** The replacement pattern which generates the alias. */
	private String replace;

	/**
	 * If false, a lowercased and uppercased variant of the username is returned in
	 * addition to the original string.
	 */
	private boolean caseSensitive = true;

	/** The compiled regex pattern. Lazy initialized. */
	private Pattern compiledRegex;

	/** Constructor. */
	public AliasRule(String match, String replace, boolean caseSensitive) {
		this.match = match;
		this.replace = replace;
		this.caseSensitive = caseSensitive;
	}

	/**
	 * Returns the generated aliases or an empty set if the rule does not match.
	 */
	public Set<String> getAliases(String username) {
		Set<String> aliases = new HashSet<String>();
		Pattern regex = getCompiledRegex();
		Matcher matcher = regex.matcher(username);
		if (matcher.matches()) {
			String alias = matcher.replaceAll(replace);
			aliases.add(alias);
			if (!caseSensitive) {
				aliases.add(alias.toLowerCase());
				aliases.add(alias.toUpperCase());
			}
			aliases.remove(username); // the actual username is not aliased
		}
		return aliases;
	}

	/** Compiles the matching regex and returns it. */
	private Pattern getCompiledRegex() {
		if (compiledRegex == null) {
			compiledRegex = Pattern.compile(match);
		}

		return compiledRegex;
	}

	/**
	 * Returns a array of {@link AliasRule}s that is parsed from a json file.
	 */
	public static AliasRule[] rulesFromJson(String file) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(file))) {
			return new Gson().fromJson(reader, AliasRule[].class);
		}
	}
}
