window.UserPlayList = {
  data() {
    return {
      playlists: [],
      tracks: [],
      selectedPlaylist: null,
      loading: true,
      error: null,
      accessToken: null
    };
  },
  created() {
    console.log("✅ UserPlayList component is created!");

    this.getSpotifyToken();
  },
  methods: {
    getSpotifyToken() {
      const storedToken = localStorage.getItem("spotify_token");
      const tokenExpiry = localStorage.getItem("spotify_token_expiry");

      if (storedToken && tokenExpiry && Date.now() < parseInt(tokenExpiry)) {
        console.log("🔑 Using stored access token.");
        this.accessToken = storedToken;
        this.fetchUserPlaylists();
        return;
      }

      const urlParams = new URLSearchParams(window.location.hash.substring(1));
      const newToken = urlParams.get("access_token");

      if (newToken) {
        console.log("🔑 New token found in URL:", newToken);

        const expiresIn = 3600 * 1000; // 1 hour
        localStorage.setItem("spotify_token", newToken);
        localStorage.setItem("spotify_token_expiry", Date.now() + expiresIn);

        this.accessToken = newToken;
        this.fetchUserPlaylists();

        window.history.replaceState({}, document.title, window.location.pathname);
      } else {
        console.log("🔑 No valid token found. Redirecting to Spotify login...");
        this.authenticateWithSpotify();
      }
    },

    async authenticateWithSpotify() {
      const clientId = "95ac7e92f6d2415d82a2992f37e23a5b";  // Your Spotify Client ID
      const redirectUri = encodeURIComponent("http://localhost:9091/");

      const authUrl = `https://accounts.spotify.com/authorize?client_id=${clientId}&response_type=token&redirect_uri=${redirectUri}&scope=playlist-read-private%20playlist-read-collaborative%20user-read-private`;

      console.log("🔑 Redirecting to Spotify login:", authUrl);
      window.location.href = authUrl;
    },

    async fetchUserPlaylists() {
      if (!this.accessToken) {
        this.error = "No valid Spotify access token.";
        return;
      }

      try {
        console.log("🎵 Fetching User Playlists from Spotify...");
        console.log("🔑 Using Access Token:", this.accessToken);

        const apiUrl = "https://api.spotify.com/v1/me/playlists";

        const response = await fetch(apiUrl, {
          headers: {
            "Authorization": "Bearer " + this.accessToken,
            "Content-Type": "application/json"
          }
        });

        console.log("📡 Spotify API Response Status:", response.status);

        if (!response.ok) {
          throw new Error(`❌ API Request Failed: ${response.status} - ${response.statusText}`);
        }

        const data = await response.json();
        console.log("✅ User Playlist Data:", data);

        if (!data.items) {
          throw new Error("❌ Unexpected API response structure: Missing items data");
        }

        this.playlists = data.items.map(playlist => ({
          id: playlist.id,
          name: playlist.name,
          totalTracks: playlist.tracks.total,
          image: playlist.images.length > 0 ? playlist.images[0].url : "",
          spotifyUrl: playlist.external_urls.spotify // ✅ Added playlist link to Spotify
        }));

        console.log("🎶 Formatted Playlist Data:", this.playlists);

      } catch (err) {
        this.error = err.message;
        console.error("❌ Error Fetching User Playlist:", err);
      } finally {
        this.loading = false;
      }
    },

    async fetchPlaylistTracks(playlistId) {
      if (!this.accessToken) {
        this.error = "No valid Spotify access token.";
        return;
      }

      try {
        console.log(`🎵 Fetching Tracks for Playlist ID: ${playlistId}`);
        this.tracks = [];
        this.selectedPlaylist = this.playlists.find(p => p.id === playlistId);

        const apiUrl = `https://api.spotify.com/v1/playlists/${playlistId}/tracks`;

        const response = await fetch(apiUrl, {
          headers: {
            "Authorization": "Bearer " + this.accessToken,
            "Content-Type": "application/json"
          }
        });

        if (!response.ok) {
          throw new Error(`❌ API Request Failed: ${response.status} - ${response.statusText}`);
        }

        const data = await response.json();
        console.log("✅ Playlist Tracks Data:", data);

        this.tracks = data.items.map(item => ({
          id: item.track.id,
          name: item.track.name,
          artist: item.track.artists.map(a => a.name).join(", "),
          album: item.track.album.name,
          image: item.track.album.images[0]?.url || "/assets/images/default_album.png",
          spotifyUrl: item.track.external_urls.spotify
        }));

        console.log("🎶 Formatted Tracks Data:", this.tracks);

      } catch (err) {
        this.error = err.message;
        console.error("❌ Error Fetching Playlist Tracks:", err);
      }
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
      <p class="lead text-center">Click a playlist to view tracks.</p>

      <div v-if="loading" class="text-center">
        <p>Loading your playlists...</p>
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>

      <div v-else-if="error" class="alert alert-danger text-center">
        <strong>Error:</strong> {{ error }}
      </div>

      <div v-else-if="playlists.length === 0" class="alert alert-warning text-center">
        <p>No playlists found. Try adding some in Spotify!</p>
      </div>

      <div v-else>
        <div class="row">
          <div v-for="playlist in playlists" :key="playlist.id" class="col-md-4">
            <div class="card shadow-sm mb-3" @click="fetchPlaylistTracks(playlist.id)" style="cursor: pointer;">
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

      <div v-if="selectedPlaylist" class="mt-4">
        <h2 class="text-center">{{ selectedPlaylist.name }}</h2>
        <p class="text-center">Click on a song to play in Spotify.</p>

        <div class="row">
          <div v-for="track in tracks" :key="track.id" class="col-md-4">
            <div class="card shadow-sm mb-3">
              <img v-if="track.image" :src="track.image" class="card-img-top" alt="Track cover">
              <div class="card-body">
                <h5 class="card-title">{{ track.name }}</h5>
                <p class="card-text"><strong>Artist:</strong> {{ track.artist }}</p>
                <p class="card-text"><strong>Album:</strong> {{ track.album }}</p>
                <a :href="track.spotifyUrl" target="_blank" class="btn btn-primary">🎵 Play on Spotify</a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
};
