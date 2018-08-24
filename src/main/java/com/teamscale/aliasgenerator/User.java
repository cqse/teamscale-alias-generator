package com.teamscale.aliasgenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This describes a single user.
 */
@SuppressWarnings("unused") // Some fields are just needed for JSON serialization.
public class User {

	/** The name of the user. */
	private String username;

	/** The user's first name. */
	private String firstName;

	/** The user's last name. */
	private String lastName;

	/** The user's email address. */
	private String emailAddress;

	/**
	 * If <code>true</code>, Gravatar will be used instead of the Teamscale avatar.
	 */
	private boolean useGravatar = false;

	/**
	 * A list of aliases for the user, e.g. login names, email addresses, LDAP IDs
	 * etc.
	 */
	private List<String> aliases = new ArrayList<String>();

	/**
	 * An authentication string. The details of this string depend on the
	 * authenticator implementation. This might be a (hashed) password or a
	 * reference to an LDAP server to use. This may be null during testing or for
	 * temporary users.
	 */
	private String authenticator;

	/**
	 * The IDs of all {@link UserGroup}s to which the user is assigned. We use this
	 * indirection since it would be impractical to also serialize all groups here.
	 */
	private Set<String> groupIds = new HashSet<String>();

	/** Map that can be used to store additional details */
	private HashMap<String, String> additionalDetails = new HashMap<>();

	/** @see #emailAddress */
	public String getEmailAddress() {
		return emailAddress;
	}

	/** Returns username. */
	public String getUsername() {
		return username;
	}

	/** Returns aliases. */
	public List<String> getAliases() {
		return aliases;
	}

	/** Sets aliases. */
	public void setAliases(List<String> aliases) {
		this.aliases = aliases;
	}

}
