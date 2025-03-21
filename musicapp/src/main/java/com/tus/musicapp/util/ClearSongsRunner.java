package com.tus.musicapp.util;

import com.tus.musicapp.repos.MusicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ClearSongsRunner implements CommandLineRunner {

    @Autowired
    private MusicRepository musicRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (args.length > 0 && args[0].equals("clearSongs")) {
            try {
                musicRepository.deleteAll();
                System.out.println("✅ All songs cleared successfully.");
            } catch (Exception e) {
                System.err.println("❌ Error clearing songs: " + e.getMessage());
            }
        } else {
            System.out.println("🔹 ClearSongsRunner: No action taken.");
        }
    }
}
