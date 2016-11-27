package com.kafein.aveamerkez;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by AUT via kafein on 20.11.2016.
 */
public class TestCustomMatcher {

    @Test
    public void testRegularExpressionMatcher() throws Exception {
        String s ="aaabbbaaaa";

        assertThat(s, RegexMatcher.matchesRegex("a*b*a*"));
    }

}
