window.MusicList = {
  data() {
    return {
      musicList: []
    };
  },
  mounted() {
    console.log("🔄 Fetching music data...");
    this.fetchMusic();
  },
  methods: {
    async fetchMusic() {
      try {
        const token = localStorage.getItem("jwt"); // ✅ Use JWT for authorization
        if (!token) {
          console.error("❌ No JWT found. User not authenticated.");
          return;
        }

        const response = await fetch("http://localhost:9091/api/music", {
          method: "GET",
          headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
          }
        });

        if (!response.ok) {
          throw new Error(`❌ API error: ${response.status}`);
        }

        const data = await response.json();
        console.log("✅ Music data received:", data);

        // ✅ Update music list
        this.musicList = data;

        // ✅ Initialize jQuery DataTable after updating data
        this.$nextTick(() => {
          $("#musicTable").DataTable({
            destroy: true, // ✅ Destroy existing table before reinitializing
            data: this.musicList,
            columns: [
              { data: "title", title: "Title" },
              { data: "artist", title: "Artist" },
              { data: "album", title: "Album" },
              { data: "genre", title: "Genre" }
            ],
            paging: true,
            searching: true,
            ordering: true,
            responsive: true
          });
        });

      } catch (error) {
        console.error("❌ Failed to fetch music:", error);
      }
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
          </tr>
        </thead>
      </table>
    </div>
  `
};
