window.App = {
  data() {
    return {
      currentView: localStorage.getItem("jwt") ? localStorage.getItem("currentView") || "HomePage" : "LoginPage"
    };
  },
  computed: {
    showNavbar() {
      return this.currentView !== "LoginPage";  // ✅ Hide navbar on LoginPage
    },
    isAuthenticated() {
      return !!localStorage.getItem("jwt"); // ✅ Checks if user is logged in
    }
  },
  methods: {
    setView(viewName) {
      console.log(`🔄 Switching view to: ${viewName}`);
      this.currentView = viewName;
      localStorage.setItem("currentView", viewName);
      
      // ✅ Ensure callback runs when switching to UserPlayList
      if (viewName === "UserPlayList") {
        this.runSpotifyCallback();
      }
    },
    logoutUser() {
      console.log("🔴 Logging out...");
      localStorage.clear();
      this.setView("LoginPage");
      window.location.reload();
    },
    getUserPlayListComponent() {
      return this.$refs.userPlayList;
    },
    runSpotifyCallback() {
      const userPlayList = this.getUserPlayListComponent();
      if (userPlayList && typeof userPlayList.handleSpotifyCallback === "function") {
        console.log("🔄 Running handleSpotifyCallback()...");
        userPlayList.handleSpotifyCallback();
      } else {
        console.warn("⚠️ UserPlayList component not available yet. Retrying...");
        setTimeout(this.runSpotifyCallback, 500);
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
      <!-- ✅ Conditionally Render Navbar -->
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
                    
                    <!-- ✅ Logout Button (Only shown when logged in) -->
					<button v-if="isAuthenticated" class="btn btn-sm btn-secondary mx-1" @click="logoutUser">
					    Logout
                    </button>
                </div>
            </div>
        </div>
      </nav>

      <component :is="currentView" ref="userPlayList" @changeView="setView"></component>

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

// ✅ Store Vue instance globally
window.appInstance = Vue.createApp(window.App).mount("#app");

// ✅ Ensure user remains logged in
window.addEventListener("load", function () {
  if (!localStorage.getItem("jwt")) {
    window.appInstance.setView("LoginPage");
  }
});

// ✅ Ensure Spotify Callback is Processed on Page Load
window.addEventListener("load", function () {
  console.log("🔄 Page Loaded - Checking for Spotify Callback...");
  setTimeout(() => {
    const userPlayList = window.appInstance.getUserPlayListComponent();
    if (userPlayList) {
      console.log("🔄 Running handleSpotifyCallback() on page load...");
      userPlayList.handleSpotifyCallback();
    } else {
      console.warn("⚠️ UserPlayList not ready. Retrying...");
      setTimeout(() => {
        const retryPlayList = window.appInstance.getUserPlayListComponent();
        if (retryPlayList) {
          retryPlayList.handleSpotifyCallback();
        } else {
          console.error("❌ UserPlayList component is still not available.");
        }
      }, 500);
    }
  }, 500);
});
