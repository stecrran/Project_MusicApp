const API_URL = "http://localhost:9091/api/music";

window.fetchMusic = async function() {
  try {
    console.log("Fetching music data...");
    const response = await fetch(API_URL);
    if (!response.ok) {
      throw new Error(`Error: ${response.statusText}`);
    }
    const data = await response.json();
    console.log("✅ Fetched Music Data:", data);
    return data;
  } catch (error) {
    console.error("❌ Fetch Error:", error);
    return [];
  }
};

window.createMusic = async function(music) {
  try {
    console.log("Saving music data...", music);
    const response = await fetch(`${API_URL}/save`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(music),
    });

    if (!response.ok) {
      throw new Error(`Error: ${response.statusText}`);
    }

    const data = await response.json();
    console.log("✅ Music saved successfully:", data);
    return data;
  } catch (error) {
    console.error("❌ Save Error:", error);
    throw error;
  }
};
