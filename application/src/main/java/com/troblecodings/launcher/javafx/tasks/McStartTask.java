package com.troblecodings.launcher.javafx.tasks;

import com.troblecodings.launcher.models.girjson.GirJson;
import com.troblecodings.launcher.util.NetUtils;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public final class McStartTask extends Task<McStartInfo> {
    private static final URI girJsonAddress = URI.create("https://girc.eu/Launcher/GIR.json");
    private static final URI minecraftAssetsAddress = URI.create("https://resources.download.minecraft.net/");
    private static final Logger logger = LoggerFactory.getLogger(McStartTask.class);

    private long totalWork = 0;

    @Override
    protected McStartInfo call() throws Exception {
        logger.info("Minecraft start requested");
        String libraryPath = "";

        // TODO: Instead of download, compare hash to local GIR.json
        updateMsgDownload("GIR.json");

        var optJson = NetUtils.downloadJson(girJsonAddress, GirJson.class);
        var girJson = optJson.orElseThrow();

        // Account for GIR.json
        totalWork = girJson.totalSize() + 1;
        newUpdateProgress(1);

        var progress = 0L;

        throw new Exception("Test");
        //return new McStartInfo(girJson.mainClass(), "");
    }

    private void newUpdateProgress(long progress) {
        updateProgress(progress, totalWork);
    }

    private void updateMsgDownload(String asset) {
        updateMessage("Downloading $asset...");
    }
}
