window.App = {
  data() {
    return {
      currentView: "HomePage"
    };
  },
  methods: {
    setView(viewName) {
      this.currentView = viewName;
    }
  },
  components: {
    HomePage: window.HomePage,
    MusicList: window.MusicList,
    ConcertList: window.ConcertList
  },
  template: `
    <div>
      <nav class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top shadow-sm">
        <div class="container">
          <a class="navbar-brand" href="#" @click.prevent="setView('HomePage')">
            <img src="/assets/images/music_640.png" alt="Music Icon" width="30" height="30" class="me-2">
            MusicApp
          </a>
          <div class="navbar-nav">
            <a class="nav-link" href="#" @click.prevent="setView('HomePage')">Home</a>
            <a class="nav-link" href="#" @click.prevent="setView('MusicList')">Music List</a>
            <a class="nav-link" href="#" @click.prevent="setView('ConcertList')">Concert List</a>
          </div>
        </div>
      </nav>
      <component :is="currentView" @changeView="setView"></component>

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
