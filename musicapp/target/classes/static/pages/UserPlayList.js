window.UserPlayList = {
  data() {
    return {
      playlists: [],
      tracks: [],
      selectedPlaylist: null,
      loading: false,
      error: null,
      accessToken: null,
      fallbackImage: "/assets/images/music_640.png"
    };
  },
  created() {
    console.log("✅ UserPlayList component is created!");
    this.checkSpotifyAuth();
  },
  mounted() {
    this.initializeDataTable("#playlistsTable");
    this.initializeDataTable("#tracksTable");
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
      console.log("🟢 Starting Spotify Authentication...");
      const clientId = "95ac7e92f6d2415d82a2992f37e23a5b";
      const redirectUri = encodeURIComponent("http://localhost:9091/");
      const scope = encodeURIComponent("user-read-private playlist-read-private playlist-read-collaborative");

      const authUrl = `https://accounts.spotify.com/authorize?client_id=${clientId}&response_type=token&redirect_uri=${redirectUri}&scope=${scope}`;

      console.log("🔑 Redirecting to Spotify login:", authUrl);
      window.location.replace(authUrl);
    },

    handleSpotifyCallback() {
      console.log("🔄 Checking for Spotify token in URL...");
      const urlParams = new URLSearchParams(window.location.hash.substring(1));
      const newToken = urlParams.get("access_token");

      if (newToken) {
        console.log("✅ New token found:", newToken);
        const expiresIn = 3600 * 1000;
        localStorage.setItem("spotify_token", newToken);
        localStorage.setItem("spotify_token_expiry", Date.now() + expiresIn);

        this.accessToken = newToken;
        console.log("✅ Token saved successfully!");

        this.fetchUserPlaylists();
        window.history.replaceState({}, document.title, window.location.pathname);
      } else {
        console.error("❌ No access token found in URL.");
      }
    },

    fetchUserPlaylists() {
      if (!this.accessToken) {
        this.error = "❌ No valid Spotify access token.";
        return;
      }

      console.log("🎵 Fetching User Playlists from Spotify...");
      this.loading = true;

      $.ajax({
        url: "https://api.spotify.com/v1/me/playlists",
        method: "GET",
        headers: {
          "Authorization": `Bearer ${this.accessToken}`
        },
        success: (data) => {
          this.playlists = data.items.map(playlist => ({
            id: playlist.id,
            name: playlist.name,
            totalTracks: playlist.tracks.total,
            image: (playlist.images && playlist.images.length > 0) ? playlist.images[0].url : this.fallbackImage,  // ✅ Fix: Check for `playlist.images` before accessing
            spotifyUrl: playlist.external_urls.spotify
          }));

          console.log("🎶 Fetched Playlists:", this.playlists);
          this.updateDataTable("#playlistsTable");
        },
        error: (xhr, status, error) => {
          this.error = `❌ API Request Failed: ${xhr.status} - ${xhr.responseText}`;
          console.error("❌ Error Fetching Playlists:", error);
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

      $.ajax({
        url: `https://api.spotify.com/v1/playlists/${playlistId}/tracks`,
        method: "GET",
        headers: {
          "Authorization": `Bearer ${this.accessToken}`
        },
        success: (data) => {
          this.tracks = data.items.map(item => ({
            id: item.track.id,
            name: item.track.name,
            artist: item.track.artists.map(a => a.name).join(", "),
            album: item.track.album.name,
            image: item.track.album.images?.[0]?.url || this.fallbackImage,  // ✅ Fix for missing images
            spotifyUrl: item.track.external_urls.spotify
          }));

          console.log("🎶 Fetched Tracks:", this.tracks);
          this.updateDataTable("#tracksTable");
        },
        error: (xhr, status, error) => {
          this.error = `❌ API Request Failed: ${xhr.status} - ${xhr.responseText}`;
          console.error("❌ Error Fetching Tracks:", error);
        }
      });
    },

    initializeDataTable(tableId) {
      $(document).ready(function () {
        $(tableId).DataTable({
          paging: true,
          searching: true,
          ordering: true,
          destroy: true
        });
      });
    },

    updateDataTable(tableId) {
      if ($.fn.DataTable.isDataTable(tableId)) {
        $(tableId).DataTable().clear().destroy();
      }
      this.$nextTick(() => {
        this.initializeDataTable(tableId);
      });
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

      <div v-if="loading" class="text-center mt-3">
        <p>Loading your playlists...</p>
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>

      <div v-else>
        <h2 class="text-center">Your Spotify Playlists</h2>
        <table id="playlistsTable" class="table table-striped">
          <thead>
            <tr>
              <th>Cover</th>
              <th>Name</th>
              <th>Total Tracks</th>
              <th>Play</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="playlist in playlists" :key="playlist.id" @click="fetchPlaylistTracks(playlist.id)" style="cursor: pointer;">
              <td><img :src="playlist.image" width="50"></td>
              <td>{{ playlist.name }}</td>
              <td>{{ playlist.totalTracks }}</td>
              <td><a :href="playlist.spotifyUrl" target="_blank" class="btn btn-success btn-sm">🎵 Open Playlist</a></td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-if="selectedPlaylist">
        <h2 class="text-center mt-4">{{ selectedPlaylist.name }} - Tracks</h2>
        <table id="tracksTable" class="table table-striped">
          <thead>
            <tr>
              <th>Cover</th>
              <th>Name</th>
              <th>Artist</th>
              <th>Album</th>
              <th>Play</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="track in tracks" :key="track.id">
              <td><img :src="track.image" width="50"></td>
              <td>{{ track.name }}</td>
              <td>{{ track.artist }}</td>
              <td>{{ track.album }}</td>
              <td><a :href="track.spotifyUrl" target="_blank" class="btn btn-primary btn-sm">🎵 Play on Spotify</a></td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  `
};
