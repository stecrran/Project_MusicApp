window.MusicList = {
	data() {
		return {
			musicList: [],
			genreData: {},
			genreChart: null,
			jwtToken: localStorage.getItem("jwt"),
			showChart: true
		};
	},

	mounted() {
		this.fetchMusic();
		this.fetchGenreCount();
	},

	methods: {
		fetchMusic() {
			console.log("🎵 Fetching music...");

			$.ajax({
				url: "http://localhost:9091/api/music",
				type: "GET",
				headers: {
					Authorization: `Bearer ${this.jwtToken}`
				},
				success: (data) => {
					this.musicList = data;
					this.refreshMusicTable();
				},
				error: (xhr) => {
					console.error("❌ Error fetching music:", xhr.responseText);
				}
			});
		},

		fetchGenreCount() {
			if (!this.showChart) return;

			$.ajax({
				url: "http://localhost:9091/api/music/genre-count",
				type: "GET",
				headers: {
					Authorization: `Bearer ${this.jwtToken}`
				},
				success: (data) => {
					this.genreData = data;

					if (Object.keys(this.genreData).length === 0) {
						this.clearGenreChart(); // ✅ Clear chart if no data
					} else {
						this.renderGenreChart(); // ✅ Render updated chart
					}
				},
				error: (xhr) => {
					console.error("❌ Error fetching genre count:", xhr.responseText);
				}
			});
		},

		renderGenreChart() {
		  if (!this.showChart) return; // ✅ Do not render if chart is hidden

		  this.$nextTick(() => {
		    const canvas = document.getElementById("genreChart");
		    if (!canvas) {
		      console.warn("⚠️ Chart canvas not found. Skipping chart rendering.");
		      return;
		    }

		    const ctx = canvas.getContext("2d");

		    // Destroy previous chart if it exists
		    if (this.genreChart) {
		      this.genreChart.destroy();
		    }

		    if (Object.keys(this.genreData).length === 0) {
		      console.log("⚠️ No genre data. Clearing chart...");
		      this.genreChart = null; // ✅ Remove reference to prevent re-use
		      return;
		    }

		    this.genreChart = new Chart(ctx, {
		      type: "bar",
		      data: {
		        labels: Object.keys(this.genreData),
		        datasets: [{
		          label: "Number of Songs",
		          data: Object.values(this.genreData),
		          backgroundColor: "rgba(54, 162, 235, 0.5)",
		          borderColor: "rgba(54, 162, 235, 1)",
		          borderWidth: 1
		        }]
		      },
		      options: {
		        responsive: true,
		        scales: {
		          y: {
		            beginAtZero: true,
		            ticks: {
		              stepSize: 1 // whole number
		            }
		          }
		        }
		      }
		    });
		  });
		},

		clearGenreChart() {
			if (this.genreChart) {
				this.genreChart.destroy();
				this.genreChart = null;
				console.log("🗑 Chart cleared.");
			}
		},

		removeSong(songId) {
			if (!confirm("Are you sure you want to delete this song?")) {
				return;
			}

			console.log(`🗑 Removing song ID: ${songId}...`);

			$.ajax({
				url: `http://localhost:9091/api/music/${songId}`,
				type: "DELETE",
				headers: {
					Authorization: `Bearer ${this.jwtToken}`
				},
				success: () => {
					console.log(`✅ Song ID: ${songId} deleted.`);
					this.musicList = this.musicList.filter(song => song.id !== songId);
					this.fetchGenreCount(); // ✅ Update chart before table refresh
					this.refreshMusicTable();
				},
				error: (xhr) => {
					console.error("❌ Error deleting song:", xhr.responseText);
				}
			});
		},

		refreshMusicTable() {
			this.$nextTick(() => {
				if ($.fn.DataTable.isDataTable("#musicTable")) {
					$("#musicTable").DataTable().clear().destroy();
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
							render: (data) =>
								`<button class="btn btn-danger btn-sm remove-song-btn" data-id="${data}">🗑 Remove</button>`
						}
					],
					paging: true,
					searching: true,
					ordering: true,
					responsive: true
				});

				// Attach click event for delete button dynamically
				$("#musicTable tbody").off("click").on("click", ".remove-song-btn", (event) => {
					const songId = $(event.currentTarget).data("id");
					this.removeSong(songId);
				});
			});
		},

		toggleChart() {
		  this.showChart = !this.showChart;
		  console.log("📊 Chart visibility toggled:", this.showChart);

		  if (this.showChart) {
		    this.$nextTick(() => {
		      this.fetchGenreCount(); // ✅ Re-fetch data **after** Vue updates the DOM
		    });
		  } else {
		    this.clearGenreChart(); // ✅ Clear chart when hiding
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
          <th>Actions</th>
        </tr>
      </thead>
      <tbody></tbody>
    </table>
	
	<!-- ✅ Chart Toggle Button -->
	<div class="mt-4">
	  <button class="btn btn-primary" @click="toggleChart">
	    {{ showChart ? "Hide Chart" : "Show Chart" }}
	  </button>
	</div>

    <!-- ✅ Chart Section -->
    <div v-if="showChart" class="mt-3">
      <h3>📊 Genre Distribution</h3>
      <canvas id="genreChart"></canvas>
    </div>
  </div>
  `
};
