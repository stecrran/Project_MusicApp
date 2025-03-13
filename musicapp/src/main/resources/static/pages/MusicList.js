window.MusicList = {
  data() {
    return {
      musicList: [],
      jwtToken: localStorage.getItem("jwt")
    };
  },

  mounted() {
    console.log("🔄 Fetching music data...");
    this.fetchMusic();
  },

  methods: {
    async fetchMusic() {
      try {
        if (!this.jwtToken) {
          console.error("❌ No JWT found. User not authenticated.");
          return;
        }

        const response = await fetch("http://localhost:9091/api/music", {
          method: "GET",
          headers: {
            "Authorization": `Bearer ${this.jwtToken}`,
            "Content-Type": "application/json"
          }
        });

        if (!response.ok) {
          throw new Error(`❌ API error: ${response.status}`);
        }

        const data = await response.json();
        console.log("✅ Music data received:", data);

        this.musicList = data;
        this.refreshMusicTable();

      } catch (error) {
        console.error("❌ Failed to fetch music:", error);
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
          throw new Error(`❌ Failed to remove song: ${response.status}`);
        }

        console.log("✅ Song removed:", songId);
        this.musicList = this.musicList.filter(song => song.id !== songId);
        this.refreshMusicTable();

      } catch (error) {
        console.error("❌ Error removing song:", error);
      }
    },

    refreshMusicTable() {
      this.$nextTick(() => {
        if ($.fn.DataTable.isDataTable("#musicTable")) {
          $("#musicTable").DataTable().destroy();
        }

        $("#musicTable").DataTable({
          data: this.musicList,
          columns: [
            { data: "title", title: "Title" },
            { data: "artist", title: "Artist" },
            { data: "album", title: "Album" },
            { data: "genre", title: "Genre" },
            {
              data: "id",
              title: "Actions",
              render: (data) => `<button class="btn btn-danger btn-sm remove-song-btn" data-id="${data}">🗑 Remove</button>`
            }
          ],
          paging: true,
          searching: true,
          ordering: true,
          responsive: true
        });

        $("#musicTable tbody").off("click").on("click", ".remove-song-btn", (event) => {
          const songId = $(event.currentTarget).data("id");
          this.removeSong(songId);
        });
      });
    }
  },

  template: `
    <div class="container mt-5">
      <h2>🎼 PlayList</h2>

      <!-- Music Table -->
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
      </table>
    </div>
  `
};
