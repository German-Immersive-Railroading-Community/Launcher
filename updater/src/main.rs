use velopack::VelopackApp;
use std::process::Command;

fn main() -> std::io::Result<()> {
    VelopackApp::build().run();

    let mut command: Command;

    #[cfg(target_os = "windows")]
    {
        command = Command::new("./GIRC-Launcher/GIRC-Launcher.exe");
    }

    #[cfg(target_os = "linux")]
    #[cfg(target_os = "macos")]
    {
        command = Command::new("./GIRC-Launcher/GIRC-Launcher");
    }

    let status = command.status()?;
    println!("Process exited with: {status}");
    Ok(())
}
