package com.tus.musicapp.test.karate;

import com.intuit.karate.junit5.Karate;

public class TestMusicEndpoints {

    @Karate.Test
    Karate testMusicEndpoints() {
        return Karate.run(
            "classpath:features/getSongs.feature",
            "classpath:features/getUsersSongs.feature",
            "classpath:features/saveAndRemoveSong.feature",
//            "classpath:features/assignSongToUser.feature",
            "classpath:features/getGenres.feature"
        ).relativeTo(getClass());  
    }
}
