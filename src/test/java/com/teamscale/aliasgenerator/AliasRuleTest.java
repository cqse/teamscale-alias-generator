package com.teamscale.aliasgenerator;

import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * Test case for {@link AliasRule} some known rules.
 */
public class AliasRuleTest {

	/**
	 * Tests whether the DOM domain is added for non dev and admin accounts.
	 */
	@Test
	public void testAddDOMDomain() {
		AliasRule rule = new AliasRule("(\\w+\\d+)", "DOM\\\\$1", true);

		assertAliases(rule, "no-match");
		assertAliases(rule, "o123456", "DOM\\o123456");
		assertAliases(rule, "o123456d");
		assertAliases(rule, "o123456a");
		assertAliases(rule, "op123456", "DOM\\op123456");
		assertAliases(rule, "op123456d");
		assertAliases(rule, "op123456a");
	}

	/** Tests if the dev domain is added for dev and admin accounts. */
	@Test
	public void testAddDevDomain() {
		AliasRule rule = new AliasRule("(\\w+\\d+(a|d))", "DEV\\\\$1", true);

		assertAliases(rule, "no-match");
		assertAliases(rule, "o123456");
		assertAliases(rule, "o123456d", "DEV\\o123456d");
		assertAliases(rule, "o123456a", "DEV\\o123456a");
		assertAliases(rule, "op123456");
		assertAliases(rule, "op123456d", "DEV\\op123456d");
		assertAliases(rule, "op123456a", "DEV\\op123456a");
	}

	/** Tests whether "d" users will get a non-d alias. */
	@Test
	public void testMinusDAlias() {
		AliasRule rule = new AliasRule("(\\w+\\d+)", "$1", false);

		assertAliases(rule, "no-match");
		assertAliases(rule, "o123456", "O123456");
		assertAliases(rule, "o123456d");
		assertAliases(rule, "o123456a");
		assertAliases(rule, "op123456", "OP123456");
		assertAliases(rule, "op123456d");
		assertAliases(rule, "op123456a");
	}

	/** Asserts that the generated aliases are equal to the expected ones. */
	private static void assertAliases(AliasRule rule, String username, String... expectedAliases) {
		Set<String> aliases = rule.getAliases(username);
		Assertions.assertThat(aliases).containsExactlyInAnyOrder(expectedAliases);
	}

}
