/*
 * Copyright 2021 Niklas van Schrick and the IntelliJ GitLab Integration contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.gitlab.taucher2003.gitlab.integration.model;

import com.gitlab.taucher2003.gitlab.integration.util.RemoteFinder;
import git4idea.repo.GitRemote;

import java.util.Objects;

public class RemoteMapping {

    private final String name;
    private final String url;
    private final String protocol;
    private final String instanceUrl;
    private final String repositoryPath;

    public RemoteMapping(String name, String url) {
        this.name = name;
        this.url = url;
        this.protocol = RemoteFinder.findProtocol(url);
        this.instanceUrl = RemoteFinder.findBase(url);
        this.repositoryPath = RemoteFinder.findPath(url);
    }

    public static RemoteMapping of(GitRemote remote) {
        return new RemoteMapping(remote.getName(), remote.getFirstUrl());
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getInstanceUrl() {
        return instanceUrl;
    }

    public String getFullInstanceUrl() {
        return protocol + getInstanceUrl();
    }

    public String getRepositoryPath() {
        return repositoryPath;
    }

    public boolean remoteEquals(RemoteMapping other) {
        return Objects.equals(other.getRepositoryPath(), getRepositoryPath()) && Objects.equals(other.getInstanceUrl(), getInstanceUrl());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        var mapping = (RemoteMapping) o;
        return Objects.equals(name, mapping.name) && Objects.equals(url, mapping.url) && Objects.equals(instanceUrl, mapping.instanceUrl) && Objects.equals(repositoryPath, mapping.repositoryPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, url, instanceUrl, repositoryPath);
    }

    @Override
    public String toString() {
        return "RemoteMapping{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", instanceUrl='" + instanceUrl + '\'' +
                ", repositoryPath='" + repositoryPath + '\'' +
                '}';
    }
}
