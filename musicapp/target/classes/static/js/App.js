window.App = {
  data() {
    return {
      currentView: this.getInitialView(),
      showSettingsModal: false,
      userRole: this.extractUserRole(),
      newUser: {
        username: "",
        password: "",
        roles: []
      },
      availableRoles: ["SPOTIFY_USER", "ADMIN"],
      registrationMessage: ""
    };
  },
  computed: {
    showNavbar() {
      return this.currentView !== "LoginPage";
    },
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
	setView(viewName) {
	  console.log(`🔄 Switching view to: ${viewName}`);
	  this.currentView = viewName;
	  localStorage.setItem("currentView", viewName);

	  if (viewName === "UserPlayList") {
	    console.log("🔄 Ensuring Spotify callback processing...");
	    this.$nextTick(() => {
	      const userPlayList = this.$refs.userPlayList;
	      if (userPlayList && typeof userPlayList.handleSpotifyCallback === "function") {
	        userPlayList.handleSpotifyCallback();
	      }
	    });
	  }

	  // ✅ Restart carousel when switching to HomePage
	  if (viewName === "HomePage") {
	    this.startCarousel();
	  }
	},
    extractUserRole() {
      const token = localStorage.getItem("jwt");
      if (!token) return null;

      try {
        const base64Url = token.split(".")[1];
        const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
        const jsonPayload = decodeURIComponent(atob(base64).split("").map(c =>
          "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2)
        ).join(""));
        const decodedToken = JSON.parse(jsonPayload);

        if (decodedToken.roles.includes("ADMIN")) return "ADMIN";
        if (decodedToken.roles.includes("SPOTIFY_USER")) return "SPOTIFY_USER";

        return null;
      } catch (error) {
        console.error("❌ Error decoding JWT:", error);
        return null;
      }
    },
	getInitialView() {
	  console.log("📌 Checking Initial View...");
	  const jwt = localStorage.getItem("jwt");

	  if (!jwt) {
	    console.log("🔴 No JWT found. Redirecting to LoginPage.");
	    return "LoginPage";
	  }

	  console.log("✅ User is logged in. Defaulting to UserPlayList.");
	  return "UserPlayList"; // ✅ Always redirect to UserPlayList on login
	},
    logoutUser() {
      console.log("🔴 Logging out...");
      localStorage.clear();
      this.setView("LoginPage");
      window.location.reload();
    },
    toggleSettingsModal() {
      this.showSettingsModal = !this.showSettingsModal;
    },
	async registerUser() {
	  try {
	    const token = localStorage.getItem("jwt");
	    if (!token) throw new Error("Unauthorized: No token found.");

	    const response = await fetch("/api/admin/register", {
	      method: "POST",
	      headers: {
	        "Content-Type": "application/json",
	        "Authorization": `Bearer ${token}`
	      },
	      body: JSON.stringify({
	        username: this.newUser.username,
	        password: this.newUser.password,
	        roles: this.newUser.roles.length > 0 ? this.newUser.roles : ["SPOTIFY_USER"]
	      })
	    });

	    if (!response.ok) {
	      if (response.status === 409) {
	        throw new Error("User already exists. Enter new username.");
	      }
	      const errorData = await response.json().catch(() => {});
	      throw new Error(errorData?.message || "Registration failed.");
	    }

	    this.registrationMessage = "✅ User registered successfully!";
	    this.newUser = { username: "", password: "", roles: [] };
	  } catch (error) {
	    console.error("❌ Registration Error:", error);
	    this.registrationMessage = `❌ ${error.message}`;
	  }
	}
,
    startCarousel() {
      console.log("🔄 Initializing Bootstrap Carousel...");
      setTimeout(() => {
        $(".carousel").carousel("cycle");
      }, 500);
    }
  },
  mounted() {
    console.log("🎠 Initializing Home Page Carousel...");
    this.startCarousel();
  },
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
	<!-- ✅ Navbar -->
	<nav v-if="showNavbar" class="navbar navbar-expand-lg navbar-dark bg-dark">
	  <div class="container">
	    <a class="navbar-brand text-white fw-bold d-flex align-items-center" href="#" @click.prevent="setView('HomePage')">
	      <img src="/assets/images/musicLogo.png" alt="Music Icon" width="260" height="140" class="me-2">
	    </a>

	    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
	      aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
	      <span class="navbar-toggler-icon"></span>
	    </button>

	    <div class="collapse navbar-collapse justify-content-end" id="navbarNav">
	      <div class="navbar-nav">
	        <button class="btn btn-outline-light mx-1" @click="setView('HomePage')">🏠 Home</button>
	        <button class="btn btn-outline-light mx-1" @click="setView('ConcertList')">🎤 Gigs</button>
	        <button class="btn btn-outline-light mx-1" @click="setView('MusicCharts')">⭐ Charts</button> <!-- ⭐ Star Glyph -->
	        <button class="btn btn-outline-light mx-1" @click="setView('UserPlayList')">🎵 User PlayList</button>

	        <button class="btn btn-outline-light mx-1" v-if="isAdmin" @click="setView('MusicList')">🎼 PlayList</button>
	        <button class="btn btn-outline-light mx-1" v-if="isAdmin" @click="toggleSettingsModal">⚙️ Settings</button>

	        <button class="btn btn-danger mx-1" v-if="isAuthenticated" @click="logoutUser">
	          🚪 Logout
	        </button>
	      </div>
	    </div>
	  </div>
	</nav>



	<!-- ✅ Dynamic View Rendering (Ensures Buttons Work) -->
	<component :is="currentView" @changeView="setView"></component>

      <!-- ✅ Settings Modal (Admin Only) -->
      <div v-if="showSettingsModal" class="modal fade show d-block" tabindex="-1">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">⚙️ User Management</h5>
              <button type="button" class="btn-close" @click="toggleSettingsModal"></button>
            </div>
            <div class="modal-body">
              <h6>Register New User</h6>
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
                    <option v-for="role in availableRoles" :key="role" :value="role">
                      {{ role }}
                    </option>
                  </select>
             
                </div>
                <button type="submit" class="btn btn-primary w-100">Register</button>
              </form>
              <p v-if="registrationMessage" class="mt-3">{{ registrationMessage }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- ✅ Footer -->
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

// ✅ Mount Vue App
console.log("🔄 Mounting Vue App...");
window.appInstance = Vue.createApp(window.App).mount("#app");
