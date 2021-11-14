/*
 * Copyright 2021 Niklas van Schrick and the IntelliJ GitLab Integration contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.gitlab.taucher2003.gitlab.integration.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RemoteFinder {

    private static final Pattern GIT_REMOTE = Pattern.compile(
            "(?<protocol>https?://|(?:ssh://)?\\w+@)(?<host>[^:/]+)[:/](?<path>[\\w-/]+)\\.git", Pattern.CASE_INSENSITIVE);
    private static final Pattern WEB_URL = Pattern.compile(
            "(?<protocol>https?://)(?<host>[^:/]+)/(?<path>.*)(?:/-/)+?.*", Pattern.CASE_INSENSITIVE);

    private RemoteFinder() {
    }

    public static String findBaseFromWeb(CharSequence webUrl) {
        return matchUrl(webUrl, WEB_URL).group("host");
    }

    public static String findPathFromWeb(CharSequence webUrl) {
        return matchUrl(webUrl, WEB_URL).group("path");
    }

    public static String findProtocolFromWeb(CharSequence webUrl) {
        return matchUrl(webUrl, WEB_URL).group("protocol");
    }

    public static String getInstanceUrlFromWeb(CharSequence webUrl) {
        return findProtocolFromWeb(webUrl) + findBaseFromWeb(webUrl);
    }

    public static String findBase(CharSequence remoteUrl) {
        return matchUrl(remoteUrl, GIT_REMOTE).group("host");
    }

    public static String findPath(CharSequence remoteUrl) {
        return matchUrl(remoteUrl, GIT_REMOTE).group("path");
    }

    public static String findProtocol(CharSequence remoteUrl) {
        var protocol = matchUrl(remoteUrl, GIT_REMOTE).group("protocol");
        if(protocol.startsWith("http")) {
            return protocol;
        }
        return "http://"; // setting to http as most servers redirect to https if possible
    }

    public static String getProjectUrl(CharSequence remoteUrl) {
        return findProtocol(remoteUrl) + findBase(remoteUrl) + "/" + findPath(remoteUrl);
    }

    private static Matcher matchUrl(CharSequence url, Pattern pattern) {
        var matcher = pattern.matcher(url);
        matcher.find();
        return matcher;
    }
}
