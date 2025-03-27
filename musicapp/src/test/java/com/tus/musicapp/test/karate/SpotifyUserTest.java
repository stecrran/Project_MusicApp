package com.tus.musicapp.test.karate;

import com.intuit.karate.junit5.Karate;

public class SpotifyUserTest {

	@Karate.Test
	Karate testManageUsers() {
        return Karate.run(
        		"classpath:features/spotifyUser.feature",
        		"classpath:features/nonAdminGetUserFail.feature",
                "classpath:features/adminOnlyUserCreation.feature"
            ).relativeTo(getClass());  
	}
}
