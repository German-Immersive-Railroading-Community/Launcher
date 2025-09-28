use std::{thread::Thread, time::Duration};

use chrono::{DateTime, Utc};
use http::Uri;
use serde::{Deserialize, Serialize};
use velopack::{
    Error, UpdateCheck, UpdateManager, VelopackApp, VelopackAsset, VelopackAssetFeed, bundle,
    download::download_url_as_string,
    locator::VelopackLocatorConfig,
    sources::{FileSource, UpdateSource},
};

/// Represents an individual asset attached to a GitHub release.
#[derive(Debug, Serialize, Deserialize, Clone)]
struct GitHubReleaseAsset {
    /// The asset url for this release asset,
    url: String,
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
}

impl UpdateSource for GitHubSource {
    fn get_release_feed(
        &self,
        channel: &str,
        app: &bundle::Manifest,
        staged_user_id: &str,
    ) -> Result<VelopackAssetFeed, Error> {
        Ok(serde_json::from_str("")?)
    }

    fn download_release_entry(
        &self,
        asset: &VelopackAsset,
        local_file: &str,
        progress_sender: Option<std::sync::mpsc::Sender<i16>>,
    ) -> Result<(), Error> {
        Ok(())
    }

    fn clone_boxed(&self) -> Box<dyn UpdateSource> {
        Box::new(self.clone())
    }
}

fn main() -> Result<(), Box<dyn std::error::Error>> {
    VelopackApp::build().run();

    let source = GitHubSource::new(
        "https://github.com/German-Immersive-Railroading-Community/Launcher".to_owned(),
        None,
        false,
    );

    let releases = source.get_releases(true)?;

    println!("{:#?}", releases[0]);

    let um = UpdateManager::new(source, None, None)?;

    let updates = um.check_for_updates()?;

    match updates {
        UpdateCheck::UpdateAvailable(update_info) => {
            println!("{:#?}", update_info);
            um.download_updates(&update_info, None)?;
            um.apply_updates_and_restart(&update_info.TargetFullRelease)?;
        }
        UpdateCheck::RemoteIsEmpty => println!("No updates in remote"),
        UpdateCheck::NoUpdateAvailable => println!("No new updates available"),
    }

    std::thread::sleep(Duration::from_secs(5));

    Ok(())
}
