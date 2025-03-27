// Vue.js component for displaying and managing the user's playlist
window.MusicList = {
	data() {
		return {
			musicList: [], // Stores the fetched music list
			genreData: {}, // Stores genre distribution data
			genreChart: null, // Holds the genre chart instance
			genreUserData: {}, // Stores genre vs. users data
			genreUserChart: null, // Holds the genre-user chart instance
			jwtToken: localStorage.getItem("jwt"), // Retrieves the JWT token from local storag
			showChart: true // Controls the visibility of the genre chart
		};
	},

	mounted() {
		this.fetchMusic();
		this.fetchGenreCount();
	},

	methods: {
		// Fetches all music tracks from the backend API
		fetchMusic() {

			$.ajax({
				url: "http://localhost:9091/api/music",
				type: "GET",
				headers: {
					Authorization: `Bearer ${this.jwtToken}`
				},
				success: (data) => {
					    this.musicList = data.map(song => ({
					        ...song,
					        users: song.users || [] // Ensure users array exists
					    }));
					    this.refreshMusicTable();
					},
				error: (xhr) => {
					console.error("Error fetching music:", xhr.responseText);
				}
			});
		},

		// Fetches genre distribution data from the API
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
						this.clearGenreChart(); // Clear chart if no data
					} else {
						this.renderGenreChart(); // Render updated chart
					}
				},
				error: (xhr) => {
					console.error("Error fetching genre count:", xhr.responseText);
				}
			});
		},

		// Renders the genre distribution chart
		renderGenreChart() {
		  if (!this.showChart) return; // Do not render if chart is hidden

		  this.$nextTick(() => {
		    const canvas = document.getElementById("genreChart");
		    if (!canvas) {
		      console.warn("Chart canvas not found. Skipping chart rendering.");
		      return;
		    }

		    const ctx = canvas.getContext("2d");

		    // Destroy previous chart if it exists
		    if (this.genreChart) {
		      this.genreChart.destroy();
		    }

		    if (Object.keys(this.genreData).length === 0) {
		      console.log("No genre data. Clearing chart...");
		      this.genreChart = null; // Remove reference to prevent re-use
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

		// Clears the genre chart when there is no data
		clearGenreChart() {
			if (this.genreChart) {
				this.genreChart.destroy();
				this.genreChart = null;
			}
		},
		
		// Fetches the genre vs. users data from the API
		fetchGenreUserCount() {

		    $.ajax({
		        url: "http://localhost:9091/api/music/genre-users",
		        type: "GET",
		        headers: {
		            Authorization: `Bearer ${this.jwtToken}`
		        },
		        success: (data) => {
		            console.log("Received Genre vs. Users Data:", data);

		            if (Object.keys(data).length === 0) {
		                console.warn("No data available for Genre vs. Users.");
		                return;
		            }

		            this.genreUserData = data;
		            this.renderGenreUserChart();
		        },
		        error: (xhr) => {
		            console.error("Error fetching genre-user count:", xhr.responseText);
		        }
		    });
		},

		// Renders the Genre vs. Users chart
		renderGenreUserChart() {
		    this.$nextTick(() => {
		        const canvas = document.getElementById("genreUserChart");
		        if (!canvas) {
		            console.warn("Chart canvas not found. Skipping chart rendering.");
		            return;
		        }
		
		        const ctx = canvas.getContext("2d");
		
		        // Destroy the previous chart if it exists
		        if (this.genreUserChart) {
		            this.genreUserChart.destroy();
		        }
		
		        if (!this.genreUserData || Object.keys(this.genreUserData).length === 0) {
		            console.warn("No genre-user data available.");
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
;
		    });
		},

		// Removes a song from the database
		removeSong(songId) {
			if (!confirm("Are you sure you want to delete this song?")) {
				return;
			}


			$.ajax({
				url: `http://localhost:9091/api/music/${songId}`,
				type: "DELETE",
				headers: {
					Authorization: `Bearer ${this.jwtToken}`
				},
				success: () => {
					console.log(`Song ID: ${songId} deleted.`);
					this.musicList = this.musicList.filter(song => song.id !== songId);
					this.fetchGenreCount(); // Update chart before table refresh
					this.refreshMusicTable();
				},
				error: (xhr) => {
					console.error("Error deleting song:", xhr.responseText);
				}
			});
		},

		// Refreshes the DataTable when new music data is fetched
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
						    render: (data) => data ? data.join(", ") : "None" // Show users
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
		
		sharePlaylist() {
			if (!this.musicList.length) return alert("Your playlist is empty! Add songs to share.");
			const details = this.musicList.map(song => `${song.title} by ${song.artist} [Album: ${song.album}]`).join("\n");
			navigator.clipboard.writeText(details)
				.then(() => alert("Playlist copied to clipboard!"))
				.catch(() => alert("Couldn't copy playlist."));
		},
		
		// Toggles the visibility of the genre chart
		toggleChart() {
		  this.showChart = !this.showChart;
		  console.log("Chart visibility toggled:", this.showChart);

		  if (this.showChart) {
		    this.$nextTick(() => {
		      this.fetchGenreCount(); // Re-fetch data **after** Vue updates the DOM
		    });
		  } else {
		    this.clearGenreChart(); // Clear chart when hiding
		  }
		}

	},

	template: `
  <div class="container mt-5">
    <h2>🎼 PlayList</h2>
	<div class="d-flex justify-content-end mb-2">
	  <button class="btn btn-success" @click="sharePlaylist">🔗 Share Playlist</button>
	</div>
	
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
	
	<!-- Button to Fetch & Render Chart -->
	<div class="mt-4">
	    <button class="btn btn-primary" @click="fetchGenreUserCount">
	        Show Genre vs. Users Chart
	    </button>
	</div>

	<!-- Chart Container -->
	<div class="mt-3">
	    <h3>📊 Genre vs. Users</h3>
	    <canvas id="genreUserChart"></canvas>
	</div>


	
	<!-- Chart Toggle Button -->
	<div class="mt-4">
	  <button class="btn btn-primary" @click="toggleChart">
	    {{ showChart ? "Hide Chart" : "Show Chart" }}
	  </button>
	</div>

    <!-- Chart Section -->
    <div v-if="showChart" class="mt-3">
      <h3>📊 Genre Distribution</h3>
      <canvas id="genreChart"></canvas>
    </div>
  </div>
  `
};
