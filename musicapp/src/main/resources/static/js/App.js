window.App = {
  data() {
    return {
      currentView: localStorage.getItem("currentView") || "HomePage"
    };
  },
  methods: {
    setView(viewName) {
      console.log(`🔄 Switching view to: ${viewName}`);
      this.currentView = viewName;
      localStorage.setItem("currentView", viewName);
    },
    getUserPlayListComponent() {
      return this.$refs.userPlayList;
    }
  },
  components: {
    HomePage: window.HomePage,
    MusicList: window.MusicList,
    ConcertList: window.ConcertList,
    MusicCharts: window.MusicCharts,
    UserPlayList: window.UserPlayList
  },
  template: `
    <div>
      <component :is="currentView" ref="userPlayList" @changeView="setView"></component>

      <footer class="bg-dark text-white py-4 mt-5">
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

// ✅ Allow navbar buttons in `index.html` to change views
window.changeView = function(viewName) {
  console.log(`🔄 Navbar clicked: Switching to ${viewName}`);
  if (window.appInstance) {
    window.appInstance.setView(viewName);
  } else {
    console.error("❌ Vue instance not found!");
  }
};

// ✅ Function to access `UserPlayList` component from console
window.getUserPlayList = function () {
  if (window.appInstance) {
    return window.appInstance.$refs.userPlayList;
  } else {
    console.error("❌ UserPlayList component not found!");
    return null;
  }
};

// ✅ Ensure Spotify Callback is Processed on Page Load
window.addEventListener("load", function () {
  if (window.getUserPlayList()) {
    console.log("🔄 Running handleSpotifyCallback() on page load...");
    window.getUserPlayList().handleSpotifyCallback();
  }
});
