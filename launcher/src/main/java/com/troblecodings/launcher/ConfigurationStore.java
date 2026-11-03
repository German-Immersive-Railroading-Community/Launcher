package com.troblecodings.launcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public final class ConfigurationStore {
    private static final Logger LOGGER = LogManager.getLogger(ConfigurationStore.class.getName());

    private final ObjectMapper mapper;
    private final Path file;

    public ConfigurationStore(ObjectMapper mapper, Path configDir) {
        this.mapper = mapper;
        file = configDir.resolve("config.json").normalize();
    }

    public Path getFilePath() {
        return file;
    }

    public GirConfig load() {
        if(Files.exists(file)) {
            try {
                return mapper.readValue(file.toFile(), GirConfig.class);
            } catch (final IOException | RuntimeException ex) {
                LOGGER.warn("Unable to read configuration, using defaults.", ex);
            }
        }

        return GirConfig.defaults();
    }

    public void save(GirConfig config) {
        Objects.requireNonNull(config);

        try {
            Files.createDirectories(file.getParent());
            var temp = Files.createTempFile(file.getParent(), "", ".tmp");
            mapper.writerWithDefaultPrettyPrinter().writeValue(temp.toFile(), config);
            try {
                Files.move(temp, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException ignored) {
                Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (final IOException ioe) {
            LOGGER.error("Failed to save configuration!", ioe);
        }
    }
}
