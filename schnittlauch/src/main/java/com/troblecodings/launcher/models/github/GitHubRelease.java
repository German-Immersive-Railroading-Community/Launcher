package com.troblecodings.launcher.models.github;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public final class GitHubRelease {
    private String url;

    @SerializedName("html_url")
    private String htmlUrl;

    @SerializedName("assets_url")
    private String assetsUrl;

    @SerializedName("tarball_url")
    private String tarballUrl;

    @SerializedName("zipball_url")
    private String zipballUrl;

    @SerializedName("discussion_url")
    private String discussionUrl;

    private int id;

    @SerializedName("node_id")
    private String nodeId;

    @SerializedName("tag_name")
    private String tagName;

    @SerializedName("target_commitish")
    private String targetCommitish;

    private String name;

    private String body;

    private boolean draft;

    private boolean prerelease;

    private boolean immutable;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("published_at")
    private String publishedAt;

    private List<GitHubAsset> assets;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public String getAssetsUrl() {
        return assetsUrl;
    }

    public void setAssetsUrl(String assetsUrl) {
        this.assetsUrl = assetsUrl;
    }

    public String getTarballUrl() {
        return tarballUrl;
    }

    public void setTarballUrl(String tarballUrl) {
        this.tarballUrl = tarballUrl;
    }

    public String getZipballUrl() {
        return zipballUrl;
    }

    public void setZipballUrl(String zipballUrl) {
        this.zipballUrl = zipballUrl;
    }

    public String getDiscussionUrl() {
        return discussionUrl;
    }

    public void setDiscussionUrl(String discussionUrl) {
        this.discussionUrl = discussionUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTargetCommitish() {
        return targetCommitish;
    }

    public void setTargetCommitish(String targetCommitish) {
        this.targetCommitish = targetCommitish;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isDraft() {
        return draft;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    public boolean isPrerelease() {
        return prerelease;
    }

    public void setPrerelease(boolean prerelease) {
        this.prerelease = prerelease;
    }

    public boolean isImmutable() {
        return immutable;
    }

    public void setImmutable(boolean immutable) {
        this.immutable = immutable;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public List<GitHubAsset> getAssets() {
        return assets;
    }

    public void setAssets(List<GitHubAsset> assets) {
        this.assets = assets;
    }
}
