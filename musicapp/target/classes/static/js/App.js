window.App = {
  data() {
    return {
      currentView: localStorage.getItem("jwt") ? localStorage.getItem("currentView") || "HomePage" : "LoginPage",
      showSettingsModal: false,
      newUser: {
        username: "",
        password: "",
        roles: ["SPOTIFY_USER"] // Default role
      },
      availableRoles: ["ADMIN", "SPOTIFY_USER"], // ✅ Role options
      registrationMessage: ""
    };
  },
  computed: {
    showNavbar() {
      return this.currentView !== "LoginPage";
    },
    isAuthenticated() {
      return !!localStorage.getItem("jwt");
    }
  },
  methods: {
    setView(viewName) {
      console.log(`🔄 Switching view to: ${viewName}`);
      this.currentView = viewName;
      localStorage.setItem("currentView", viewName);
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

	    if (!token) {
	      throw new Error("❌ Unauthorized: No token found. Please log in first.");
	    }

	    const response = await fetch("/api/admin/register", {
	      method: "POST",
	      headers: {
	        "Content-Type": "application/json",
	        "Authorization": `Bearer ${token}` // ✅ Send JWT token in request
	      },
	      body: JSON.stringify(this.newUser)
	    });

	    if (response.status === 403) {
	      throw new Error("❌ Forbidden: You do not have permission to register users.");
	    }

	    if (response.status === 401) {
	      throw new Error("❌ Unauthorized: Your session may have expired. Please log in again.");
	    }

	    if (!response.ok) {
	      const errorData = await response.json().catch(() => {});
	      throw new Error(errorData?.message || "❌ Registration failed.");
	    }

	    this.registrationMessage = "✅ User registered successfully!";
	    this.newUser = { username: "", password: "", roles: ["SPOTIFY_USER"] };
	  } catch (error) {
	    console.error("❌ Registration Error:", error);
	    this.registrationMessage = `❌ ${error.message}`;
	  }
	}
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
      <nav v-if="showNavbar" class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top shadow-sm">
        <div class="container">
            <a class="navbar-brand text-white fw-bold" href="#" @click.prevent="setView('HomePage')">
                <img src="/assets/images/music_640.png" alt="Music Icon" width="30" height="30" class="me-2">
                MusicApp
            </a>

            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
                aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse justify-content-end" id="navbarNav">
                <div class="navbar-nav text-end">
                    <button class="btn btn-outline-info nav-btn mx-1" @click="setView('HomePage')">Home</button>
                    <button class="btn btn-outline-info nav-btn mx-1" @click="setView('MusicList')">PlayList</button>
                    <button class="btn btn-outline-info nav-btn mx-1" @click="setView('ConcertList')">Gigs</button>
                    <button class="btn btn-outline-info nav-btn mx-1" @click="setView('MusicCharts')">Charts</button>
                    <button class="btn btn-outline-info nav-btn mx-1" @click="setView('UserPlayList')">User PlayList</button>
                    
                    <!-- ✅ Settings Button -->
                    <button v-if="isAuthenticated" class="btn btn-sm btn-secondary mx-1" @click="toggleSettingsModal">
                        <i class="fas fa-cog"></i>
                    </button>

                    <!-- ✅ Logout Button -->
                    <button v-if="isAuthenticated" class="btn btn-sm btn-secondary mx-1" @click="logoutUser">
                        Logout
                    </button>
                </div>
            </div>
        </div>
      </nav>

      <component :is="currentView" ref="userPlayList" @changeView="setView"></component>

      <!-- ✅ Settings Modal -->
      <div v-if="showSettingsModal" class="modal fade show d-block" tabindex="-1">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">⚙️ Settings</h5>
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
                  <label class="form-label">Role</label>
                  <select class="form-select" v-model="newUser.roles" multiple>
                    <option v-for="role in availableRoles" :key="role" :value="role">{{ role }}</option>
                  </select>
                </div>
                <button type="submit" class="btn btn-primary w-100">Register</button>
              </form>
              <p v-if="registrationMessage" class="mt-3">{{ registrationMessage }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
};

// ✅ Store Vue instance globally
window.appInstance = Vue.createApp(window.App).mount("#app");
