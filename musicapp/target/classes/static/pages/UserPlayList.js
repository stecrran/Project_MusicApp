window.UserPlayList = {
  data() {
    return {
      playlists: [],
      selectedPlaylist: null,
      loading: false,
      error: null,
      accessToken: null
    };
  },
  created() {
    console.log("✅ UserPlayList component is created!");
    this.checkSpotifyAuth();
  },
  methods: {
    checkSpotifyAuth() {
      const storedToken = localStorage.getItem("spotify_token");
      const tokenExpiry = localStorage.getItem("spotify_token_expiry");

      if (storedToken && tokenExpiry && Date.now() < parseInt(tokenExpiry)) {
        console.log("🔑 Using stored access token.");
        this.accessToken = storedToken;
        this.fetchUserPlaylists();
      } else {
        console.log("🔑 No valid token found. Waiting for user login.");
      }
    },

    authenticateWithSpotify() {
      console.log("🟢 Button clicked: Starting Spotify Authentication...");
      
      const clientId = "95ac7e92f6d2415d82a2992f37e23a5b"; // Your Spotify Client ID
      const redirectUri = encodeURIComponent("http://localhost:9091/");
      const scope = encodeURIComponent("user-read-private playlist-read-private playlist-read-collaborative");

      const authUrl = `https://accounts.spotify.com/authorize?client_id=${clientId}&response_type=token&redirect_uri=${redirectUri}&scope=${scope}`;

      console.log("🔑 Redirecting to Spotify login:", authUrl);
      window.location.href = authUrl;
    },

    handleSpotifyCallback() {
      console.log("🔄 Checking for Spotify token in URL...");
      const urlParams = new URLSearchParams(window.location.hash.substring(1));
      const newToken = urlParams.get("access_token");

      if (newToken) {
        console.log("✅ New token found in URL:", newToken);

        const expiresIn = 3600 * 1000; // 1 hour
        localStorage.setItem("spotify_token", newToken);
        localStorage.setItem("spotify_token_expiry", Date.now() + expiresIn);

        this.accessToken = newToken;
        console.log("✅ Token saved successfully!");

        this.fetchUserPlaylists();

        window.history.replaceState({}, document.title, window.location.pathname);
      } else {
        console.error("❌ No access token found in URL. User might have denied access.");
      }
    },

    async fetchUserPlaylists() {
      if (!this.accessToken) {
        this.error = "❌ No valid Spotify access token.";
        return;
      }

      console.log("🎵 Fetching User Playlists from Spotify...");

      try {
        this.loading = true;

        const response = await fetch("https://api.spotify.com/v1/me/playlists", {
          headers: {
            "Authorization": `Bearer ${this.accessToken}`,
            "Content-Type": "application/json"
          }
        });

        if (!response.ok) {
          const errorText = await response.text();
          console.error("❌ API Response Error:", errorText);
          throw new Error(`❌ API Request Failed: ${response.status} - ${errorText}`);
        }

        const data = await response.json();

        // ✅ Fix: Ensure images exist before accessing them
        this.playlists = data.items.map(playlist => ({
          id: playlist.id,
          name: playlist.name,
          totalTracks: playlist.tracks.total,
          image: (playlist.images && playlist.images.length > 0) ? playlist.images[0].url : "/assets/images/default_album.png",
          spotifyUrl: playlist.external_urls.spotify
        }));

        console.log("🎶 Fetched Playlists:", this.playlists);

      } catch (err) {
        this.error = err.message;
        console.error("❌ Error Fetching Playlists:", err);
      } finally {
        this.loading = false;
      }
    },

    logoutSpotify() {
      console.log("🚪 Logging out from Spotify...");
      localStorage.removeItem("spotify_token");
      localStorage.removeItem("spotify_token_expiry");
      this.accessToken = null;
      this.playlists = [];
      this.selectedPlaylist = null;
      this.error = null;
    }
  },
  template: `
    <div class="container mt-4">
      <div class="text-center mb-3">
        <a href="https://open.spotify.com/" target="_blank">
          <img src="https://upload.wikimedia.org/wikipedia/commons/2/26/Spotify_logo_with_text.svg" 
               alt="Spotify Logo" width="150">
        </a>
      </div>

      <h1 class="display-4 text-center">User Playlists</h1>
      <p class="lead text-center">Log in to view your playlists.</p>

      <div class="text-center">
        <button v-if="!accessToken" class="btn btn-success btn-lg" @click="authenticateWithSpotify">
          🔗 Connect to Spotify
        </button>
        <button v-else class="btn btn-danger btn-lg" @click="logoutSpotify">
          🚪 Logout from Spotify
        </button>
      </div>

      <div v-if="loading" class="text-center mt-3">
        <p>Loading your playlists...</p>
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>

      <div v-else-if="playlists.length > 0" class="mt-4">
        <h2 class="text-center">Your Spotify Playlists</h2>
        <div class="row">
          <div v-for="playlist in playlists" :key="playlist.id" class="col-md-4">
            <div class="card shadow-sm mb-3">
              <img v-if="playlist.image" :src="playlist.image" class="card-img-top" alt="Playlist cover">
              <div class="card-body">
                <h5 class="card-title">{{ playlist.name }}</h5>
                <p class="card-text"><strong>Total Tracks:</strong> {{ playlist.totalTracks }}</p>
                <a :href="playlist.spotifyUrl" target="_blank" class="btn btn-success">🎵 Open in Spotify</a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
};
