/*
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      https://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package org.apache.commons.cli;

import java.util.ArrayList;
import java.util.List;

/**
 * The class GnuParser provides an implementation of the
 * {@link Parser#flatten(Options, String[], boolean) flatten}
 * method.
 *
 * @deprecated since 1.3, use the {@link DefaultParser} instead
 */
@Deprecated
public class GnuParser extends Parser {

    /**
     * Constructs a new instance.
     */
    public GnuParser() {
        // empty
    }

    /**
     * This flatten method does so using the following rules:
     * <ol>
     * <li>If an {@link Option} exists for the first character of the
     * {@code arguments} entry <strong>AND</strong> an
     * {@link Option} does not exist for the whole {@code argument} then add the
     * first character as an option to the
     * processed tokens list for example "-D" and add the rest of the entry to the
     * also.</li>
     * <li>Otherwise just add the token to the processed tokens list.</li>
     * </ol>
     *
     * @param options         The Options to parse the arguments by.
     * @param arguments       The arguments that have to be flattened.
     * @param stopAtNonOption specifies whether to stop flattening when a non option
     *                        has been encountered
     * @return a String array of the flattened arguments
     */
    @Override
    protected String[] flatten(final Options options, final String[] arguments, final boolean stopAtNonOption) {
        final List<String> tokens = new ArrayList<>();
        boolean eatTheRest = false;

        for (int i = 0; i < arguments.length; i++) {
            final String arg = arguments[i];
            if (arg != null) {
                if (isEndOfOptions(arg)) {
                    eatTheRest = true;
                    tokens.add(arg);
                } else if (isOptionArgument(arg)) {
                    eatTheRest = processOptionArgument(options, arg, tokens, stopAtNonOption);
                } else {
                    tokens.add(arg);
                }

                if (eatTheRest) {
                    addRemainingArguments(arguments, tokens, i);
                    break;
                }
            }
        }

        return tokens.toArray(Util.EMPTY_STRING_ARRAY);
    }

    /**
     * Checks if the argument indicates end of options parsing.
     */
    private boolean isEndOfOptions(final String arg) {
        return "--".equals(arg);
    }

    /**
     * Checks if the argument is an option (starts with hyphen but is not just "-").
     */
    private boolean isOptionArgument(final String arg) {
        return arg.startsWith("-") && !"-".equals(arg);
    }

    /**
     * Processes an option argument and adds appropriate tokens.
     * 
     * @param options         the options to check against
     * @param arg             the argument to process
     * @param tokens          the list to add tokens to
     * @param stopAtNonOption whether to stop at non-options
     * @return true if should eat the rest of arguments
     */
    private boolean processOptionArgument(final Options options, final String arg, final List<String> tokens,
            final boolean stopAtNonOption) {
        final String opt = Util.stripLeadingHyphens(arg);

        if (options.hasOption(opt)) {
            tokens.add(arg);
            return false;
        }

        if (handleOptionWithEquals(options, arg, opt, tokens)) {
            return false;
        }

        if (handlePropertyOption(options, arg, tokens)) {
            return false;
        }

        // Not a recognized option
        tokens.add(arg);
        return stopAtNonOption;
    }

    /**
     * Handles options with equals sign (--foo=value or -foo=value).
     * 
     * @return true if the option was handled
     */
    private boolean handleOptionWithEquals(final Options options, final String arg, final String opt,
            final List<String> tokens) {
        final int equalPos = DefaultParser.indexOfEqual(opt);
        if (equalPos != -1 && options.hasOption(opt.substring(0, equalPos))) {
            // the format is --foo=value or -foo=value
            tokens.add(arg.substring(0, arg.indexOf(Char.EQUAL))); // --foo
            tokens.add(arg.substring(arg.indexOf(Char.EQUAL) + 1)); // value
            return true;
        }
        return false;
    }

    /**
     * Handles property options (-Dproperty=value).
     * 
     * @return true if the option was handled
     */
    private boolean handlePropertyOption(final Options options, final String arg, final List<String> tokens) {
        if (arg.length() >= 2 && options.hasOption(arg.substring(0, 2))) {
            // the format is a special properties option (-Dproperty=value)
            tokens.add(arg.substring(0, 2)); // -D
            tokens.add(arg.substring(2)); // property=value
            return true;
        }
        return false;
    }

    /**
     * Adds all remaining arguments to the tokens list.
     */
    private void addRemainingArguments(final String[] arguments, final List<String> tokens, int startIndex) {
        for (int i = startIndex + 1; i < arguments.length; i++) {
            tokens.add(arguments[i]);
        }
    }

}
