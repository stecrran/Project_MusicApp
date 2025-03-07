window.MusicChartsPage = {
  template: `
    <div class="p-5 mb-4 bg-success text-white rounded-3 shadow-sm">
      <div class="container py-5">
        <h1 class="display-4">Top Music Charts</h1>
        <p class="lead">Check out the latest trending songs and albums around the world.</p>
        <button class="btn btn-light btn-lg" @click="$emit('changeView', 'HomePage')">Back to Home</button>
      </div>
    </div>
  `
};
