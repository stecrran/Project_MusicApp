window.HomePage = {
  template: `
    <div class="p-5 mb-4 bg-primary text-white rounded-3 shadow-sm">
      <div class="container py-5">
        <h1 class="display-4">Welcome to MusicApp</h1>
        <p class="lead">Discover new music, track your favorite artists, and never miss a concert.</p>
        <button class="btn btn-light btn-lg" @click="$emit('changeView', 'ConcertList')">Explore Concerts</button>
      </div>
    </div>
  `
};
