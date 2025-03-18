// HomePage.js - Vue.js component for the front page of uMusicApp
window.HomePage = {
  data() {
    return {
      images: [], // Stores the list of images for the carousel
      error: null // Stores error messages if API requests fail
    };
  },
  // Automatically loads images when the component is created
  created() {
    this.loadImages();
  },
  methods: {
    loadImages() {
      const token = localStorage.getItem("jwt"); // Get JWT token from localStorage
      if (!token) {
        console.error("No JWT token found. User not authenticated.");
        this.error = "Unauthorized. Please log in.";
        return;
      }

      // Use jQuery AJAX to fetch images from the backend
      $.ajax({
        url: "http://localhost:9091/api/carousel-images",
        type: "GET",
        headers: {
          "Authorization": `Bearer ${token}`
        }
      })
      .done((response) => {
        console.log("Carousel images fetched:", response);

        // Shuffle images before setting them
        this.images = this.shuffleArray(response);
      })
      .fail((xhr) => {
        let errorMessage = "API request failed.";
        if (xhr.status === 401) errorMessage = "Unauthorized. Please log in again.";
        if (xhr.status === 403) errorMessage = "Access forbidden.";
        
        console.error(errorMessage, xhr.responseText);
        this.error = errorMessage;
      });
    },
	
    // Fisher-Yates Shuffle Algorithm to Randomize Images
	// https://www.freecodecamp.org/news/how-to-shuffle-an-array-of-items-using-javascript-or-typescript/
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
