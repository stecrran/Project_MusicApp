window.MusicList = {
  data: function() {
    return {
      musicList: []
    };
  },
  mounted: async function() {
    console.log("🔄 Fetching music data...");
    try {
      const data = await window.fetchMusic();
      console.log("✅ Music data received:", data);
      this.musicList = [...data]; // Ensure reactivity
    } catch (error) {
      console.error("❌ Failed to fetch music:", error);
    }
  },
  template: `
    <div class="container mt-5">
      <h2>PlayList</h2>
      <button class="btn btn-secondary mb-3" @click="$emit('changeView', 'HomePage')">🔙 Back to Home</button>
      <table id="musicTable" class="table table-striped">
        <thead>
          <tr>
            <th>Title</th>
            <th>Artist</th>
            <th>Album</th>
            <th>Genre</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="music in musicList" :key="music.id">
            <td>{{ music.title }}</td>
            <td>{{ music.artist }}</td>
            <td>{{ music.album }}</td>
            <td>{{ music.genre }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  `
};
