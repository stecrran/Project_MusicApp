window.MusicCharts = {
  data() {
    return {
      tracks: [],  // Store playlist tracks
      loading: true,
      error: null
    };
  },
  created() {
    console.log("✅ MusicCharts component is created!");
    this.fetchMusicCharts();
  },
  methods: {
    async fetchMusicCharts() {
      try {
        console.log("🎵 Fetching Spotify playlist data...");

        const response = await fetch("https://api.spotify.com/v1/playlists/37i9dQZEVXbMDoHDwVN2tF", {
          headers: {
            "Authorization": "Bearer BQBRBuxZXiVgg6o4JrNNoat9JUru4ToScSk3JmVfl6CcSiaDHNDHGmqeati1pxg8C80BhPZYhUUwThOYkw9-oRvHJpsZHq8oGJerK67TSXwglTZTbw0zY8ibVWkx_A8_wS3uaZAzr7E",
            "Content-Type": "application/json"
          }
        });

        console.log("📡 Response Status:", response.status);

        if (!response.ok) {
          throw new Error(`Error ${response.status}: ${response.statusText}`);
        }

        const data = await response.json();
        console.log("✅ API Response Data:", data);

        if (!data.tracks || !data.tracks.items) {
          throw new Error("Unexpected API response structure: Missing tracks data");
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
        console.error("❌ Error fetching music charts:", err);
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
