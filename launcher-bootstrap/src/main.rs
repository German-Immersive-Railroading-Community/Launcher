use chrono::{DateTime, Utc};
use http::Uri;
use log::{error, info, warn};
use serde::{Deserialize, Serialize};
use velopack::{
    Error, UpdateCheck, UpdateManager, VelopackApp, VelopackAsset, VelopackAssetFeed, bundle,
    download::{self, download_url_as_string},
    sources::UpdateSource,
};

/// Represents an individual asset attached to a GitHub release.
#[derive(Debug, Serialize, Deserialize, Clone)]
struct GitHubReleaseAsset {
    /// The asset url for this release asset,
    url: String,
    browser_download_url: String,
    name: String,
    content_type: String,
}

#[derive(Debug, Serialize, Deserialize, Clone)]
struct GitHubRelease {
    name: String,
    prerelease: bool,
    published_at: DateTime<Utc>,
    assets: Vec<GitHubReleaseAsset>,
}

#[derive(Clone)]
struct GitHubSource {
    repo_uri: Uri,
    access_token: Option<String>,

    /// If true, downloads the latest pre-release. If false, downloads the latest stable release.
    pre_release: bool,

    // Exists so that we can retrieve release info easily when we download an asset
    releases: Vec<GitHubRelease>,
}

impl GitHubSource {
    const PAGE: i32 = 1;
    const PER_PAGE: i32 = 10;

    /// Creates a new [`GitHubSource`].
    pub fn new(repo_url: String, access_token: Option<String>, pre_release: bool) -> Self {
        let repo_uri: Uri = repo_url.trim_end_matches('/').parse().unwrap();

        Self {
            repo_uri,
            access_token,
            pre_release,
            releases: Vec::new(),
        }
    }

    pub fn get_releases(&self, include_pre_releases: bool) -> Result<Vec<GitHubRelease>, Error> {
        let release_uri = format!(
            "{}repos{}/releases?per_page={}&page={}",
            self.get_api_base_uri(),
            self.repo_uri.path(),
            Self::PER_PAGE,
            Self::PAGE
        );

        let response = download_url_as_string(&release_uri)?;

        let mut releases: Vec<GitHubRelease> =
            serde_json::from_str(&response).unwrap_or(Vec::new());

        if !include_pre_releases {
            releases.retain(|x| !x.prerelease);
        }

        // Sort in descending order from when the release was published
        releases.sort_unstable_by(|a, b| b.published_at.cmp(&a.published_at));

        Ok(releases)
    }

    // TODO: implement enterprise github servers
    // This method can be moved into a generic git trait
    fn get_api_base_uri(&self) -> Uri {
        Uri::from_static("https://api.github.com/")
    }

    fn get_asset_url(&self, release: &GitHubRelease, file_name: &String) -> Result<String, Error> {
        if release.assets.is_empty() {
            return Err(Error::Generic(
                format!("No assets found in GitHub Release '{}'!", release.name).into(),
            ));
        }

        let asset = if let Some(asset) = release.assets.iter().find(|x| x.name == *file_name) {
            asset
        } else {
            return Err(Error::Generic(format!(
                "No asset matching {} found!",
                file_name
            )));
        };

        if !asset.url.is_empty() && self.access_token.is_some() {
            Ok(asset.url.clone())
        } else {
            Ok(asset.browser_download_url.clone())
        }
    }
}

impl UpdateSource for GitHubSource {
    fn get_release_feed(
        &self,
        channel: &str,
        _app: &bundle::Manifest,
        _staged_user_id: &str,
    ) -> Result<VelopackAssetFeed, Error> {
        let releases = self.get_releases(self.pre_release)?;

        if releases.len() == 0 {
            warn!("No releases found at {}!", self.repo_uri);
            return Ok(VelopackAssetFeed::default());
        }

        let release_name = format!("releases.{channel}.json");
        let mut asset_feed = VelopackAssetFeed::default();

        for release in releases {
            let asset_url = self.get_asset_url(&release, &release_name)?;

            let release_info = download::download_url_as_string(&asset_url)?;
            let feed: VelopackAssetFeed = serde_json::from_str(&release_info)?;
            asset_feed.Assets.append(&mut feed.Assets.clone());
        }

        Ok(asset_feed)
    }

    fn download_release_entry(
        &self,
        asset: &VelopackAsset,
        local_file: &str,
        progress_sender: Option<std::sync::mpsc::Sender<i16>>,
    ) -> Result<(), Error> {
        if let Some(release) = self
            .releases
            .iter()
            .find(|r| r.assets.iter().any(|r| r.name == asset.FileName))
        {
            if let Some(asset) = release.assets.iter().find(|a| a.name == asset.FileName) {
                info!(
                    "About to download GitHub release from URL '{}' to file '{}'",
                    asset.url, local_file
                );

                download::download_url_to_file(&asset.url, local_file, move |p| {
                    if let Some(progress_sender) = &progress_sender {
                        let _ = progress_sender.send(p);
                    }
                })?;
            } else {
                return Err(Error::Generic("Couldn't find correct asset whoops".into()));
            }
        } else {
            return Err(Error::Generic("Couldn't find correct release whoops".into()));
        }

        Ok(())
    }

    fn clone_boxed(&self) -> Box<dyn UpdateSource> {
        Box::new(self.clone())
    }
}

fn main() -> Result<(), Box<dyn std::error::Error>> {
    tracing_subscriber::fmt().init();

    VelopackApp::build().run();

    let source = GitHubSource::new(
        "https://github.com/German-Immersive-Railroading-Community/Launcher".to_owned(),
        None,
        true,
    );

    match UpdateManager::new(source, None, None) {
        Ok(um) => match um.check_for_updates() {
            Ok(updates) => match updates {
                UpdateCheck::UpdateAvailable(update_info) => {
                    info!(
                        "Update available! {}",
                        update_info.TargetFullRelease.Version
                    );
                    um.download_updates(&update_info, None)?;
                    um.apply_updates_and_restart(&update_info.TargetFullRelease)?;
                }
                UpdateCheck::RemoteIsEmpty => info!("No updates in remote"),
                UpdateCheck::NoUpdateAvailable => info!("No new updates available"),
            },
            Err(e) => error!("Failed checking for updates: {}", e),
        },
        Err(e) => error!("Failed constructing update manager: {}", e),
    };

    Ok(())
}
