window.HomePage = {
  data() {
    return {
      images: []
    };
  },
  created() {
    this.loadImages();
  },
  methods: {
    async loadImages() {
      try {
        const response = await fetch("/api/carousel-images"); // Fetch images from backend
        const data = await response.json();
        
        if (Array.isArray(data) && data.length > 0) {
          this.images = data.sort(() => Math.random() - 0.5); // Shuffle images
        } else {
          console.error("No images found in carousel folder.");
        }
      } catch (error) {
        console.error("Error loading images:", error);
      }
    }
  },
  template: `
    <div class="p-5 mb-4 bg-primary text-white rounded-3 shadow-sm">
      <div class="container py-5">
        <div class="row align-items-center">
          
          <!-- Left Section: Text & Buttons -->
          <div class="col-md-6">
            <h1 class="display-4">Welcome to MusicApp</h1>
            <p class="lead">Discover new music, track your favorite artists, and never miss a concert.</p>
            <div class="mt-4">
              <button class="btn btn-light btn-lg me-2" @click="$emit('changeView', 'ConcertList')">Explore Concerts</button>
              <button class="btn btn-outline-light btn-lg" @click="$emit('changeView', 'MusicCharts')">View Music Charts</button>
            </div>
          </div>

          <!-- Right Section: Carousel -->
          <div class="col-md-6">
            <div id="musicCarousel" class="carousel slide" data-bs-ride="carousel">
              <div class="carousel-inner">
                <div v-for="(image, index) in images" :key="index" class="carousel-item" :class="{ active: index === 0 }">
                  <img :src="image" class="d-block w-100 carousel-img" alt="Music carousel image">
                </div>
              </div>
            </div>
          </div>

        </div>
      </div>
    </div>
  `
};
