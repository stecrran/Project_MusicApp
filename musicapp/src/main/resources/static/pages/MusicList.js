window.MusicList = {
	data() {
		return {
			musicList: [],
			genreData: {},
			genreChart: null,
			genreUserData: {}, 
			genreUserChart: null, 
			jwtToken: localStorage.getItem("jwt"),
			showGenreChart: false,
			showUserChart: false,
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
					    this.musicList = data.map(song => ({
					        ...song,
					        users: song.users || [] // ✅ Ensure users array exists
					    }));
					    this.refreshMusicTable();
					},
				error: (xhr) => {
					console.error("❌ Error fetching music:", xhr.responseText);
				}
			});
		},

		// ✅ Fetch Genre Distribution Data
		fetchGenreCount() {
			if (!this.showGenreChart) return;

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

		// ✅ Render Genre Distribution Chart
		renderGenreChart() {
		  if (!this.showGenreChart) return; // ✅ Do not render if chart is hidden

		  this.$nextTick(() => {
		    const canvas = document.getElementById("genreChart");
		    if (!canvas) {
		      return;
		    }

		    const ctx = canvas.getContext("2d");

		    // Destroy previous chart if it exists
		    if (this.genreChart) {
		      this.genreChart.destroy();
		    }

		    if (Object.keys(this.genreData).length === 0) {
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
		
			toggleGenreChart() {
			  this.showGenreChart = !this.showGenreChart;

			  if (this.showGenreChart) {
			    this.$nextTick(() => {
			      this.fetchGenreCount(); // ✅ Re-fetch data **after** Vue updates the DOM
			    });
			  } else {
			    this.clearGenreChart(); // ✅ Clear chart when hiding
			  }
			},

		clearGenreChart() {
			if (this.genreChart) {
				this.genreChart.destroy();
				this.genreChart = null;
			}
		},
		
		// ✅ Fetch Genre vs. Users Data
		fetchGenreUserCount() {

		    $.ajax({
		        url: "http://localhost:9091/api/music/genre-users",
		        type: "GET",
		        headers: {
		            Authorization: `Bearer ${this.jwtToken}`
		        },
		        success: (data) => {

		            if (Object.keys(data).length === 0) {
		                return;
		            }

		            this.genreUserData = data;
		            this.renderGenreUserChart();
		        },
		        error: (xhr) => {
		            console.error("❌ Error fetching genre-user count:", xhr.responseText);
		        }
		    });
		},

		// ✅ Render Genre vs. Users Chart
		renderGenreUserChart() {
		    this.$nextTick(() => {
		        const canvas = document.getElementById("genreUserChart");
		        if (!canvas) {
		            return;
		        }
		
		        const ctx = canvas.getContext("2d");
		
		        // ✅ Destroy the previous chart if it exists
		        if (this.genreUserChart) {
		            this.genreUserChart.destroy();
		        }
		
		        if (!this.genreUserData || Object.keys(this.genreUserData).length === 0) {
		            return;
		        }
		
		        this.genreUserChart = new Chart(ctx, {
		            type: "bar",
		            data: {
		                labels: Object.keys(this.genreUserData),
		                datasets: [{
		                    label: "Number of Users",
		                    data: Object.values(this.genreUserData),
		                    backgroundColor: "rgba(255, 99, 132, 0.5)",
		                    borderColor: "rgba(255, 99, 132, 1)",
		                    borderWidth: 1
		                }]
		            },
		            options: {
		                responsive: true,
		                scales: {
		                    y: {
		                        beginAtZero: true,
		                        ticks: {
		                            stepSize: 1 // Ensure whole numbers
		                        }
		                    }
		                }
		            }
		        });
		    });
		},
		
			// ✅ Toggle Genre vs. Users Chart Visibility
			toggleUserChart() {
				this.showUserChart = !this.showUserChart;
				if (this.showUserChart) {
					this.fetchGenreUserCount();
				} else {
					this.clearGenreUserChart();
				}
			},

			clearGenreUserChart() {
				if (this.genreUserChart) {
					this.genreUserChart.destroy();
					this.genreUserChart = null;
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
						    data: "users",
						    title: "Users",
						    render: (data) => data ? data.join(", ") : "None" // ✅ Show users
						},
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
		  <th>Users</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody></tbody>
    </table>
	
	<!-- ✅ Button to Toggle Genre vs. Users Chart -->
	<div class="mt-4">
	    <button class="btn btn-primary" @click="toggleUserChart">
	        {{ showUserChart ? "Hide Genre vs. Users Chart" : "Show Genre vs. Users Chart" }}
	    </button>
	</div>

	    <!-- ✅ Genre vs. Users Chart Container -->
	    <div v-if="showUserChart" class="mt-3">
	        <h3>📊 Genre vs. Users</h3>
	        <canvas id="genreUserChart"></canvas>
	    </div>

	    <!-- ✅ Button to Toggle Genre Distribution Chart -->
	    <div class="mt-4">
	        <button class="btn btn-primary" @click="toggleGenreChart">
	            {{ showGenreChart ? "Hide Genre Distribution Chart" : "Show Genre Distribution Chart" }}
	        </button>
	    </div>

	    <!-- ✅ Genre Distribution Chart Container -->
	    <div v-if="showGenreChart" class="mt-3">
	        <h3>📊 Genre Distribution</h3>
	        <canvas id="genreChart"></canvas>
	    </div>
	</div>

  `
};
