// Vue.js component for handling Spotify user playlists and track management
window.UserPlayList = {
	data() {
		return {
			playlists: [], // Stores the user's Spotify playlists
			tracks: [], // Stores the tracks of the selected playlist
			selectedPlaylist: null, // Stores the currently selected playlist
			selectedTrack: null, // Stores the currently selected track for playback
			selectedTrackUrl: null, // Stores the Spotify URL of the selected track
			loading: false, // Indicates whether data is being loaded
			error: null, // Stores error messages if API requests fail
			accessToken: localStorage.getItem("spotify_token") || null, // Retrieves the Spotify access token from local storage
			fallbackImage: "/assets/images/music_640.png", // Default image for playlists without covers
			spotifyUrl: "https://open.spotify.com/" // Base URL for Spotify links
		};
	},
	created() {
		this.handleSpotifyCallback();
		this.checkSpotifyAuth();
	},
	// Watches for changes in the access token and fetches playlists when available
	watch: {
		accessToken(newToken) {
			if (newToken) {
				this.fetchUserPlaylists();
			}
		}
	},

	// Runs after the component is mounted
	mounted() {
		// Ensure proper modal behavior when opened or closed
	  $('#playModal').on('hidden.bs.modal', function() {
	    $(this).attr("aria-hidden", "true").attr("inert", ""); // Make modal non-interactive

	    // Move focus back to the last active element (e.g., Playlist table)
	    setTimeout(() => {
	      $('#playlistTable').focus(); // Ensure focus is on an interactive, visible element
	    }, 50);
	  });

	  $('#playModal').on('shown.bs.modal', function() {
	    $(this).removeAttr("aria-hidden").removeAttr("inert"); // Make modal interactive again

	    // Move focus to the first interactive element inside modal
	    $(this).find('.btn-close').focus();
	  });
	},
	
	methods: {
		// Checks if the Spotify authentication token is still valid
		checkSpotifyAuth() {
			const storedToken = localStorage.getItem("spotify_token");
			const tokenExpiry = localStorage.getItem("spotify_token_expiry");

			if (storedToken && tokenExpiry && Date.now() < parseInt(tokenExpiry)) {
				this.accessToken = storedToken;
				this.fetchUserPlaylists();
			} else {
				localStorage.removeItem("spotify_token");
				localStorage.removeItem("spotify_token_expiry");
				this.accessToken = null;
			}
		},

		// Redirects the user to Spotify's authentication page
		authenticateWithSpotify() {
			const clientId = "95ac7e92f6d2415d82a2992f37e23a5b"; // Spotify Client ID
			const redirectUri = encodeURIComponent(window.location.origin + window.location.pathname);
			const scope = encodeURIComponent("user-read-private playlist-read-private playlist-read-collaborative");

			const authUrl = `https://accounts.spotify.com/authorize?client_id=${clientId}&response_type=token&redirect_uri=${redirectUri}&scope=${scope}`;

			window.location.assign(authUrl);
		},

		// Handles Spotify authentication callback and extracts the access token
		handleSpotifyCallback() {
			if (window.location.hash.includes("access_token")) {
				const urlParams = new URLSearchParams(window.location.hash.substring(1));
				const newToken = urlParams.get("access_token");

				if (newToken) {
					const expiresIn = 3600 * 1000;

					localStorage.setItem("spotify_token", newToken);
					localStorage.setItem("spotify_token_expiry", Date.now() + expiresIn);

					window.history.replaceState({}, document.title, window.location.pathname);

					this.accessToken = newToken;
					this.fetchUserPlaylists();
				} else {
					console.error("No access token found in URL.");
				}
			}
		},

		// Logs out the user from Spotify by clearing stored tokens
		logoutSpotify() {
			localStorage.removeItem("spotify_token");
			localStorage.removeItem("spotify_token_expiry");

			this.accessToken = null;
			this.playlists = [];
			this.tracks = [];
			this.selectedPlaylist = null;
			this.error = null;

		},

		// Fetches the user's playlists from Spotify
		fetchUserPlaylists() {
			if (!this.accessToken) {
				this.error = "No valid Spotify access token.";
				return;
			}

			this.loading = true;

			$.ajax({
				url: "https://api.spotify.com/v1/me/playlists",
				method: "GET",
				headers: { "Authorization": `Bearer ${this.accessToken}` },
				success: (data) => {
					this.playlists = data.items.map(playlist => ({
						id: playlist.id,
						name: playlist.name,
						totalTracks: playlist.tracks.total,
						image: playlist.images?.[0]?.url || this.fallbackImage,
						spotifyUrl: playlist.external_urls.spotify
					}));

					this.$nextTick(() => {
						if ($.fn.DataTable.isDataTable("#playlistTable")) {
							$("#playlistTable").DataTable().destroy();
						}

						const table = $("#playlistTable").DataTable({
							data: this.playlists,
							columns: [
								{ data: "image", title: "Cover", render: (data) => `<img src="${data}" width="50">` },
								{ data: "name", title: "Name" },
								{ data: "totalTracks", title: "Total Tracks" }
							],
							paging: true,
							searching: true,
							ordering: true,
							responsive: true
						});

						$("#playlistTable tbody").off("click").on("click", "tr", (event) => {
							const rowData = table.row(event.currentTarget).data();
							this.fetchPlaylistTracks(rowData.id);
						});
					});
				},
				error: (xhr) => {
					console.error("Error Fetching Playlists:", xhr.responseText);
				},
				complete: () => {
					this.loading = false;
				}
			});
		},

		// Fetches tracks from the selected playlist
		fetchPlaylistTracks(playlistId) {
			if (!this.accessToken) {
				this.error = "No valid Spotify access token.";
				return;
			}

			// Update selected playlist before fetching tracks
			this.selectedPlaylist = this.playlists.find(p => p.id === playlistId);
			this.tracks = [];

			$.ajax({
				url: `https://api.spotify.com/v1/playlists/${playlistId}/tracks`,
				method: "GET",
				headers: { "Authorization": `Bearer ${this.accessToken}` },
				success: (data) => {
					console.log("Tracks Data:", data);

					this.tracks = data.items.map(item => ({
						id: item.track.id,
						name: item.track.name,
						artist: item.track.artists.map(a => a.name).join(", "),
						artistId: item.track.artists[0]?.id || null,  // Ensure artistId is stored
						album: item.track.album.name,
						image: item.track.album.images?.[0]?.url || this.fallbackImage,
						spotifyUrl: item.track.external_urls.spotify,
						durationMs: item.track.duration_ms
					}));


					this.$nextTick(() => {
						if ($.fn.DataTable.isDataTable("#trackTable")) {
							$("#trackTable").DataTable().destroy();
						}

						$("#trackTable").DataTable({
							data: this.tracks,
							columns: [
								{ data: "name", title: "Track Name" },
								{ data: "artist", title: "Artist" },
								{ data: "album", title: "Album" },
								{
									data: "spotifyUrl",
									title: "Play",
									render: (data, type, row) =>
										`<button class="btn btn-primary play-btn" data-url="${data}" 
									     data-track='${JSON.stringify(row).replace(/'/g, "&apos;").replace(/"/g, "&quot;")}'>🎵 Play</button>`

								}
							],
							paging: true,
							searching: true,
							ordering: true,
							responsive: true
						});

						// Bind Play Button Click Event
						$("#trackTable tbody").off("click").on("click", ".play-btn", (event) => {
							const button = $(event.currentTarget);
							const trackData = JSON.parse(button.attr("data-track"));

							console.log("Selected track:", trackData);

							this.selectedTrack = trackData;
							this.selectedTrackUrl = trackData.spotifyUrl;

							this.openPlayModal(trackData);
						});

					});

				},
				error: (xhr) => {
					console.error("Error Fetching Tracks:", xhr.responseText);
				}
			});
		},

		// Fetch genre associated with Artist
		fetchArtistGenre(artistId) {
		    if (!artistId) {
		        console.error("Missing artistId. Cannot fetch genre.");
		        return Promise.resolve("Unknown");
		    }

		    return $.ajax({
		        url: `https://api.spotify.com/v1/artists/${artistId}`,
		        type: "GET",
		        headers: {
		            "Authorization": `Bearer ${this.accessToken}`
		        }
		    }).then(response => {
		        return response.genres.length > 0 ? response.genres.join(", ") : "Unknown";
		    }).fail((xhr) => {
		        console.error("Failed to fetch artist genre:", xhr.responseText);
		        return "Unknown";
		    });
		},

		// Saves a selected track to the database with the genre
		saveSongToDatabase(track) {
		    const genrePromise = track.artistId ? this.fetchArtistGenre(track.artistId) : Promise.resolve("Unknown");

		    genrePromise.then((genre) => {
		        track.genre = genre;

		        $.ajax({
		            url: "/api/music/save",
		            type: "POST",
		            contentType: "application/json",
		            headers: {
		                "Authorization": `Bearer ${localStorage.getItem("jwt")}`
		            },
		            data: JSON.stringify({
		                spotifyId: track.id,
		                title: track.name,
		                artist: track.artist,
		                album: track.album,
		                genre: track.genre, // Now correctly includes the fetched genre
		                durationMs: track.durationMs || 0,
		                spotifyUrl: track.spotifyUrl
		            })
		        }).done((response) => {
		            console.log("Song saved successfully:", response);
		        }).fail((xhr) => {
		            console.error("Failed to save song:", xhr.responseText);
		        });
		    });
		},

		// Opens the play modal and saves the track to the database
		openPlayModal(track) {
			this.selectedTrack = track;
			this.selectedTrackUrl = track.spotifyUrl;
			$("#playModal").modal("show");
		},

		// Two purposes: Play song on Spotify, save song to Database
		playAndSaveTrack() {
			if (!this.selectedTrack) {
				console.error("No track selected.");
				return;
			}

			this.saveSongToDatabase(this.selectedTrack);
			// Close the modal before opening Spotify - using timeout instead of instant 'open Spotify url'
			$("#playModal").modal("hide");
			//window.open(this.selectedTrack.spotifyUrl, "_blank"); 

			setTimeout(() => {
				window.open(this.selectedTrack.spotifyUrl, "_blank");
			}, 300);
		}
	},
	
	template: `
  <div class="container mt-4">
	  <!-- ✅ Center-aligned Spotify logo -->
	  <img src="https://upload.wikimedia.org/wikipedia/commons/2/26/Spotify_logo_with_text.svg" 
	       width="130" 
	       class="d-block mx-auto mb-3">
	
	  <!-- ✅ Centered Title -->
	  <h3 class="text-center fw-bold fs-3">Spotify Playlists</h3>

      <div class="text-center">
          <button v-if="!accessToken" class="btn btn-success btn-lg" @click="authenticateWithSpotify">
              🔗 Connect to Spotify
          </button>
          <button v-else class="btn btn-danger btn-lg" @click="logoutSpotify">
              🚪 Logout from Spotify
          </button>
      </div>

      <table id="playlistTable" class="display table table-striped" style="width:100%" v-if="accessToken">
          <thead>
              <tr>
                  <th>Cover</th>
                  <th>Name</th>
                  <th>Total Tracks</th>
              </tr>
          </thead>
      </table>

      <table id="trackTable" class="display table table-striped" style="width:100%" v-if="selectedPlaylist && accessToken">
          <thead>
              <tr>
                  <th>Track Name</th>
                  <th>Artist</th>
                  <th>Album</th>
                  <th>Play</th>
              </tr>
          </thead>
      </table>

      <div id="playModal" class="modal fade" tabindex="-1">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">Play on Spotify</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body text-center">
              <img src="https://upload.wikimedia.org/wikipedia/commons/2/26/Spotify_logo_with_text.svg" width="120">
              <p class="mt-3">Do you want to play this song?</p>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
              <button class="btn btn-success" @click="playAndSaveTrack">Play on Spotify</button>
            </div>
          </div>
        </div>
      </div>
  </div>`
};