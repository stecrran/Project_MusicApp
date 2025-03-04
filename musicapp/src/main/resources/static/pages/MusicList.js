// MusicList.js
window.MusicList = {
  data: function() {
    return {
      musicList: []
    };
  },
  mounted: function() {
    var self = this;
    // Assuming fetchMusic is defined globally.
    if (typeof window.fetchMusic === "function") {
      window.fetchMusic()
        .then(function(data) {
          self.musicList = data;
        })
        .catch(function(error) {
          console.error("Failed to fetch music:", error);
        });
    } else {
      console.warn("fetchMusic function is not defined.");
    }
  },
  template: `
    <div class="container mt-5">
      <h2>Music List</h2>
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
