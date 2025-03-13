window.MusicList = {
  data() {
    return {
      musicList: [],
      jwtToken: localStorage.getItem("jwt")
    };
  },

  mounted() {
    this.fetchMusic();
  },

  methods: {
    async fetchMusic() {
      try {
        const response = await fetch('http://localhost:9091/api/music', {
          method: "GET",
          headers: {
            "Authorization": `Bearer ${this.jwtToken}`,
            "Content-Type": "application/json"
          }
        });

        if (!response.ok) {
          throw new Error(`❌ API error: ${response.status}`);
        }

        this.musicList = await response.json();
        this.refreshMusicTable();

      } catch (error) {
        console.error("❌ Error fetching music:", error);
      }
    },

    async removeSong(songId) {
      if (!confirm("Are you sure you want to delete this song?")) {
        return;
      }
      try {
        const response = await fetch(`http://localhost:9091/api/music/${songId}`, {
          method: "DELETE",
          headers: {
            "Authorization": `Bearer ${this.jwtToken}`,
            "Content-Type": "application/json"
          }
        });

        if (!response.ok) {
          throw new Error(`❌ API error: ${response.status}`);
        }

        this.musicList = this.musicList.filter(song => song.id !== songId);
        this.refreshMusicTable();

      } catch (error) {
        console.error("❌ Error deleting song:", error);
      }
    },

    refreshMusicTable() {
      this.$nextTick(() => {
        if ($.fn.DataTable.isDataTable("#musicTable")) {
          $("#musicTable").DataTable().clear().destroy();
        }

        $("#musicTable").DataTable({
          data: this.musicList,
          columns: [
            { data: "title" },
            { data: "artist" },
            { data: "album" },
            { data: "genre" },
            { 
              data: "id", 
              render: (data) => `<button class="btn btn-danger btn-sm remove-song-btn" data-id="${data}">🗑 Remove</button>`
            }
          ],
          paging: true,
          searching: true,
          ordering: true,
          responsive: true
        });

        // Attach click listeners
        $('#musicTable tbody').off("click").on('click', '.remove-song-btn', (event) => {
          const songId = $(event.currentTarget).data("id");
          this.removeSong(songId);
        });
      });
    }
  },

  template: `
    <div class="container mt-5">
      <h2>🎼 PlayList</h2>
      <table id="musicTable" class="display table table-striped" style="width:100%">
        <thead>
          <tr>
            <th>Title</th>
            <th>Artist</th>
            <th>Album</th>
            <th>Genre</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody></tbody>
      </table>
    </div>
  `
};
