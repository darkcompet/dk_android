/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.util;

class MyStringTrimmer {
    static String trimExtras(String msg, char[] delimiters) {
        if (msg == null || msg.length() == 0) {
            return msg;
        }

        boolean fromLeft = true;
        boolean fromRight = true;
        final boolean shouldCheckTargets = (delimiters != null);
        final int N = msg.length();
        int startIndex = 0, endIndex = N - 1;

        while (startIndex <= endIndex && (fromLeft || fromRight)) {
            // Check from left to right
            if (fromLeft) {
                char current = msg.charAt(startIndex);
                boolean stopCheck = true;

                // Check whether character insides targets
                if (shouldCheckTargets) {
                    for (int i = delimiters.length - 1; i >= 0; --i) {
                        if (current == delimiters[i]) {
                            stopCheck = false;
                            ++startIndex;
                            break;
                        }
                    }
                }
                // Stop checking whitespace since found this character in targets
                if (stopCheck) {
                    fromLeft = false;
                }
                // Check whether the character is whitespace
                else if (Character.isWhitespace((int) current)) {
                    ++startIndex;
                }
            }

            // Check from right to left
            if (fromRight) {
                char current = msg.charAt(endIndex);
                boolean stopCheck = true;

                // Check whether the character insides targets
                if (shouldCheckTargets) {
                    for (int i = delimiters.length - 1; i >= 0; --i) {
                        if (current == delimiters[i]) {
                            stopCheck = false;
                            --endIndex;
                            break;
                        }
                    }
                }
                // Stop checking whitespace since found this character in targets
                if (stopCheck) {
                    fromRight = false;
                }
                // Check whether the character is whitespace
                else if (Character.isWhitespace((int) current)) {
                    --endIndex;
                }
            }
        }

        return (endIndex < startIndex) ? "" : msg.substring(startIndex, endIndex + 1);
    }

    static String trimExact(String msg, char[] targets) {
        if (msg == null || msg.length() == 0 || targets == null || targets.length == 0) {
            return msg;
        }

        boolean fromLeft = true;
        boolean fromRight = true;
        final int N = msg.length();
        int startIndex = 0, endIndex = N - 1;

        while (startIndex <= endIndex && (fromLeft || fromRight)) {
            // check from left to right
            if (fromLeft) {
                char current = msg.charAt(startIndex);
                boolean stopCheck = true;
                // check whether current insides targets
                for (int i = targets.length - 1; i >= 0; --i) {
                    if (current == targets[i]) {
                        stopCheck = false;
                        ++startIndex;
                        break;
                    }
                }
                if (stopCheck) {
                    fromLeft = false;
                }
            }
            // check from right to left
            if (fromRight) {
                char current = msg.charAt(endIndex);
                boolean stopCheck = true;
                // check whether current insides targets
                for (int i = targets.length - 1; i >= 0; --i) {
                    if (current == targets[i]) {
                        stopCheck = false;
                        --endIndex;
                        break;
                    }
                }

                if (stopCheck) {
                    fromRight = false;
                }
            }
        }

        return (endIndex < startIndex) ? "" : msg.substring(startIndex, endIndex + 1);
    }
}
