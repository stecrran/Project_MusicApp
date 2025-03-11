window.UserPlayList = {
  data() {
    return {
      playlists: [],
      tracks: [],
      selectedPlaylist: null,
      loading: false,
      error: null,
      accessToken: localStorage.getItem("spotify_token") || null,
      fallbackImage: "/assets/images/music_640.png",
      spotifyUrl: "https://open.spotify.com/"
    };
  },
  created() {
    console.log("✅ UserPlayList component is created!");
    this.handleSpotifyCallback();
    this.checkSpotifyAuth();
  },
  watch: {
    accessToken(newToken) {
      if (newToken) {
        console.log("🔄 Token changed, fetching playlists...");
        this.fetchUserPlaylists();
      }
    }
  },
  methods: {
    checkSpotifyAuth() {
      const storedToken = localStorage.getItem("spotify_token");
      const tokenExpiry = localStorage.getItem("spotify_token_expiry");

      if (storedToken && tokenExpiry && Date.now() < parseInt(tokenExpiry)) {
        console.log("🔑 Using stored access token:", storedToken);
        this.accessToken = storedToken;
        this.fetchUserPlaylists(); // ✅ Fetch playlists immediately
      } else {
        console.log("🔑 No valid token found. User must log in.");
        localStorage.removeItem("spotify_token");
        localStorage.removeItem("spotify_token_expiry");
        this.accessToken = null;
      }
    },

    authenticateWithSpotify() {
      console.log("🟢 Starting Spotify Authentication...");
      const clientId = "95ac7e92f6d2415d82a2992f37e23a5b";
      const redirectUri = encodeURIComponent(window.location.origin + window.location.pathname);
      const scope = encodeURIComponent("user-read-private playlist-read-private playlist-read-collaborative");

      const authUrl = `https://accounts.spotify.com/authorize?client_id=${clientId}&response_type=token&redirect_uri=${redirectUri}&scope=${scope}`;

      console.log("🔑 Redirecting to Spotify login:", authUrl);
      window.location.assign(authUrl);
    },

    handleSpotifyCallback() {
      console.log("🔄 Checking for Spotify token in URL...");

      if (window.location.hash.includes("access_token")) {
        const urlParams = new URLSearchParams(window.location.hash.substring(1));
        const newToken = urlParams.get("access_token");

        if (newToken) {
          console.log("✅ New token found:", newToken);
          const expiresIn = 3600 * 1000; // Token expires in 1 hour

          // ✅ Store token in localStorage
          localStorage.setItem("spotify_token", newToken);
          localStorage.setItem("spotify_token_expiry", Date.now() + expiresIn);

          console.log("✅ Token saved successfully!");
          
          // ✅ Remove token from URL (prevents page refresh issue)
          window.history.replaceState({}, document.title, window.location.pathname);

          // ✅ Update Vue state & fetch playlists
          this.accessToken = newToken;
          this.fetchUserPlaylists();
        } else {
          console.error("❌ No access token found in URL.");
        }
      }
    },
    
    logoutSpotify() {
      console.log("🚪 Logging out from Spotify...");
      localStorage.removeItem("spotify_token");
      localStorage.removeItem("spotify_token_expiry");

      this.accessToken = null;
      this.playlists = [];
      this.tracks = [];
      this.selectedPlaylist = null;
      this.error = null;

      console.log("🔴 Logged out successfully.");
    },

    fetchUserPlaylists() {
      if (!this.accessToken) {
        console.error("❌ No valid Spotify access token.");
        this.error = "❌ No valid Spotify access token.";
        return;
      }

      console.log("🎵 Fetching User Playlists with token:", this.accessToken);
      this.loading = true;

      $.ajax({
        url: "https://api.spotify.com/v1/me/playlists",
        method: "GET",
        headers: {
          "Authorization": `Bearer ${this.accessToken}`
        },
        success: (data) => {
          console.log("✅ Received playlist data:", data);

          if (!data.items || data.items.length === 0) {
            console.warn("⚠️ No playlists found.");
            this.error = "⚠️ No playlists available.";
            return;
          }

          this.playlists = data.items.map(playlist => ({
            id: playlist.id,
            name: playlist.name,
            totalTracks: playlist.tracks.total,
            image: (playlist.images && playlist.images.length > 0) ? playlist.images[0].url : this.fallbackImage,
            spotifyUrl: playlist.external_urls.spotify
          }));

          console.log("🎶 Processed Playlists:", this.playlists);
        },
        error: (xhr, status, error) => {
          console.error("❌ Error Fetching Playlists:", xhr.responseText);
          this.error = `❌ API Request Failed: ${xhr.status} - ${xhr.responseText}`;
        },
        complete: () => {
          this.loading = false;
        }
      });
    },

    fetchPlaylistTracks(playlistId) {
      if (!this.accessToken) {
        this.error = "❌ No valid Spotify access token.";
        return;
      }

      console.log(`🎵 Fetching tracks for playlist ID: ${playlistId}...`);
      this.selectedPlaylist = this.playlists.find(p => p.id === playlistId);
      this.tracks = [];

      $.ajax({
        url: `https://api.spotify.com/v1/playlists/${playlistId}/tracks`,
        method: "GET",
        headers: {
          "Authorization": `Bearer ${this.accessToken}`
        },
        success: (data) => {
          console.log("✅ Tracks Data:", data);

          this.tracks = data.items.map(item => ({
            id: item.track.id,
            name: item.track.name,
            artist: item.track.artists.map(a => a.name).join(", "),
            album: item.track.album.name,
            image: item.track.album.images?.[0]?.url || this.fallbackImage,
            spotifyUrl: item.track.external_urls.spotify,
            durationMs: item.track.duration_ms
          }));

          console.log("🎶 Updated Tracks:", this.tracks);
        },
        error: (xhr, status, error) => {
          console.error("❌ Error Fetching Tracks:", xhr.responseText);
        }
      });
    },

    saveSongToDatabase(track) {
      console.log("💾 Saving song to database:", track);

      fetch("/api/music/save", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${localStorage.getItem("jwt")}`
        },
        body: JSON.stringify({
          spotifyId: track.id,
          title: track.name,
          artist: track.artist,
          album: track.album,
          genre: "Unknown",
          durationMs: track.durationMs || 0, // Ensure durationMs is included
          spotifyUrl: track.spotifyUrl
        })
      })
      .then(response => response.json())
      .then(data => {
        console.log("✅ Song saved successfully:", data);
      })
      .catch(error => {
        console.error("❌ Failed to save song:", error);
      });
    }
  },
  template: `
    <div class="container mt-4">
      <h1 class="display-4 text-center">User Playlists</h1>

      <div class="text-center">
        <button v-if="!accessToken" class="btn btn-success btn-lg" @click="authenticateWithSpotify">
          🔗 Connect to Spotify
        </button>
        <button v-else class="btn btn-danger btn-lg" @click="logoutSpotify">
          🚪 Logout from Spotify
        </button>
      </div>

      <table class="table table-striped" v-if="playlists.length > 0">
        <thead>
          <tr>
            <th>Cover</th>
            <th>Name</th>
            <th>Total Tracks</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="playlist in playlists" :key="playlist.id" @click="fetchPlaylistTracks(playlist.id)">
            <td><img :src="playlist.image" width="50"></td>
            <td>{{ playlist.name }}</td>
            <td>{{ playlist.totalTracks }}</td>
          </tr>
        </tbody>
      </table>

      <table class="table table-striped" v-if="selectedPlaylist">
        <tbody>
          <tr v-for="track in tracks" :key="track.id">
            <td>{{ track.name }} - {{ track.artist }}</td>
            <td><a :href="track.spotifyUrl" target="_blank" @click.prevent="saveSongToDatabase(track)">🎵 Play on Spotify</a></td>
          </tr>
        </tbody>
      </table>
    </div>
  `
};
