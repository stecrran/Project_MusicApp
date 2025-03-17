// ConcertList.js - Vue.js component for displaying upcoming concerts from Ticketmaster API
window.ConcertList = {
  data: function () {
    return {
      concerts: [],
      loading: true,
      error: '',
      selectedCountry: 'IE',
      countries: [
        { name: 'Ireland', code: 'IE' },
        { name: 'UK', code: 'GB' },
        { name: 'Germany', code: 'DE' },
        { name: 'Spain', code: 'ES' },
        { name: 'Netherlands', code: 'NL' },
        { name: 'Italy', code: 'IT' },
        { name: 'Norway', code: 'NO' },
        { name: 'Sweden', code: 'SE' },
        { name: 'Finland', code: 'FI' },
        { name: 'Denmark', code: 'DK' }
      ]
    };
  },
  computed: {
	// Computes the full name of the selected country for display
    selectedCountryName: function () {
      var selected = this.countries.find(c => c.code === this.selectedCountry);
      return selected ? selected.name : '';
    }
  },
  mounted: function () {
    this.fetchConcerts();
  },
  methods: {
	// Fetches concerts from Ticketmaster API based on selected country
    fetchConcerts: function () {
      var self = this;
      self.loading = true; // Show loading indicator
      self.error = ''; // Clear previous errors
      self.concerts = []; // Reset concerts list

      axios.get('https://app.ticketmaster.com/discovery/v2/events.json', {
        params: {
          countryCode: self.selectedCountry, // API parameter to filter by country
          apikey: 'FC5tck5lgQjuG3BQRByMyC4gU9TGdvz9' // Ticketmaster API key for authentication
        }
      })
      .then(function (response) {
        self.concerts = response.data._embedded?.events || [];
      })
      .catch(function (error) {
        console.error('Failed to load concerts:', error);
        self.error = 'Failed to load concerts. Please try again later.'; 
      })
      .finally(function () {
        self.loading = false; // Hide loading indicator after API call completes
      });
    },
    getImageUrl: function (concert) {
      var image = concert.images.find(function (img) {
        return img.ratio === '3_2';
      });
      return image ? image.url : '';
    },
	// Formats a date string into a readable format
    getDate: function (dateStr) {
      var date = new Date(dateStr);
      return date.toLocaleDateString(undefined, {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    },
	// Triggers concert fetching when the user selects a different count
    onCountryChange: function () {
      this.fetchConcerts();
    }
  },
  template: `
    <div class="container mt-5 position-relative">

      <div class="position-absolute top-0 end-0 mt-4 me-4 text-center" style="width: 150px;">
        <small class="text-muted d-block mb-1">Powered by:</small>
        <img 
          src="/assets/images/TicketmasterDevCapture.PNG" 
          alt="Ticketmaster Screenshot" 
          style="width: 100%; height: auto; border-radius: 8px; box-shadow: 0 2px 6px rgba(0,0,0,0.2);"
        />
      </div>

      <h2 class="text-2xl font-bold mb-4">
        Upcoming Concerts - {{ selectedCountryName }}
      </h2>

      <div class="mb-4">
        <label for="countrySelect" class="form-label">Select Country:</label>
        <select
          id="countrySelect"
          v-model="selectedCountry"
          @change="onCountryChange"
          class="form-select form-select-sm w-auto"
        >
          <option v-for="country in countries" :value="country.code">
            {{ country.name }}
          </option>
        </select>
      </div>

      <div v-if="loading" class="my-4">
        <div class="progress">
          <div class="progress-bar progress-bar-striped progress-bar-animated bg-primary" style="width: 100%">
            Loading...
          </div>
        </div>
      </div>

      <div v-if="concerts.length" class="row row-cols-1 row-cols-md-3 g-4">
        <div class="col" v-for="concert in concerts" :key="concert.id">
          <div class="card h-100 shadow-sm">
            
            <!-- Clickable image with tooltip -->
            <a :href="concert.url" target="_blank" rel="noopener" :title="concert.name">
              <img :src="getImageUrl(concert)" class="card-img-top" alt="Concert Image">
            </a>
            
            <div class="card-body">
              <!-- Clickable concert name -->
              <h5 class="card-title">
                <a :href="concert.url" target="_blank" rel="noopener" class="text-decoration-none text-dark">
                  {{ concert.name }}
                </a>
              </h5>
              <p class="card-text text-muted">{{ getDate(concert.dates.start.localDate) }}</p>
              <span class="badge bg-success">Live</span>
            </div>
          </div>
        </div>
      </div>

      <div v-if="!loading && !concerts.length" class="alert alert-warning mt-4" role="alert">
        No concerts found for the selected country.
      </div>

    </div>
  `
};
