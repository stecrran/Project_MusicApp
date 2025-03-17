// Main Vue.js application for uMusicApp
window.App = {
	data() {
		return {
			currentView: this.getInitialView(),
			showSettingsModal: false, // Controls the visibility of the user settings modal
			userRole: this.extractUserRole(),
			newUser: { // Stores new user details for registration
				username: "",
				password: "",
				roles: []
			},
			availableRoles: ["SPOTIFY_USER", "ADMIN"],
			registrationMessage: "", // Stores the registration status message
			users: [] // Stores the list of registered users (only visible to Admins)
		};
	},

	computed: {
		showNavbar() {
			return this.currentView !== "LoginPage";
		},
		// Checks if the user is authenticated (based on JWT presence in local storage)
		isAuthenticated() {
			return !!localStorage.getItem("jwt");
		},
		isAdmin() {
			return this.userRole === "ADMIN";
		},
		isSpotifyUser() {
			return this.userRole === "SPOTIFY_USER";
		}
	},

	methods: {
		// Switches the current view and updates local storage
		setView(viewName) {
			console.log(`Switching view to: ${viewName}`);
			this.currentView = viewName;
			localStorage.setItem("currentView", viewName);

			// If switching to UserPlayList, handle Spotify callback if needed
			if (viewName === "UserPlayList") {
				console.log("Ensuring Spotify callback processing...");
				this.$nextTick(() => {
					const userPlayList = this.$refs.userPlayList;
					if (userPlayList && typeof userPlayList.handleSpotifyCallback === "function") {
						userPlayList.handleSpotifyCallback();
					}
				});
			}

			// If switching to HomePage, start the carousel
			if (viewName === "HomePage") {
				this.startCarousel();
			}
		},
		
		// Extracts the user role from the stored JWT token
		extractUserRole() {
			const token = localStorage.getItem("jwt");
			if (!token) return null;
			try {
				const base64Url = token.split(".")[1];
				const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
				const jsonPayload = decodeURIComponent(
					atob(base64)
						.split("")
						.map(c => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
						.join("")
				);
				const decodedToken = JSON.parse(jsonPayload);
				const roles = decodedToken.roles || [];
				return roles.includes("ADMIN")
					? "ADMIN"
					: roles.includes("SPOTIFY_USER")
						? "SPOTIFY_USER"
						: null;
			} catch (error) {
				console.error("X Error decoding JWT:", error);
				return null;
			}
		},
		
		// Determines the initial view when the app loads
		getInitialView() {
			console.log("Checking Initial View...");
			const jwt = localStorage.getItem("jwt");
			if (!jwt) {
				console.log("No JWT found. Redirecting to LoginPage.");
				return "LoginPage";
			}
			console.log("User is logged in. Defaulting to UserPlayList.");
			return "UserPlayList";
		},
		
		logoutUser() {
			console.log("Logging out...");
			localStorage.clear();
			this.setView("LoginPage");
			window.location.reload();
		},
		
		// Fetches all users from the admin API (only if the logged-in user is an Admin)
		fetchUsers() {
		    if (!this.isAdmin) return;
		    const token = localStorage.getItem("jwt");

		    $.ajax({
		        url: "/api/admin/users",
		        type: "GET",
		        headers: { Authorization: `Bearer ${token}` },
		        success: (data) => {
		            this.users = data;
					this.$forceUpdate(); // Ensure Vue updates the UI
		        },
		        error: (xhr) => {
		            console.error("X Error loading users:", xhr.responseText);
		        }
		    });
		},
		
		// Deletes a user account (Admins cannot delete themselves)
		deleteUser(userId, username) {
		    if (username === "Admin") {
		        alert("X You cannot delete the 'Admin' user!");
		        return;
		    }
		    if (!confirm("Are you sure you want to delete this user?")) return;
		    
		    const token = localStorage.getItem("jwt");

		    $.ajax({
		        url: `/api/admin/users/${userId}`,
		        type: "DELETE",
		        headers: { Authorization: `Bearer ${token}` },
		        success: () => {
		            this.users = this.users.filter(user => user.id !== userId);
		            alert(`User '${username}' deleted successfully.`);
		        },
		        error: (xhr) => {
		            alert(`X Error deleting user: ${xhr.responseText}`);
		            console.error("X Error deleting user:", xhr.responseText);
		        }
		    });
		},
		
		// Registers a new user via API
		registerUser() {
		    const token = localStorage.getItem("jwt");
		    if (!token) {
		        alert("X Unauthorized: No token found.");
		        return;
		    }

		    $.ajax({
		        url: "/api/admin/register",
		        type: "POST",
		        contentType: "application/json",
		        headers: { Authorization: `Bearer ${token}` },
		        data: JSON.stringify({
		            username: this.newUser.username,
		            password: this.newUser.password,
		            roles: this.newUser.roles
		        }),
		        success: () => {
		            this.registrationMessage = "User registered successfully!";
		            this.newUser = { username: "", password: "", roles: [] };
		            this.fetchUsers();
		        },
		        error: (xhr) => {
		            if (xhr.status === 409) {
		                alert("X User already exists. Enter new username.");
		            } else {
		                alert(`X Registration failed: ${xhr.responseText}`);
		            }
		        }
		    });
		},
		
		// Toggle 'users' nmodal 
		toggleSettingsModal() {
			this.showSettingsModal = !this.showSettingsModal;
			if (this.showSettingsModal) {
			    this.fetchUsers();
			    this.$nextTick(() => this.$forceUpdate()); // Ensure the modal updates
			}
		},
		
		// Initializes the Bootstrap carousel for the homepage
		startCarousel() {
			console.log("Initializing Bootstrap Carousel...");
			setTimeout(() => {
				$(".carousel").carousel("cycle");
			}, 500);
		}
	},
	
	// Executes when the Vue instance is mounted
	mounted() {
		if (this.isAdmin) this.fetchUsers();
		this.startCarousel();
	},
	
	// Registers components for dynamic view rendering
	components: {
		LoginPage: window.LoginPage,
		HomePage: window.HomePage,
		MusicList: window.MusicList,
		ConcertList: window.ConcertList,
		MusicCharts: window.MusicCharts,
		UserPlayList: window.UserPlayList
	},
	
	template: `
    <div>
      <!-- Navbar -->
      <nav v-if="showNavbar" class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
          <a class="navbar-brand d-flex align-items-center" href="#" @click.prevent="setView('HomePage')">
            <img src="/assets/images/musicLogo.png" alt="Music Icon" class="logo-fixed">
          </a>
          <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
            aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
          </button>
          <div class="collapse navbar-collapse justify-content-end custom-dropdown" id="navbarNav">
            <div class="navbar-nav">
              <button class="btn btn-outline-light mx-1" @click="setView('HomePage')">🏠 Home</button>
              <button class="btn btn-outline-light mx-1" v-if="isAuthenticated" @click="setView('ConcertList')">🎤 Gigs</button>
              <button class="btn btn-outline-light mx-1" v-if="isAuthenticated" @click="setView('MusicCharts')">⭐ Charts</button>
              <button class="btn btn-outline-light mx-1" v-if="isAuthenticated" @click="setView('UserPlayList')">🎵 Spotify Connect</button>
              <button class="btn btn-outline-light mx-1" v-if="isAuthenticated" @click="setView('MusicList')">🎼 User Playlist</button>
              <button class="btn btn-outline-light mx-1" v-if="isAdmin" @click="toggleSettingsModal">⚙️ Users</button>
              <button class="btn btn-danger mx-1" v-if="isAuthenticated" @click="logoutUser">🚪 Logout</button>
            </div>
          </div>
        </div>
      </nav>

      <!-- Dynamic View Rendering -->
      <component :is="currentView" @changeView="setView"></component>

      <!-- Settings Modal -->
      <div v-if="showSettingsModal" class="modal fade show d-block" tabindex="-1">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">⚙️ User Management</h5>
              <button type="button" class="btn-close" @click="toggleSettingsModal"></button>
            </div>
            <div class="modal-body">
              <form @submit.prevent="registerUser">
                <div class="mb-3">
                  <label class="form-label">Username</label>
                  <input type="text" class="form-control" v-model="newUser.username" required>
                </div>
                <div class="mb-3">
                  <label class="form-label">Password</label>
                  <input type="password" class="form-control" v-model="newUser.password" required>
                </div>
                <div class="mb-3">
                  <label class="form-label">Select Role</label>
                  <select class="form-select" v-model="newUser.roles" multiple>
                    <option v-for="role in availableRoles" :key="role" :value="role">{{ role }}</option>
                  </select>
                </div>
                <button type="submit" class="btn btn-primary w-100">Register</button>
              </form>
              <p v-if="registrationMessage" class="mt-3">{{ registrationMessage }}</p>
              <h5 class="mt-4">📋 Existing Users</h5>
			  <ul class="list-group">
			    <li v-if="users.length === 0" class="list-group-item text-muted">
			      No users found.
			    </li>
			    <li v-for="user in users" :key="user.id" class="list-group-item d-flex justify-content-between align-items-center">
			      {{ user.username }} ({{ user.roles.join(", ") }})
			      <button class="btn btn-sm btn-danger" @click="deleteUser(user.id, user.username)">❌ Delete</button>
			    </li>
			  </ul>
            </div>
          </div>
        </div>
      </div>

      <!-- Footer -->
      <footer v-if="showNavbar" class="bg-dark text-white py-4 mt-5">
        <div class="container text-center">
          <p>Follow us on</p>
          <a href="#" class="text-white me-3"><i class="fab fa-facebook fa-lg"></i></a>
          <a href="#" class="text-white me-3"><i class="fab fa-twitter fa-lg"></i></a>
          <a href="#" class="text-white"><i class="fab fa-instagram fa-lg"></i></a>
          <p class="mt-3 mb-0">&copy; 2025 MusicApp. All rights reserved.</p>
        </div>
      </footer>
    </div>
  `
};

// Mount the Vue.js application
console.log("Mounting Vue App...");
window.appInstance = Vue.createApp(window.App).mount("#app");
