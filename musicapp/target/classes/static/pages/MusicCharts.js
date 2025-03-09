window.MusicCharts = {
  data() {
    return {
      playlistId: "37i9dQZEVXbMDoHDwVN2tF", // Spotify Global Top 50
      spotifyUrl: "https://open.spotify.com/playlist/37i9dQZEVXbMDoHDwVN2tF"
    };
  },
  template: `
    <div class="container mt-4">
      <h1 class="display-4 text-center">Top Music Charts</h1>
      <p class="lead text-center">Check out the latest trending songs and albums.</p>

      <div class="text-center mb-3">
        <a :href="spotifyUrl" target="_blank">
          <img src="https://upload.wikimedia.org/wikipedia/commons/2/26/Spotify_logo_with_text.svg" 
               alt="Spotify Logo" width="150">
        </a>
      </div>

      <div class="text-center">
        <iframe style="border-radius:12px"
          :src="'https://open.spotify.com/embed/playlist/' + playlistId"
          width="100%" height="400"
          frameborder="0" allowfullscreen=""
          allow="autoplay; clipboard-write; encrypted-media; fullscreen; picture-in-picture">
        </iframe>
      </div>
    </div>
  `
};
