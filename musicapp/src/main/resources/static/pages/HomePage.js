window.HomePage = {
  data() {
    return {
      images: [],
      error: null
    };
  },
  created() {
    this.loadImages();
  },
  methods: {
    async loadImages() {
      console.log("🔄 Fetching carousel images...");

      const token = localStorage.getItem("jwt"); // ✅ Get JWT token from localStorage
      if (!token) {
        console.error("❌ No JWT token found. User not authenticated.");
        this.error = "Unauthorized. Please log in.";
        return;
      }

      try {
        const response = await fetch("http://localhost:9091/api/carousel-images", {
          method: "GET",
          headers: {
            "Authorization": `Bearer ${token}`, // ✅ Send JWT token
            "Content-Type": "application/json"
          }
        });

        if (!response.ok) {
          if (response.status === 401) {
            throw new Error("❌ Unauthorized. Please log in again.");
          }
          if (response.status === 403) {
            throw new Error("❌ Access forbidden.");
          }
          throw new Error(`❌ API error: ${response.status}`);
        }

        let images = await response.json();

        // ✅ Shuffle images randomly before assigning them
        images = this.shuffleArray(images);

        this.images = images;
        console.log("✅ Carousel images randomized & loaded:", this.images);
      } catch (error) {
        console.error("❌ Error loading images:", error);
        this.error = error.message;
      }
    },

    // 🎲 Fisher-Yates Shuffle Algorithm to Randomize Images
    shuffleArray(array) {
      for (let i = array.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [array[i], array[j]] = [array[j], array[i]];
      }
      return array;
    }
  },
  template: `
    <div class="p-5 mb-4 bg-primary text-white rounded-3 shadow-sm">
      <div class="container py-5">
        <div class="row align-items-center">
          
          <!-- Left Section: Text & Buttons -->
          <div class="col-md-6 text-center">
		  <h1 class="mb-4 fw-bold text-center" style="color: #0eed5d;">
		    Welcome to uMusicApp <br><br>
		    <span class="d-block">Discover the Best Music and Concerts</span>
		  </h1>
            
            <div class="d-flex justify-content-center gap-3">
              <button class="btn btn-light btn-lg" @click="$emit('changeView', 'ConcertList')">
                🎤 Explore Concerts
              </button>
              
              <button class="btn btn-outline-light btn-lg" @click="$emit('changeView', 'MusicCharts')">
                ⭐ View Music Charts
              </button>
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
