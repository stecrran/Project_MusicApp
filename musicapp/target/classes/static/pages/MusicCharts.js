window.MusicCharts = {
  data() {
    return {
      tracks: [],
      loading: true,
      error: null,
      accessToken: null
    };
  },
  created() {
    console.log("✅ MusicCharts component is created!");

    // Check if we have a stored token
    const storedToken = localStorage.getItem("spotify_token");
    const tokenExpiry = localStorage.getItem("spotify_token_expiry");

    if (storedToken && tokenExpiry && Date.now() < parseInt(tokenExpiry)) {
      console.log("🔑 Using stored access token.");
      this.accessToken = storedToken;
      this.fetchMusicCharts();
      return;
    }

    // Extract token from Spotify redirect URL
    const urlParams = new URLSearchParams(window.location.hash.substring(1));
    const newToken = urlParams.get("access_token");

    if (newToken) {
      console.log("🔑 New token found in URL:", newToken);

      // Store the token and expiry in localStorage
      const expiresIn = 3600 * 1000; // 1 hour
      localStorage.setItem("spotify_token", newToken);
      localStorage.setItem("spotify_token_expiry", Date.now() + expiresIn);

      this.accessToken = newToken;
      this.fetchMusicCharts();

      // Clear the token from URL to prevent issues on reload
      window.history.replaceState({}, document.title, window.location.pathname);
    } else {
      console.log("🔑 No valid token found. Redirecting to Spotify login...");
      this.authenticateWithSpotify();
    }
  },
  methods: {
    async authenticateWithSpotify() {
      const clientId = "95ac7e92f6d2415d82a2992f37e23a5b";  // Your Spotify Client ID
      const redirectUri = encodeURIComponent("http://localhost:9091/"); // Must match Spotify Developer settings

      const authUrl = `https://accounts.spotify.com/authorize?client_id=${clientId}&response_type=token&redirect_uri=${redirectUri}&scope=user-read-private%20playlist-read-private`;

      console.log("🔑 Redirecting to Spotify login:", authUrl);
      window.location.href = authUrl;
    },

    async fetchMusicCharts() {
      if (!this.accessToken) {
        this.error = "No valid Spotify access token.";
        return;
      }

      try {
        console.log("🎵 Fetching Spotify playlist data...");
        console.log("🔑 Using Access Token:", this.accessToken);

        const response = await fetch("https://api.spotify.com/v1/playlists/3cEYpjA9oz9GiPac4AsH4n", {
          headers: {
            "Authorization": "Bearer " + this.accessToken,
            "Content-Type": "application/json"
          }
        });

        console.log("📡 Playlist Response Status:", response.status);

        if (!response.ok) {
          throw new Error(`❌ API Request Failed: ${response.status} - ${response.statusText}`);
        }

        const data = await response.json();
        console.log("✅ API Response Data:", data);

        if (!data.tracks || !data.tracks.items) {
          throw new Error("❌ Unexpected API response structure: Missing tracks data");
        }

        this.tracks = data.tracks.items.map(item => ({
          name: item.track.name,
          artist: item.track.artists.map(a => a.name).join(", "),
          album: item.track.album.name,
          image: item.track.album.images[0]?.url || ""
        }));

        console.log("🎶 Formatted Tracks Data:", this.tracks);

      } catch (err) {
        this.error = err.message;
        console.error("❌ Error Fetching Music Charts:", err);
      } finally {
        this.loading = false;
      }
    }
  },
  template: `
    <div class="container mt-4">
      <h1 class="display-4 text-center">Top Music Charts</h1>
      <p class="lead text-center">Check out the latest trending songs and albums.</p>

      <div v-if="loading" class="text-center">
        <p>Loading music charts...</p>
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>

      <div v-else-if="error" class="alert alert-danger text-center">
        <strong>Error:</strong> {{ error }}
      </div>

      <div v-else>
        <div class="row">
          <div v-for="track in tracks" :key="track.name" class="col-md-4">
            <div class="card shadow-sm mb-3">
              <img v-if="track.image" :src="track.image" class="card-img-top" alt="Album cover">
              <div class="card-body">
                <h5 class="card-title">{{ track.name }}</h5>
                <p class="card-text"><strong>Artist:</strong> {{ track.artist }}</p>
                <p class="card-text"><strong>Album:</strong> {{ track.album }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
};
