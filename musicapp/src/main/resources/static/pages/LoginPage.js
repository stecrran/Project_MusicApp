// Vue.js component for handling user authentication
window.LoginPage = {
  template: `
  <div class="login-container">
      <div class="login-card card shadow-lg p-4">
          <div class="text-center mb-4">
              <img src="assets/images/music_640.png" alt="MusicApp" class="img-fluid logo-small">
          </div>
          <h2 class="text-center text-primary mb-4">Welcome Back</h2>
          
          <form @submit.prevent="handleLogin">
              <div class="form-group">
                  <label for="username" class="font-weight-semibold">Username</label>
                  <input type="text" id="username" class="form-control rounded-pill px-3" v-model="username" required placeholder="Enter your username">
              </div>

              <div class="form-group mt-3">
                  <label for="password" class="font-weight-semibold">Password</label>
                  <input type="password" id="password" class="form-control rounded-pill px-3" v-model="password" required placeholder="Enter your password">
              </div>

              <button type="submit" class="btn btn-primary btn-block mt-4 py-2 rounded-pill w-100" :disabled="isLoading">
                  {{ isLoading ? "Logging in..." : "Login" }}
              </button>

              <div v-if="errorMessage" class="alert alert-danger mt-3 text-center">
                  <strong>Error:</strong> {{ errorMessage }}
              </div>
          </form>
      </div>
  </div>
  `,
  data() {
    return {
      username: "",
      password: "",
      isLoading: false, // Indicates whether login is in progress
      errorMessage: ""
    };
  },
  methods: {
	// Handles user login when the form is submitted
    async handleLogin() {
      this.isLoading = true;
      this.errorMessage = "";

      try {
		// Send a login request to the backend API
        const response = await fetch("/api/auth/login", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ username: this.username, password: this.password })
        });

		// If login request fails, throw an error
		if (!response.ok) {
		    let errorMessage = "Invalid username or password";
		    try {
		        const errorData = await response.json();
		        if (errorData && errorData.message) {
		            errorMessage = errorData.message;
		        }
		    } catch (e) {
		        // If response is not JSON (e.g., 500 error), use default message
		    }
		    throw new Error(errorMessage);
		}

		// Parse the successful response
        const data = await response.json();
		
		// Store authentication details in localStorage
        localStorage.setItem("jwt", data.jwt);
        localStorage.setItem("username", data.username);
        localStorage.setItem("role", data.roles.join(", "));
        localStorage.setItem("currentRole", data.roles[0]);
        localStorage.setItem("currentView", "MusicList");

        // Redirect user after successful login
        this.$emit("changeView", "MusicList"); // Notify component to change the view
        window.location.reload(); // Refresh the page to apply changes
      } catch (error) {
        this.errorMessage = `${error.message}`;
      } finally {
        this.isLoading = false;
      }
    }
  }
};
